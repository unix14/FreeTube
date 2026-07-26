package com.triPCups.media.freeTube.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class SharedPrefsManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "app_preferences441"
    }

    fun <T> saveValue(key: String, value: T) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Double -> editor.putFloat(key, value.toFloat())
            is Long -> editor.putLong(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is List<*> -> editor.putString(key, gson.toJson(value))
            is Map<*, *> -> editor.putString(key, gson.toJson(value))
            else -> throw IllegalArgumentException("Unsupported type")
        }
        editor.apply()
    }

    fun <T> loadValue(key: String, defaultValue: T): Any? {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Double -> sharedPreferences.getFloat(key, defaultValue.toFloat()).toDouble() as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is List<*> -> {
                val json = sharedPreferences.getString(key, gson.toJson(defaultValue))
                val type = object : TypeToken<List<Any>>() {}.type
                gson.fromJson(json, type) as T
            }
            is Map<*, *> -> {
                val json = sharedPreferences.getString(key, gson.toJson(defaultValue))
                val type = object : TypeToken<Map<Any, Any>>() {}.type
                gson.fromJson(json, type) as T
            }
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun <K, V> updateMap(key: String, mapKey: K, mapValue: V) {
        @Suppress("UNCHECKED_CAST")
        val currentMap: MutableMap<K, V> =
            (loadValue(key, emptyMap<K, V>()) as? Map<K, V>)?.toMutableMap() ?: mutableMapOf()
        currentMap[mapKey] = mapValue
        saveValue(key, currentMap)
    }
}
