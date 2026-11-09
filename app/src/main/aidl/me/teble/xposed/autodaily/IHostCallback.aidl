package me.teble.xposed.autodaily;

/**
 * 守护向宿主发送的异步通知接口，回调本身不执行 Cron 业务任务。
 */
oneway interface IHostCallback {
    /**
     * 通知宿主预唤醒已就绪，继续等待原有 Cron 检查。
     *
     * @param wakeId 唤醒请求的唯一标识
     * @param scheduledAt 预期检查的 Unix 毫秒时间戳
     */
    void onWakeReady(String wakeId, long scheduledAt);

    /**
     * 通知宿主取消指定任务。
     *
     * @param runId 任务的唯一标识
     * @param reason 取消原因
     */
    void cancelRun(String runId, String reason);

    /**
     * 通知宿主当前会话已关闭。
     *
     * @param reason 关闭原因
     */
    void onSessionClosed(String reason);
}
