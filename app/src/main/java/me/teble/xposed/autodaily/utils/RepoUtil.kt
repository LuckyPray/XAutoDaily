package me.teble.xposed.autodaily.utils

import okhttp3.Request

enum class FileEnum(val path: String) {
    META("meta.json"),
    CONF_META("conf-meta.json"),
    APP_META("app-meta.json"),
    CONF("xa_conf"),
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

    private fun loadRepoFile(path: String): String {
        LogUtil.d("curr repo url: $repoUrl")
        val req = Request.Builder().url("$repoUrl/$path").build()
        return try {
            client.newCall(req).execute().body!!.string()
        } catch (e: Throwable) {
            LogUtil.e(e)
            val index = repoUrls.indexOf(repoUrl)
            if (index < repoUrls.size - 1) {
                repoUrl = repoUrls[index + 1]
                return loadRepoFile(path)
            }
            throw e
        }
    }

    fun load(fileEnum: FileEnum) {
        loadRepoFile(fileEnum.path)
    }
}
