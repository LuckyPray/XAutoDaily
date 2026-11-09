package me.teble.xposed.autodaily;
import me.teble.xposed.autodaily.IHostCallback;
import me.teble.xposed.autodaily.IHostSession;

/**
 * 面向已验证宿主进程的短时会话交接入口，不暴露守护管理接口。
 */
interface IHostBootstrap {
    /**
     * 校验协议版本和调用者身份后建立宿主会话。
     *
     * @param version 宿主使用的协议版本
     * @param callback 宿主回调接口
     * @return 仅限该宿主进程使用的会话
     */
    IHostSession connect(int version, IHostCallback callback);
}
