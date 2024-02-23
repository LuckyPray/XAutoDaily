package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.teble.xposed.autodaily.data.Dependency

class LicenseViewModel : ViewModel() {

    private val _dependencies = MutableStateFlow<List<Dependency>>(emptyList())
    val dependencies = _dependencies.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun readJson(context: Context) {
        viewModelScope.launch(IO) {
            _dependencies.value =
                Json.decodeFromStream<List<Dependency>>(context.assets.open("licenses.json"))
                    .toList()
            Log.d("XLOG", "readJson: ${_dependencies.value}")
        }

    }
}