package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.teble.xposed.autodaily.data.Dependency

class LicenseViewModel : ViewModel() {


    var dependencies = mutableStateListOf<Dependency>()

    @OptIn(ExperimentalSerializationApi::class)
    fun readJson(context: Context) {
        viewModelScope.launch(IO) {
            dependencies =
                Json.decodeFromStream<List<Dependency>>(context.assets.open("licenses.json"))
                    .toMutableStateList()
        }

    }
}