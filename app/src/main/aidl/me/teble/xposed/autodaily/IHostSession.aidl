package me.teble.xposed.autodaily;
import android.os.Bundle;

/**
 * 绑定到已验证宿主进程的受限会话，不提供命令执行或其他包的操作能力。
 */
interface IHostSession {
    /**
     * 通知守护宿主账号和运行环境已就绪。
     */
    void ready();

    /**
     * 为任务申请有时限的防冻结保护，并接管预唤醒阶段的保护。
     *
     * @param runId 本次任务的唯一标识
     * @return 保护申请结果，包含是否成功、是否关闭守护及错误信息
     */
    Bundle beginRun(String runId);

    /**
     * 更新任务保护心跳，续期不得超过任务的最长运行时限。
     *
     * @param runId 本次任务的唯一标识
     * @return 是否续期成功
     */
    boolean heartbeat(String runId);

    /**
     * 结束任务或跳过唤醒准备，并释放对应的保护。
     *
     * @param runId 任务或唤醒请求的唯一标识
     */
    void finishRun(String runId);

    /**
     * 同步并持久化宿主的下次 Cron 检查时间。
     *
     * @param nextAt 下次检查的 Unix 毫秒时间戳，为 0 时取消计划
     */
    void publishSchedule(long nextAt);
}
