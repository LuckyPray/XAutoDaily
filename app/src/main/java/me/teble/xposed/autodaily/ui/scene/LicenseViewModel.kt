package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.teble.xposed.autodaily.data.Dependency
import me.teble.xposed.autodaily.hook.base.hostContext

class LicenseViewModel : ViewModel() {


    @OptIn(ExperimentalSerializationApi::class)
    var dependencies = mutableStateListOf<Dependency>(
        *Json.decodeFromStream<List<Dependency>>(
            hostContext.assets.open("licenses.json")
        ).toTypedArray()
    )

}