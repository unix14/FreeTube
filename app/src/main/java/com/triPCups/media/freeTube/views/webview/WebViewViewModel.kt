package com.triPCups.media.freeTube.views.webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebViewViewModel : ViewModel() {


    private val _currentUrl = MutableLiveData<String>()
    val currentUrlData: LiveData<String> = _currentUrl


    fun loadUrl(url: String) {
        _currentUrl.postValue(url)
    }
}