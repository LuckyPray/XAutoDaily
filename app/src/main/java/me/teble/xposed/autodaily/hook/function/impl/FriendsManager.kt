package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.function.FunctionInitException
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.getMethods
import mqq.manager.Manager
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class FriendsManager : BaseFunction(
    TAG = "FriendsManager"
) {

    lateinit var friendsManager: Manager
    lateinit var cFriends: Class<*>

    override fun init() {
        cFriends = load("Lcom/tencent/mobileqq/data/Friends;")
            ?: throw RuntimeException("类加载失败 -> Friends")
        load("Lcom/tencent/mobileqq/app/QQManagerFactory;")?.let {
            it.fieldValue("FRIENDS_MANAGER").let {
                friendsManager = QApplicationUtil.appRuntime.getManager(it as Int)
                return
            }
        }
        var manager = QApplicationUtil.appRuntime.getManager(50)
        // 尝试获取好友
        if (checkFriendsObj(manager)) {
            friendsManager = manager
            return
        }
        manager = QApplicationUtil.appRuntime.getManager(51)
        if (checkFriendsObj(manager)) {
            friendsManager = manager
            return
        }
        throw FunctionInitException("获取好友列表出错, 类型不符 -> Lcom/tencent/mobileqq/data/Friends;")
    }

    open fun getFriends(): List<Friend>? {
        val map = getFriendsMap(friendsManager)
        val friendsList = LinkedList<Friend>()
        val fNike = cFriends.field("name")!!
        val fRemarke = cFriends.field("remark")!!
        val fUin = cFriends.field("uin")!!
        map.entries.forEach {
            val arr = it.value as ArrayList<*>
            arr.forEach {
                it?.let {
                    friendsList.add(
                        Friend(
                            fUin.get(it) as String,
                            fNike.get(it) as String,
                            fRemarke.get(it) as String?
                        )
                    )
                }
            }
        }
        return friendsList
    }

    private fun checkFriendsObj(manager: Any?): Boolean {
        if (manager == null) return false
        try {
            val map = getFriendsMap(manager)
            map.entries.forEach {
                val arr = it.value as ArrayList<*>
                arr.forEach {
                    it?.let {
                        return it::class.java == cFriends
                    }
                }
            }
        } catch (e: Exception) {
            // 找不到好友map
        }
        return checkFriendsArray(manager)
    }
    // 高版本qq判断方式
    // Map<分组名, List<好友>>
    private fun getFriendsMap(manager: Any): ConcurrentHashMap<*, *> {
        manager.getMethods().forEach {
            if (Modifier.isPrivate(it.modifiers)
                && it.returnType == ConcurrentHashMap::class.java
            ) {
                it.isAccessible = true
                return it.invoke(manager, true) as ConcurrentHashMap<*, *>
            }
        }
        throw RuntimeException("获取好友信息失败")
    }
    // 低版本QQ判断方式
    // ArrayList<好友>
    private fun checkFriendsArray(manager: Any): Boolean {
        manager.getMethods().forEach {
            if (Modifier.isPublic(it.modifiers)
                && it.returnType == ArrayList::class.java
                && it.parameterTypes.isEmpty()
            ) {
                val arr = it.invoke(friendsManager) as ArrayList<*>
                if (arr.isNotEmpty() && arr[0]::class.java == cFriends) {
                    return true
                }
            }
        }
        return false
    }

}