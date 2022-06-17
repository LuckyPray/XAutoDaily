package me.teble.xposed.autodaily.hook.function.proxy

import android.content.Context
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.function.impl.*
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
        SendMessageManager::class.java,
        GroupSignInManager::class.java,
        MiniProfileManager::class.java,
        PublicAccountManager::class.java,
    )

    private val functionMap = HashMap<Class<out BaseFunction>, BaseFunction>().let {
        val strategy = AndroidClassLoadingStrategy.Wrapping(
            hostContext.getDir(
                "generated",
                Context.MODE_PRIVATE
            )
        )
        for (cls in functionArray) {
            if (Modifier.toString(cls.modifiers).contains("final")) {
                throw RuntimeException("修饰符错误")
            }
            it[cls] = ByteBuddy()
                .subclass(cls)
                .method(
                    ElementMatchers.not(ElementMatchers.isDeclaredBy(Any::class.java))
                        .and(ElementMatchers.not(ElementMatchers.isOverriddenFrom(BaseFunction::class.java)))
                )
                .intercept(MethodDelegation.to(FunctionProxy()))
                .make()
                .load(cls.classLoader, strategy)
                .loaded.newInstance()
        }
        it
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseFunction> getFunction(functionClass: Class<T>) = functionMap[functionClass] as T

    val favoriteManager = getFunction(FavoriteManager::class.java)

    val ticketManager = getFunction(TicketManager::class.java)

    val friendsManager = getFunction(FriendsManager::class.java)

    val troopManager = getFunction(TroopManager::class.java)

    val miniLoginManager = getFunction(MiniLoginManager::class.java)

    val sendMessageManager = getFunction(SendMessageManager::class.java)

    val groupSignInManager = getFunction(GroupSignInManager::class.java)

    val miniProfileManager = getFunction(MiniProfileManager::class.java)

    val publicAccountManager = getFunction(PublicAccountManager::class.java)
}
