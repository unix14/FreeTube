package com.triPCups.media.freeTube.views.webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor() : ViewModel() {


    private val _currentUrl = MutableLiveData<String>()
    val currentUrlData: LiveData<String> = _currentUrl


    fun loadUrl(url: String) {
        _currentUrl.postValue(url)
    }

    fun clearWebpage() {
        _currentUrl.postValue("")
    }
}