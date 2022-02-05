package me.teble.xposed.autodaily.task.request

import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.model.TaskRequest
import me.teble.xposed.autodaily.task.request.model.TaskResponse

interface ITaskReqUtil {

    fun create(
        task: Task,
        env: MutableMap<String, Any>
    ): List<TaskRequest>

    fun executor(
        taskRequest: TaskRequest
    ): TaskResponse
}