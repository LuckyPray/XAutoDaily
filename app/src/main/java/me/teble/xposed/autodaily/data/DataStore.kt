package me.teble.xposed.autodaily.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val KeepAlive = booleanPreferencesKey("keep_alive")
val QKeepAlive = booleanPreferencesKey("q_keep_alive")
val TimKeepAlive = booleanPreferencesKey("tim_keep_alive")
val UntrustedTouchEvents = booleanPreferencesKey("untrusted_touch_events")

val HiddenAppIcon = booleanPreferencesKey("hidden_app_icon")

val BlackTheme = booleanPreferencesKey("black_theme")
val Theme = intPreferencesKey("theme")