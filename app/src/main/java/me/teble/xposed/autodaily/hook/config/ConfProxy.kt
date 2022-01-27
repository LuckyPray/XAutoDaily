package me.teble.xposed.autodaily.hook.config

import android.os.Parcelable
import com.tencent.mmkv.MMKV

open class ConfProxy(
    private val mmkv: MMKV?
) {

    open fun getKeys(): Array<String> {
        return mmkv?.allKeys() ?: emptyArray()
    }

    open fun contains(key: String): Boolean {
        return mmkv?.contains(key)!!
    }

    open fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mmkv?.getBoolean(key, defaultValue)!!
    }

    open fun putBoolean(key: String, value: Boolean) {
        mmkv?.putBoolean(key, value)
    }

    open fun getInt(key: String, defaultValue: Int): Int {
        return mmkv?.getInt(key, defaultValue)!!
    }

    open fun putInt(key: String, value: Int) {
        mmkv?.putInt(key, value)
    }

    open fun getLong(key: String, defaultValue: Long): Long {
        return mmkv?.getLong(key, defaultValue)!!
    }

    open fun putLong(key: String, value: Long) {
        mmkv?.putLong(key, value)
    }

    open fun getFloat(key: String, defaultValue: Float): Float {
        return mmkv?.getFloat(key, defaultValue)!!
    }

    open fun putFloat(key: String, value: Float) {
        mmkv?.putFloat(key, value)
    }

    open fun getString(key: String): String? {
        return mmkv?.getString(key, null)
    }

    open fun getString(key: String, defaultValue: String): String {
        return getString(key) ?: defaultValue
    }

    open fun putString(key: String, value: String? = null) {
        mmkv?.putString(key, value)
    }

    open fun getStringSet(key: String): Set<String>? {
        return mmkv?.getStringSet(key, null)
    }

    open fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return getStringSet(key) ?: defaultValue
    }

    open fun putStringSet(key: String, value: Set<String>? = null) {
        mmkv?.putStringSet(key, value)
    }

    open fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>): T? {
        return mmkv?.decodeParcelable(key, clazz, null)
    }

    open fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>, defaultValue: T): T {
        return getParcelable(key, clazz) ?: defaultValue
    }

    open fun <T : Parcelable> putParcelable(key: String, value: T?) {
        mmkv?.encode(key, value)
    }

    open fun remove(key: String) {
        mmkv?.removeValueForKey(key)
    }

    open fun remove(vararg keys: String) {
        mmkv?.removeValuesForKeys(keys)
    }

    open fun remove(keys: Collection<String>) {
        mmkv?.removeValuesForKeys(keys.toTypedArray())
    }

    open fun removeFromKey(prefix: String = "", contains: String = "", suffix: String = "") {
        val removeKeys = mutableSetOf<String>()
        mmkv?.all?.entries?.forEach {
            if (prefix.isNotEmpty() && it.key.startsWith(prefix)) {
                removeKeys.add(it.key)
            }
            if (suffix.isNotEmpty() && it.key.endsWith(suffix)) {
                removeKeys.add(it.key)
            }
            if (contains.isNotEmpty() && it.key.contains(contains)) {
                removeKeys.add(it.key)
            }
        }
        remove(removeKeys)
    }

    open fun clearAll() {
        mmkv?.clearAll()
        mmkv?.trim()
    }
}