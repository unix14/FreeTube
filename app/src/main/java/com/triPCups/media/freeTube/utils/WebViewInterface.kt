package com.triPCups.media.freeTube.utils

import android.util.Log
import android.webkit.JavascriptInterface
import com.triPCups.media.freeTube.views.webview.WebViewFragmentListener

var urlToLoad = ""

class WebAppInterface(private val listener: WebViewFragmentListener) {

    @JavascriptInterface
    fun onVideoClicked(videoUrl: String) {
        // Handle the video URL and navigate to another fragment
        Log.d("wow", "onVideoClicked: wow!!!! $videoUrl")
        if(urlToLoad == videoUrl) {
            return
        }
        urlToLoad = videoUrl
        Log.d("wow", "onVideoClicked: gone through $videoUrl")


        val videoId = YoutubeHelper.extractVideoIdFromUrl(videoUrl)
        videoId?.let {
            val second = YoutubeHelper.extractTimestampFromUrl(videoUrl) ?: -1

            Log.d("wow", "onVideoClicked: second is $second")
            listener.onVideoClicked(it)
        }
    }
}