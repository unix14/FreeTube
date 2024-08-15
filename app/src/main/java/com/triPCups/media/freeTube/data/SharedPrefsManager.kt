package com.triPCups.media.freeTube.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.triPCups.media.freeTube.utils.TAG

@Suppress("UNCHECKED_CAST")
object SharedPrefsManager {

    private const val PREF_NAME = "app_preferences441"
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    // Generic function to save a value
    fun <T> saveValue(key: String, value: T) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
//            is Double -> editor.putFloat(key, value.toFloat()) // Save as Float
            is Long -> editor.putLong(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is List<*> -> editor.putString(key, gson.toJson(value))
            is Map<*, *> -> editor.putString(key, gson.toJson(value))
            else -> throw IllegalArgumentException("Unsupported type")
        }
        editor.apply()
    }

    // Generic function to load a value
    fun <T> loadValue(key: String, defaultValue: T): Any? {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
//            is Double -> sharedPreferences.getFloat(key, defaultValue.toFloat())  as T
//            is Double -> sharedPreferences.getFloat(key, defaultValue.toFloat()) as Float // Convert Float to Double
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

    // Function to update a map in SharedPreferences with a new value for a specific key
    fun <K, V> updateMap(key: String, mapKey: K, mapValue: V) {
        // Load the current map or create an empty map if it doesn't exist
        val currentMap: MutableMap<K, V> = loadValue<Map<String, Float>>(key, emptyMap<String, Float>()) as MutableMap<K, V>

        Log.d(TAG, "onCurrentSecond: aaa videoId check1 ${currentMap[mapKey]} ${(currentMap[mapKey] ?: Intent())::class.java.simpleName}")
        Log.d(TAG, "onCurrentSecond: aaa videoId check11 ${mapValue} ${(mapValue ?: Intent())::class.java.simpleName}")


        Log.d(TAG, "updateMap: currentMap $currentMap currentMap.size : ${currentMap.size}")

//        if(currentMap[mapKey] != null) {
//            currentMap.replace(mapKey, mapValue)
//        } else {
//            currentMap[mapKey] = mapValue // Update the map with new entry
////            currentMap[mapKey] = mapValue
//        }

        // Handle potential type inconsistencies
        if (mapValue is Double) {
            // Convert Double to Float if needed
            @Suppress("UNCHECKED_CAST")
            currentMap[mapKey] = (mapValue as Double).toString().toFloat() as V
        } else {
            currentMap[mapKey] = mapValue
        }


        saveValue(key, currentMap) // Save the updated map back to SharedPreferences

        Log.d(TAG, "updateMap: mapKey $mapKey mapValue : $mapValue")
    }
}