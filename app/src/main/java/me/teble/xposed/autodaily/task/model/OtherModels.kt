package me.teble.xposed.autodaily.task.model

import kotlinx.serialization.Serializable


@Serializable
data class TroopInfo(
    val name: String,
    val uin: String,
)

@Serializable
data class VoterInfo(
    // qq
    val uin: Long,
    // 最后点赞时间戳
    val lastTime: Int,
    // 被点赞数量
    val voteCnt: Short,
    // 今日点赞对方次数
    val todayFavorite: Short,
    // 还能点赞数量
    val availableCnt: Short,
)

@Serializable
data class Friend(
    val uin: String,
    val nike: String,
    val remark: String?,
)

@Serializable
data class MiniProfile(
    val avatar: String,
    val nick: String
)

@Serializable
data class VersionInfo(
    val appVersion: Int,
    val minAppVersion: Int,
    val updateLog: List<String>,
    val confUrl: String,
    val confVersion: Int,
    val notice: String,
)

@Serializable
data class Result(
    val code: Int,
    val data: VersionInfo
)

@Serializable
data class PackageData(
    val tags: List<String>,
    val versions: List<String>
)