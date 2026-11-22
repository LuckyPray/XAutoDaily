package me.teble.xposed.autodaily.shizuku;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * 在 Shizuku 守护进程中执行 am 命令，成功结果仅表示命令正常返回，不保证系统实际防冻结效果。
 */
public final class AmUnfreezeController {
    private static final int MAX_OUTPUT_CHARS = 8192;
    private static final Pattern HOST_PROCESS =
            Pattern.compile("com\\.tencent\\.(mobileqq|tim)(:MSF)?");
    private static final Pattern UNKNOWN_COMMAND = Pattern.compile(
            "(?im)^\\s*Unknown command:\\s*['\"]?unfreeze['\"]?\\s*$");
    private volatile Result unavailableResult;
    private boolean commandObserved;

    /**
     * 是否已经收到系统明确不支持解冻命令的结果，不根据 Android 版本推断。
     */
    public boolean isUnavailable() {
        return unavailableResult != null;
    }

    /**
     * 返回首次确认命令缺失的实际输出，供后续检查复用，避免反复执行无效命令。
     */
    public Result getUnavailableResult() {
        return unavailableResult;
    }

    public Result unfreeze(String processName, boolean sticky) {
        return unfreeze(processName, 0, sticky);
    }

    public synchronized Result unfreeze(String processName, int expectedPid, boolean sticky) {
        if (processName == null || !HOST_PROCESS.matcher(processName).matches()) {
            return new Result(false, -1, false, "", "Unsupported host process");
        }
        if (unavailableResult != null) return unavailableResult;
        List<String> command = new ArrayList<>();
        command.add("/system/bin/am");
        command.add("unfreeze");
        if (sticky) command.add("--sticky");
        command.add(expectedPid > 0 ? Integer.toString(expectedPid) : processName);
        Result result = execute(command, 5000);
        observe(result);
        return result;
    }

    /**
     * 仅在尚无命令执行证据时缓存明确的命令缺失，保留超时等不确定结果的恢复责任。
     */
    private void observe(Result result) {
        if (result.isUnknownCommand() && !commandObserved) {
            unavailableResult = result;
        } else {
            commandObserved = true;
        }
    }

    /**
     * 根据退出码判断命令执行结果，输出仅用于诊断和识别明确的命令缺失。
     * 调用方需要校验目标进程身份，不能通过命令输出确认进程仍然存活。
     *
     * @param command 待执行的命令及参数
     * @param timeoutMs 命令执行超时时间，单位为毫秒
     * @return 命令执行结果及诊断信息
     */
    static Result execute(List<String> command, long timeoutMs) {
        StringBuffer output = new StringBuffer();
        AtomicReference<String> readError = new AtomicReference<>();
        Process process = null;
        int exitCode = -1;
        boolean timedOut = false;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            Process running = process;
            Thread reader = new Thread(() -> {
                try (InputStreamReader stream = new InputStreamReader(
                        running.getInputStream(), StandardCharsets.UTF_8)) {
                    char[] buffer = new char[1024];
                    int count;
                    while ((count = stream.read(buffer)) != -1) {
                        int remaining = MAX_OUTPUT_CHARS - output.length();
                        if (remaining > 0) output.append(buffer, 0, Math.min(count, remaining));
                    }
                } catch (IOException e) {
                    readError.set(e.toString());
                }
            }, "XA-AmUnfreezeOutput");
            reader.setDaemon(true);
            reader.start();

            // Process.waitFor(timeout, unit) 需要 API 26，此处轮询退出状态以兼容 API 24 和 25。
            long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
            while (true) {
                try {
                    exitCode = process.exitValue();
                    break;
                } catch (IllegalThreadStateException stillRunning) {
                    if (System.nanoTime() - deadline >= 0) {
                        timedOut = true;
                        process.destroy();
                        break;
                    }
                    Thread.sleep(20);
                }
            }
            reader.join(250);
            String text = output.toString();
            if (timedOut) return new Result(false, exitCode, true, text, "am timed out");
            if (exitCode != 0) {
                return new Result(false, exitCode, false, text, "am failed");
            }
            String warning = reader.isAlive() || readError.get() != null ? "Incomplete am output" : "";
            return new Result(true, exitCode, false, text, warning);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result(false, exitCode, timedOut, output.toString(), "Interrupted");
        } catch (IOException | SecurityException e) {
            return new Result(false, exitCode, timedOut, output.toString(), e.toString());
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static final class Result {
        public final boolean success;
        public final int exitCode;
        public final boolean timedOut;
        public final String output;
        public final String error;

        /**
         * 判断命令是否在分发前就被系统拒绝，不能将权限错误或超时当作命令缺失。
         */
        public boolean isUnknownCommand() {
            return !success && !timedOut && exitCode != 0 && UNKNOWN_COMMAND.matcher(output).find();
        }

        Result(boolean success, int exitCode, boolean timedOut, String output, String error) {
            this.success = success;
            this.exitCode = exitCode;
            this.timedOut = timedOut;
            this.output = output;
            this.error = error;
        }
    }
}
