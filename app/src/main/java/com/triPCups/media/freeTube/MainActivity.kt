package com.triPCups.media.freeTube

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.triPCups.media.freeTube.consts.Constants
import com.triPCups.media.freeTube.consts.Constants.Companion.BASE_UTUBE_URL
import com.triPCups.media.freeTube.databinding.ActivityMainBinding
import com.triPCups.media.freeTube.utils.YoutubeHelper
import com.triPCups.media.freeTube.views.video.VideoFragment
import com.triPCups.media.freeTube.views.webview.WebViewFragment
import com.triPCups.media.freeTube.views.webview.WebViewFragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WebViewFragmentListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleNewIntent(isFirstCreate = true)
        initUi()
    }

    private fun initUi() {
        // todo fix nav color is not changed in some devices
        val blackColor = Color.rgb(0, 0, 0)
        window.navigationBarColor = blackColor
        window.statusBarColor = blackColor
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNewIntent(isFirstCreate = false)
    }

    private fun handleNewIntent(isFirstCreate: Boolean = false) {
        var handledLink = false
        with(intent) {
            when {
                action == Intent.ACTION_SEND -> {
                    if ("text/plain" == type) {
                        // Handle text being sent
                        getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
                            handledLink = true
                            YoutubeHelper.extractVideoIdFromUrl(url)?.let { videoId ->
                                val second = YoutubeHelper.extractTimestampFromUrl(url) ?: -1
                                loadVideoFragment(videoId, second)
                            }
                        }
                    } else {
                        // print an error
                        Log.e("wow", "handleShareText: Can't read intent.type is $type and action is $action")
                    }
                }
                else -> {
                    // Handle other intents, such as being started from the home screen
                    Log.e("wow", "handleShareText: couldn't recognise the intent.type is $type and action is $action")

                    // Handle intent from other apps starting a youtube video
                    data?.let {
                        // Handle the URL here
                        val url = data.toString()
                        handledLink = true
                        // Perform action based on the URL, like loading a specific fragment or activity
                        if(url.contains("youtube.com") || url.contains("youtu.be")) {
                            YoutubeHelper.extractVideoIdFromUrl(url)?.let { videoId ->
                                val second = YoutubeHelper.extractTimestampFromUrl(url) ?: -1
                                loadVideoFragment(videoId, second)
                            }
                        } else {
                            loadWebViewFragment(url)
                        }
                    }
                }
            }
        }

        if (isFirstCreate && !handledLink) {
            loadHome()
        }
    }

    private fun loadVideoFragment(videoId: String, startSecond: Int = -1) {
        loadFragment(VideoFragment.newInstance(videoId, startSecond))
    }

    private fun loadWebViewFragment(url: String) {
        loadFragment(WebViewFragment.newInstance(url))
    }

    private fun loadFragment(fragment: Fragment) = with(supportFragmentManager) {
        beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null) // Add to back stack to handle back navigation
            .commit()
    }

    override fun loadHome() {
        loadWebViewFragment(BASE_UTUBE_URL)
    }

    override fun onVideoClicked(videoId: String, startSecond: Int) {
        loadVideoFragment(videoId, startSecond)
    }
}