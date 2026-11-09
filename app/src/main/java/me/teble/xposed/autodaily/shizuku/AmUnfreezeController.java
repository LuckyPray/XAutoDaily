package me.teble.xposed.autodaily.shizuku;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 在 Shizuku 守护进程中执行 am 命令，成功结果仅表示系统已确认接受请求。
 */
public final class AmUnfreezeController {
    private static final int MAX_OUTPUT_CHARS = 8192;
    private static final Pattern HOST_PROCESS =
            Pattern.compile("com\\.tencent\\.(mobileqq|tim)(:MSF)?");
    private static final Pattern CONFIRMATION = Pattern.compile(
            "(?m)^Unfreezing process (\\S+) \\((\\d+)\\) sticky=(true|false)\\s*$");

    public Result unfreeze(String processName, boolean sticky) {
        return unfreeze(processName, 0, sticky);
    }

    public Result unfreeze(String processName, int expectedPid, boolean sticky) {
        if (processName == null || !HOST_PROCESS.matcher(processName).matches()) {
            return new Result(false, -1, false, 0, "", "Unsupported host process");
        }
        List<String> command = new ArrayList<>();
        command.add("/system/bin/am");
        command.add("unfreeze");
        if (sticky) command.add("--sticky");
        command.add(expectedPid > 0 ? Integer.toString(expectedPid) : processName);
        Result result = execute(command, processName, sticky, 5000);
        if (result.success && expectedPid > 0 && result.pid != expectedPid) {
            return new Result(false, result.exitCode, false, result.pid, result.output, "PID changed");
        }
        return result;
    }

    /**
     * 执行解冻命令并校验输出，不依赖 Android API，便于在 JVM 中验证超时和错误处理。
     *
     * @param command 待执行的命令及参数
     * @param processName 期望解冻的宿主进程名
     * @param sticky 是否请求持续防冻结
     * @param timeoutMs 命令执行超时时间，单位为毫秒
     * @return 命令执行结果及系统确认信息
     */
    static Result execute(List<String> command, String processName, boolean sticky, long timeoutMs) {
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
            if (timedOut) return new Result(false, exitCode, true, 0, text, "am timed out");
            if (exitCode != 0) {
                return new Result(false, exitCode, false, 0, text, "am failed");
            }
            if (reader.isAlive() || readError.get() != null) {
                return new Result(false, exitCode, false, 0, text, "Incomplete am output");
            }
            Matcher confirmation = CONFIRMATION.matcher(text);
            while (confirmation.find()) {
                if (processName.equals(confirmation.group(1))
                        && Boolean.toString(sticky).equals(confirmation.group(3))) {
                    int pid = Integer.parseInt(confirmation.group(2));
                    if (pid > 0) return new Result(true, exitCode, false, pid, text, "");
                }
            }
            return new Result(false, exitCode, false, 0, text,
                    "ROM did not confirm the requested unfreeze operation");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result(false, exitCode, timedOut, 0, output.toString(), "Interrupted");
        } catch (IOException | SecurityException | NumberFormatException e) {
            return new Result(false, exitCode, timedOut, 0, output.toString(), e.toString());
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static final class Result {
        public final boolean success;
        public final int exitCode;
        public final boolean timedOut;
        public final int pid;
        public final String output;
        public final String error;

        Result(boolean success, int exitCode, boolean timedOut, int pid,
                String output, String error) {
            this.success = success;
            this.exitCode = exitCode;
            this.timedOut = timedOut;
            this.pid = pid;
            this.output = output;
            this.error = error;
        }
    }
}
