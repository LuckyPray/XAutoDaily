package me.teble.xposed.autodaily.hook.function.proxy

import android.content.Context
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.function.BaseSendMessage
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.function.impl.FavoriteManager
import me.teble.xposed.autodaily.hook.function.impl.FriendsManager
import me.teble.xposed.autodaily.hook.function.impl.GroupSignInManager
import me.teble.xposed.autodaily.hook.function.impl.MiniLoginManager
import me.teble.xposed.autodaily.hook.function.impl.MiniProfileManager
import me.teble.xposed.autodaily.hook.function.impl.NtSendMessageManager
import me.teble.xposed.autodaily.hook.function.impl.PublicAccountManager
import me.teble.xposed.autodaily.hook.function.impl.SendMessageManager
import me.teble.xposed.autodaily.hook.function.impl.TicketManager
import me.teble.xposed.autodaily.hook.function.impl.TroopManager
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import net.bytebuddy.ByteBuddy
import net.bytebuddy.android.AndroidClassLoadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import java.lang.reflect.Modifier


object FunctionPool {

    private val functionArray: Array<Class<out BaseFunction>> = arrayOf(
        FavoriteManager::class.java,
        TicketManager::class.java,
        FriendsManager::class.java,
        TroopManager::class.java,
        MiniLoginManager::class.java,
        NtSendMessageManager::class.java,
        SendMessageManager::class.java,
        GroupSignInManager::class.java,
        MiniProfileManager::class.java,
        PublicAccountManager::class.java,
    )

    private val strategy = AndroidClassLoadingStrategy.Wrapping(
        hostContext.getDir(
            "generated",
            Context.MODE_PRIVATE
        )
    )

    private val functionMap = functionArray.associateWith { cls ->
        if (Modifier.toString(cls.modifiers).contains("final")) {
            throw RuntimeException("修饰符错误")
        }
        ByteBuddy()
            .subclass(cls)
            .method(
                ElementMatchers.not(ElementMatchers.isDeclaredBy(Any::class.java))
                    .and(ElementMatchers.not(ElementMatchers.isOverriddenFrom(BaseFunction::class.java)))
            )
            .intercept(MethodDelegation.to(FunctionProxy()))
            .make()
            .load(cls.classLoader, strategy)
            .loaded.getDeclaredConstructor().newInstance()
    }.toPersistentHashMap()

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseFunction> getFunction(functionClass: Class<T>) = functionMap[functionClass] as T

    val favoriteManager by lazy { getFunction(FavoriteManager::class.java) }

    val ticketManager by lazy { getFunction(TicketManager::class.java) }

    val friendsManager by lazy { getFunction(FriendsManager::class.java) }

    val troopManager by lazy { getFunction(TroopManager::class.java) }

    val miniLoginManager by lazy { getFunction(MiniLoginManager::class.java) }

    val sendMessageManager: BaseSendMessage by lazy {
        if (QApplicationUtil.isNtQQ()) {
            getFunction(NtSendMessageManager::class.java)
        } else {
            getFunction(SendMessageManager::class.java)
        }
    }

    val groupSignInManager by lazy { getFunction(GroupSignInManager::class.java) }

    val miniProfileManager by lazy { getFunction(MiniProfileManager::class.java) }

    val publicAccountManager by lazy { getFunction(PublicAccountManager::class.java) }
}
