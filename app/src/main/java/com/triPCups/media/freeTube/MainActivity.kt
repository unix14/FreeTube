package com.triPCups.media.freeTube

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
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
    private var orientationEventListener: OrientationEventListener? = null

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

        handleNewIntent()
        initUi()

        // Initialize the OrientationEventListener
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    // The device is in an unknown orientation state
                    return
                }

                // Determine the rotation angle (0, 90, 180, 270 degrees)
                when (orientation) {
                    in 45..134 -> {
                        // Device is rotated to landscape (clockwise)
                        Log.d("Orientation", "Landscape 90°")
                        // Force landscape orientation
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    }

                    in 135..224 -> {
                        // Device is rotated upside down
                        Log.d("Orientation", "Upside Down 180°")
                        // Force portrait orientation if you want to revert to portrait when upside down
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    }

                    in 225..314 -> {
                        // Device is rotated to landscape (counterclockwise)
                        Log.d("Orientation", "Landscape 270°")
                        // Force landscape orientation
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }

                    else -> {
                        // Device is in portrait
                        Log.d("Orientation", "Portrait 0°")
                        // Force portrait orientation
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                }
            }
        }
        // Enable the OrientationEventListener
        orientationEventListener?.enable()
    }


    override fun onDestroy() {
        super.onDestroy()
        // Disable the OrientationEventListener when the activity is destroyed
        orientationEventListener?.disable()
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNewIntent()
    }
    private fun handleNewIntent() = with(intent) {
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
                    } else {
                        loadWebViewFragment(url)
                    }
                }
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
            .replace(binding.container.id, fragment)
            .addToBackStack(null) // Add to back stack to handle back navigation
            .commit()
    }

    override fun loadHome() {
        loadWebViewFragment(BASE_UTUBE_URL)
    }

    override fun onVideoClicked(videoId: String) {
        loadVideoFragment(videoId)
    }
}