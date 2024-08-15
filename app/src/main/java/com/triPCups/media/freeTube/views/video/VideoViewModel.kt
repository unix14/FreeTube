package com.triPCups.media.freeTube.views.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoViewModel() : ViewModel() {

    private var _currentVideoId = MutableLiveData<String>()
    var currentVideoData: LiveData<String> = _currentVideoId


    fun setCurrentVideo(videoId: String) {
        _currentVideoId.postValue(videoId)
    }


}