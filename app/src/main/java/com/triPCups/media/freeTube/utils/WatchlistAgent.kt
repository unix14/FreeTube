package com.triPCups.media.freeTube.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.triPCups.media.freeTube.data.SharedPrefsManager
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class WatchlistAgent private constructor(private val context: Context) {

    init {
        // Initialize SharedPreferencesManager with the context
        SharedPrefsManager.initialize(context)
    }

    companion object {
        private const val KEY_WATCHED_VIDEOS = "watched_videos122"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: WatchlistAgent? = null

        // Get the singleton instance
        fun getInstance(context: Context): WatchlistAgent {
            return instance ?: synchronized(this) {
                instance ?: WatchlistAgent(context).also { instance = it }
            }
        }
    }

    // Function to save a map of watched videos and their latest watch points
    fun saveWatchedVideos(watchedVideos: Map<String, Double>) {
        SharedPrefsManager.saveValue<Map<String, Double>>(KEY_WATCHED_VIDEOS, watchedVideos)
    }

    // Function to load the map of watched videos and their latest watch points
    fun loadWatchedVideos(): Map<String, Double> {
        return SharedPrefsManager.loadValue<Map<String, Double>>(KEY_WATCHED_VIDEOS, emptyMap<String, Double>()) as MutableMap<String, Double>
    }

    // Function to add or update a single video's watch point
    fun add(videoId: String, watchPoint: Double) {
        val watchedVideos = loadWatchedVideos().toMutableMap()
        watchedVideos[videoId] = watchPoint
        saveWatchedVideos(watchedVideos)
    }

    // Function to remove a video's watch point
    fun remove(context: Context, videoId: String) {
        val watchedVideos = loadWatchedVideos().toMutableMap()
        watchedVideos.remove(videoId)
        saveWatchedVideos(watchedVideos)
    }

    // Function to update the watched videos map with new entries
    fun updateWatchSecondInVideo(videoId: String, second: Double) {
//        Log.d(TAG, "onCurrentSecond: got in2 $second")
        Log.d(TAG, "onCurrentSecond: aaa videoId check2 $second ${second::class.java.simpleName}")

        SharedPrefsManager.updateMap<String, Double>(KEY_WATCHED_VIDEOS, videoId, second)
    }

    // Function to get the current watch point for a specific video
    @Suppress("UNCHECKED_CAST")
    fun getCurrentSecondForVideo(videoId: String): Double? {

        val watchedVideos: Map<String, Double> = SharedPrefsManager.loadValue(KEY_WATCHED_VIDEOS, emptyMap<String, Double>()) as Map<String, Double>
        return watchedVideos[videoId]?.toDouble() // Ensure Double type

//        return ;
//        Log.d(TAG, "getCurrentSecondForVideo: aaa trying to think.")
//
//        val watchedVideos: Map<String, Float> = loadWatchedVideos()
//
//        Log.d(TAG, "getCurrentSecondForVideo: aaa second is ${watchedVideos[videoId]} video id $videoId watchedVideos : ${watchedVideos.values.size}")
////        if((watchedVideos[videoId]!!.ty/*) is Float)*/
//
//        var fafaf = (watchedVideos[videoId] as Float?)?.toString()?.toFloat()
//        Log.d(TAG, "getCurrentSecondForVideo: aaa fafaf is $fafaf")
//
//        return watchedVideos[videoId]
////        return watchedVideos[videoId]?.roundToLong()?.toFloat()
    }




//    // Function to add or update a single video's watch point
//    fun updateWatchSecondInVideo(videoId: String, watchPoint: Float) {
//        val watchedVideos = loadWatchedVideos().toMutableMap()
//        watchedVideos[videoId] = watchPoint
//        saveWatchedVideos(watchedVideos)
//    }
}