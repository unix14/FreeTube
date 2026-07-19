package com.triPCups.media.freeTube.utils

import android.content.Context
import android.util.Log
import com.triPCups.media.freeTube.data.SharedPrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistAgent @Inject constructor(
    private val prefs: SharedPrefsManager
) {
    companion object {
        private const val KEY_WATCHED_VIDEOS = "watched_videos122"
    }

    fun saveWatchedVideos(watchedVideos: Map<String, Double>) {
        prefs.saveValue<Map<String, Double>>(KEY_WATCHED_VIDEOS, watchedVideos)
    }

    @Suppress("UNCHECKED_CAST")
    fun loadWatchedVideos(): Map<String, Double> {
        return (prefs.loadValue<Map<String, Double>>(KEY_WATCHED_VIDEOS, emptyMap<String, Double>()) as? Map<String, Double>) ?: emptyMap()
    }

    fun add(videoId: String, watchPoint: Double) {
        val watchedVideos = loadWatchedVideos().toMutableMap()
        watchedVideos[videoId] = watchPoint
        saveWatchedVideos(watchedVideos)
    }

    fun remove(videoId: String) {
        val watchedVideos = loadWatchedVideos().toMutableMap()
        watchedVideos.remove(videoId)
        saveWatchedVideos(watchedVideos)
    }

    fun updateWatchSecondInVideo(videoId: String, second: Double) {
        Log.d(TAG, "updateWatchSecondInVideo: videoId=$videoId second=$second")
        prefs.updateMap<String, Double>(KEY_WATCHED_VIDEOS, videoId, second)
    }

    fun getCurrentSecondForVideo(videoId: String): Double? {
        return loadWatchedVideos()[videoId]
    }
}
