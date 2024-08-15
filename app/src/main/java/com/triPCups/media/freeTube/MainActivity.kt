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
import com.triPCups.media.freeTube.databinding.ActivityMainBinding
import com.triPCups.media.freeTube.utils.YoutubeHelper
import com.triPCups.media.freeTube.views.video.VideoFragment
import com.triPCups.media.freeTube.views.webview.WebViewFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var sharedVideoId: String? = null

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
        val blackColor = Color.rgb(0, 0, 0)
        window.navigationBarColor = blackColor
        window.statusBarColor = blackColor

        loadWebViewFragment("https://www.youtube.com/results?search_query=qqq")
//        loadVideoFragment(sharedVideoId?.let {
//            YoutubeHelper.extractVideoIdFromUrl(sharedVideoId!!)
//            //todo extract time to skip to point
//        } ?: run {
//            Constants.DEFAULT_VIDEO_ID
//        })
    }

    private fun handleShareData() = with(intent) {
        when {
            action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    // Handle text being sent
                    getStringExtra(Intent.EXTRA_TEXT)?.let {
                        // Update UI to reflect text being shared
                        sharedVideoId = it
                    }
                } else {
                    // print an error
                    Log.e("wow", "handleShareText: Can't read intent.type is $type and action is $action")
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
                Log.e("wow", "handleShareText: couldn't recognise the intent.type is $type and action is $action")
            }
        }
    }

    private fun loadVideoFragment(videoId: String) {
        loadFragment(VideoFragment.newInstance(videoId))
    }

    private fun loadWebViewFragment(url: String) {
        loadFragment(WebViewFragment.newInstance(url))
    }

    private fun loadFragment(fragment: Fragment) = with(supportFragmentManager) {
        beginTransaction()
            .add(binding.container.id, fragment)
            .commit()
    }
}