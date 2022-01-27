package me.teble.xposed.autodaily.hook.config

import android.os.Parcelable

class MockConfProxy : ConfProxy(null) {

    override fun getKeys(): Array<String> {
        return emptyArray()
    }

    override fun contains(key: String): Boolean {
        return true
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {

    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return defaultValue
    }

    override fun putInt(key: String, value: Int) {

    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return defaultValue
    }

    override fun putLong(key: String, value: Long) {

    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return defaultValue
    }

    override fun putFloat(key: String, value: Float) {

    }

    override fun getString(key: String): String {
        return ""
    }

    override fun getString(key: String, defaultValue: String): String {
        return defaultValue
    }

    override fun putString(key: String, value: String?) {
    }

    override fun getStringSet(key: String): Set<String> {
        return emptySet()
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return emptySet()
    }

    override fun putStringSet(key: String, value: Set<String>?) {
        super.putStringSet(key, value)
    }

    override fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>): T? {
        return super.getParcelable(key, clazz)
    }

    override fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>, defaultValue: T): T {
        return super.getParcelable(key, clazz, defaultValue)
    }

    override fun <T : Parcelable> putParcelable(key: String, value: T?) {

    }

    override fun remove(key: String) {

    }

    override fun remove(vararg keys: String) {

    }

    override fun remove(keys: Collection<String>) {

    }

    override fun removeFromKey(prefix: String, contains: String, suffix: String) {

    }

    override fun clearAll() {

    }
}