package com.triPCups.media.freeTube.views.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import com.triPCups.media.freeTube.consts.Constants
import com.triPCups.media.freeTube.databinding.FragmentWebViewBinding
import com.triPCups.media.freeTube.utils.WebAppInterface


interface WebViewFragmentListener {
    fun onVideoClicked(videoId: String)
    fun loadHome()
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
                webView.settings.mediaPlaybackRequiresUserGesture = true // This may prevent autoplay
            }
            webView.webChromeClient = WebChromeClient()
            webView.addJavascriptInterface(WebAppInterface(listener!!) {
                viewModel.clearWebpage()
            }, "AndroidInterface")
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

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    Log.d("wow", "shouldInterceptRequest: url is ${request?.url}")
                    if (request != null
                        /*&& request.url.toString().contains("googlevideo.com") && request.url.toString().contains("/videoplayback") &&*/
                        && (viewModel.currentUrlData.value?.toString()?.equals(Constants.BASE_UTUBE_URL) != true
                                && (request.url.toString().contains("youtube.com/youtubei/v1/player") || request.url.toString().contains("youtube.com/youtubei/v1/next") || request.url.toString().contains("youtube.com/watch")))
                        ) {
                        // Return an empty response to block the video from loading
                        return WebResourceResponse("text/plain", "UTF-8", null)
                    }
                    return super.shouldInterceptRequest(view, request)
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
        // Handle back press to navigate to WebViewFragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.webView.apply {
                    if(canGoBack()) {
                        Log.d("wow", "handleOnBackPressed: canGoBack")
                        listener?.loadHome()
                        goBack()
                    } else {
                        Log.d("wow", "handleOnBackPressed: finish")
                        requireActivity().finish()
                    }
                }
            }
        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }
}