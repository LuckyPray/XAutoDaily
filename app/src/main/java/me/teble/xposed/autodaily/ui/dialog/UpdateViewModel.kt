package me.teble.xposed.autodaily.ui.dialog

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.config.GITHUB_RELEASE_URL
import me.teble.xposed.autodaily.config.PAN_URL
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.openUrl

@Stable
class UpdateViewModel : ViewModel() {
    fun updateConfirm(context: Context): (type: UpdateType) -> Unit {
        return { type ->
            when (type) {
                UpdateType.Ignore -> {
                    ConfUnit.skipUpdateVersion = "${ConfUnit.metaInfoCache?.app?.versionCode}"

                }

                UpdateType.Drive -> {

                    context.openUrl(PAN_URL)
                }

                UpdateType.Github -> {
                    context.openUrl(GITHUB_RELEASE_URL)
                }
            }
        }
    }
}