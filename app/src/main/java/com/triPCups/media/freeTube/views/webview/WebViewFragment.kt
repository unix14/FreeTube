package com.triPCups.media.freeTube.views.webview

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.triPCups.media.freeTube.R
import com.triPCups.media.freeTube.consts.Constants
import com.triPCups.media.freeTube.databinding.FragmentWebViewBinding
import com.triPCups.media.freeTube.views.video.VideoFragment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

    private fun initUi() = with(binding){
        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                return false
//            }
        }
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
        return binding.root
    }
}