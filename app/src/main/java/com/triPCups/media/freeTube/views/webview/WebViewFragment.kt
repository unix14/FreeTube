package com.triPCups.media.freeTube.views.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.triPCups.media.freeTube.consts.Constants
import com.triPCups.media.freeTube.databinding.FragmentWebViewBinding
import com.triPCups.media.freeTube.utils.WebAppInterface


interface WebViewFragmentListener {
    fun onVideoClicked(videoId: String)
}

class WebViewFragment : Fragment() {

    companion object {
        fun newInstance(videoId: String) = WebViewFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.WEB_VIEW_FRAG_URL_PARAM, videoId)
            }
        }
    }

    private lateinit var binding: FragmentWebViewBinding
    private val viewModel: WebViewViewModel by viewModels()
    private var listener: WebViewFragmentListener? = null

    private fun initObservers() = with(viewModel) {
        currentUrlData.observe(viewLifecycleOwner) { url ->
            handleUrl(url)
        }
    }

    private fun handleUrl(url: String?) = with(binding) {
        if(!url.isNullOrEmpty()) {
            webView.loadUrl(url)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initUi()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUi() = with(binding){
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webView.addJavascriptInterface(WebAppInterface(listener!!), "AndroidInterface")
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("wow", "onPageStarted: url is $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("wow", "onPageFinished: url is $url")

                    injectJavaScript()
                }
            }
        }
    }

    private fun injectJavaScript() {
        val js = """
        document.addEventListener('click', function(event) {
            // Intercept clicks on video thumbnails (usually <a> elements)
            if (event.target.tagName === 'A' && event.target.href.includes('youtube.com/watch')) {
                event.preventDefault();
                AndroidInterface.onVideoClicked(event.target.href);
            }
            
            // Intercept clicks on video titles (usually part of a <span> or <a> element inside a <div>)
            // Adjust the selector as needed based on the page structure
            let target = event.target;
            while (target) {
                if (target.tagName === 'A' && (target.href.includes('youtube.com/watch') || target.href.includes('youtu.be/'))) {
                    event.preventDefault();
                    AndroidInterface.onVideoClicked(target.href);
                    break;
                }
                target = target.parentElement;
            }
        }, true);
    """
        binding.webView.evaluateJavascript(js, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        arguments?.apply {
            getString(Constants.WEB_VIEW_FRAG_URL_PARAM)?.let { url ->
                viewModel.loadUrl(url)
            }
        }
        if(context is WebViewFragmentListener) {
            listener = context as WebViewFragmentListener
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }
}