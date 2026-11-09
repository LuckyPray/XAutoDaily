package me.teble.xposed.autodaily;

import android.os.Bundle;
import me.teble.xposed.autodaily.IHostCallback;
import me.teble.xposed.autodaily.IHostSession;

/**
 * 模块应用使用的守护管理接口，宿主通过受限的 IHostSession 进行通信。
 */
interface IUserService {

    /**
     * 处理 Shizuku 服务定义的销毁请求，释放保护状态并退出守护进程。
     */
    void destroy() = 16777114;

    /**
     * 处理模块应用主动发起的退出请求。
     */
    void exit() = 1;

    /**
     * 检查守护是否已完成初始化且尚未停止。
     *
     * @return 守护是否具备运行条件
     */
    boolean isRunning() = 2;

    /**
     * 执行模块应用提交的命令。
     *
     * @param shell 待执行的命令
     * @return 命令的标准输出
     */
    String execShell(String shell) = 3;

    /**
     * 解冻 QQ 或 TIM 的主进程或 MSF 进程，并清除已有的持续防冻结状态。
     *
     * @param processName 宿主进程名
     * @param sticky 此入口仅允许 false，持续防冻结须通过有时限的任务会话申请
     * @return 解冻结果、错误信息及命令输出
     */
    Bundle unfreezeHostProcess(String processName, boolean sticky) = 4;

    /**
     * 获取守护通信协议版本。
     *
     * @return 当前协议版本号
     */
    int getProtocolVersion() = 5;

    /**
     * 由模块应用代为建立宿主会话，守护仍会校验宿主的进程身份。
     *
     * @param packageName 宿主包名
     * @param uid 宿主 UID
     * @param pid 宿主主进程 PID
     * @param callback 宿主回调接口
     * @return 仅限该宿主进程使用的会话
     */
    IHostSession openHostSession(String packageName, int uid, int pid, IHostCallback callback) = 6;

    /**
     * 唤醒宿主并等待就绪，业务任务仍由宿主原有的 Cron 执行。
     *
     * @param packageName 宿主包名
     * @param runId 本次唤醒请求的唯一标识
     * @param scheduledAt 预期的 Cron 检查时间，使用 Unix 毫秒时间戳；为 0 时仅诊断唤醒链路
     */
    void wakeHost(String packageName, String runId, long scheduledAt) = 7;

    /**
     * 获取宿主连接、唤醒及防冻结恢复状态。
     *
     * @param packageName 宿主包名
     * @return 当前宿主状态快照
     */
    Bundle getHostStatus(String packageName) = 8;

    /**
     * 重新读取用户配置，并更新会话和任务保护。
     */
    void reloadConfig() = 9;
}
