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
        // Only the most recently watched videos are kept, so the stored JSON (which is re-read
        // and re-written on every pause) can't grow without bound.
        private const val MAX_WATCHED_VIDEOS = 500
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
        // toMutableMap() is a LinkedHashMap, so iteration order is oldest -> most recently updated.
        val watchedVideos = loadWatchedVideos().toMutableMap()
        watchedVideos.remove(videoId) // re-insert so this video becomes the most recent entry
        watchedVideos[videoId] = second
        val overflow = watchedVideos.size - MAX_WATCHED_VIDEOS
        if (overflow > 0) {
            watchedVideos.keys.take(overflow).forEach { watchedVideos.remove(it) }
        }
        saveWatchedVideos(watchedVideos)
    }

    fun getCurrentSecondForVideo(videoId: String): Double? {
        return loadWatchedVideos()[videoId]
    }
}
