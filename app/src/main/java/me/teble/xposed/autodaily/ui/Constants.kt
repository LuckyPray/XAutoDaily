package me.teble.xposed.autodaily.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.data.Dependency

private val LocalFriendList = staticCompositionLocalOf {
    mutableStateListOf(*FunctionPool.friendsManager.getFriends().orEmpty().toTypedArray())
}

@OptIn(ExperimentalSerializationApi::class)
private val LocalDependencies = staticCompositionLocalOf {
    mutableStateListOf(
        *Json.decodeFromStream<List<Dependency>>(
            hostContext.assets.open("licenses.json")
        ).toTypedArray()
    )
}
private val LocalTaskGroupsState = staticCompositionLocalOf {
    mutableStateListOf(*ConfigUtil.loadSaveConf().taskGroups.toTypedArray())
}

private val LocalTaskState = staticCompositionLocalOf {
    mutableStateListOf(
        *ConfigUtil.loadSaveConf().taskGroups.map { it.tasks }.flatten().toTypedArray()
    )
}

object XAutodailyConstants {
    val FriendList: SnapshotStateList<Friend>
        @ReadOnlyComposable
        @Composable
        get() = LocalFriendList.current

    val DependencyList: SnapshotStateList<Dependency>
        @ReadOnlyComposable
        @Composable
        get() = LocalDependencies.current

    val TaskGroupsList: SnapshotStateList<TaskGroup>
        @ReadOnlyComposable
        @Composable
        get() = LocalTaskGroupsState.current

    val TaskState: SnapshotStateList<Task>
        @ReadOnlyComposable
        @Composable
        get() = LocalTaskState.current
}
