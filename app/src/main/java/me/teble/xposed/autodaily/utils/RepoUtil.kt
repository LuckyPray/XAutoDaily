package me.teble.xposed.autodaily.utils

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import okhttp3.Request

enum class FileEnum(val path: String) {
    META("meta.json"),
    CONF_META("conf-meta.json"),
    APP_META("app-meta.json"),
    CONF("xa_conf"),
    NOTICE("notice"),
}

object RepoFileLoader {

    private const val originRepoUrl = "https://xa.luckypray.org"

    private val repoUrls = listOf(
        originRepoUrl,
        "https://xa-vercel.luckypray.org",
        "https://xa-cloudflare.luckypray.org",
        "https://cdn.jsdelivr.net/gh/LuckyPray/XAutoDaily@pages",
        "https://ghproxy.com/https://raw.githubusercontent.com/LuckyPray/XAutoDaily/pages",
    )

    private var repoUrl = originRepoUrl

    private fun loadRepoFile(path: String, verifyJson: Boolean): String? {
        LogUtil.d("curr repo url: $repoUrl")
        val url = "$repoUrl/$path"
        val req = Request.Builder().url(url).build()
        try {
            val response = client.newCall(req).execute()
            if (response.isSuccessful) {
                val content = response.body!!.string()
                if (verifyJson) {
                    val cls = content.parse<JsonElement>()::class.java
                    LogUtil.d("verify json content: $cls")
                    if (cls != JsonObject::class.java
                        && cls != JsonArray::class.java) {
                        throw IllegalArgumentException("invalid json: $content")
                    }
                }
                return content
            }
            LogUtil.w("load repo file failed: $url, code: ${response.code}, response: ${response.body}")
            return null
        } catch (e: Throwable) {
            LogUtil.e(e)
            val index = repoUrls.indexOf(repoUrl)
            if (index < repoUrls.size - 1) {
                repoUrl = repoUrls[index + 1]
                return loadRepoFile(path, verifyJson)
            }
            throw e
        }
    }

    fun load(fileEnum: FileEnum): String? {
        return runCatching {
            loadRepoFile(fileEnum.path, fileEnum.path.endsWith(".json"))
        }.getOrNull()
    }
}