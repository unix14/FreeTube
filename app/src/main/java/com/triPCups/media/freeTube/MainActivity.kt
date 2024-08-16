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

class MainActivity : AppCompatActivity(), WebViewFragmentListener {

    private lateinit var binding: ActivityMainBinding
    private var sharedVideoUrl: String? = null

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

        handleShareData()
        initUi()
    }

    private fun initUi() {
        // todo fix nav color is not changed in some devices
        val blackColor = Color.rgb(0, 0, 0)
        window.navigationBarColor = blackColor
        window.statusBarColor = blackColor

        loadHome()

        sharedVideoUrl?.let {
            val second = YoutubeHelper.extractTimestampFromUrl(it) ?: -1
            Log.d("wow", "initUi: second is $second")

            loadVideoFragment(YoutubeHelper.extractVideoIdFromUrl(sharedVideoUrl ?: "") ?: Constants.DEFAULT_VIDEO_ID)
            //todo extract time to skip to point
        }
    }

    private fun handleShareData() = with(intent) {
        when {
            action == Intent.ACTION_SEND -> {
                if ("text/plain" == type) {
                    // Handle text being sent
                    getStringExtra(Intent.EXTRA_TEXT)?.let {
                        // Update UI to reflect text being shared
                        sharedVideoUrl = it
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
                    // Perform action based on the URL, like loading a specific fragment or activity
                    if(url.contains("youtube.com") || url.contains("youtu.be")) {
                        sharedVideoUrl = url
                    }
                }
            }
        }
    }

    private fun loadVideoFragment(videoId: String) {
        loadFragment(VideoFragment.newInstance(videoId))
    }

    private fun loadWebViewFragment(url: String) {
        addFragment(WebViewFragment.newInstance(url))
    }

    private fun loadFragment(fragment: Fragment) = with(supportFragmentManager) {
        beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null) // Add to back stack to handle back navigation
            .commit()
    }

    private fun addFragment(fragment: Fragment) = with(supportFragmentManager) {
        beginTransaction()
            .add(binding.container.id, fragment)
            .commit()
    }

    override fun loadHome() {
        loadWebViewFragment(BASE_UTUBE_URL)
    }

    override fun onVideoClicked(videoId: String) {
        loadVideoFragment(videoId)
    }
}