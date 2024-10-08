package com.triPCups.media.freeTube.views.video

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.triPCups.media.freeTube.consts.Constants
import com.triPCups.media.freeTube.databinding.FragmentVideoBinding
import com.triPCups.media.freeTube.utils.TAG
import com.triPCups.media.freeTube.utils.WatchlistAgent
import com.triPCups.media.freeTube.utils.urlToLoad
import kotlin.jvm.functions.Function0


class VideoFragment : Fragment() {

    companion object {
        fun newInstance(videoId: String) = VideoFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.VIDEO_FRAG_VIDEO_ID_PARAM, videoId)
            }
        }
    }


    private var didStartedToPlay: Boolean = false

    //vm
    private val viewModel: VideoViewModel by viewModels()

    //ui params
    private var _binding: FragmentVideoBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var videoPlayer: YouTubePlayer
    private var youTubePlayerListener: AbstractYouTubePlayerListener? = null

    //video params - todo - move them to vm ???
    private var currentState: PlayerConstants.PlayerState = PlayerConstants.PlayerState.UNKNOWN
    private var currentSecond: Double = 0.0
    private var currentVideoId: String = ""


    private fun initUi() = with(binding.youTubePlayerView) {
        /// Below line of code is commented out because we want to play videos in background
        /// Read more @ https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#play-youtube-videos-in-the-background
//        lifecycle.addObserver(this)
        enableBackgroundPlayback(true)
        addYouTubePlayerListener(youTubePlayerListener!!)
        addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                Log.d("wow", "Fullscreen: onEnterFullscreen")
            }
            override fun onExitFullscreen() {
                Log.d("wow", "Fullscreen: onExitFullscreen")
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("wow", "onConfigurationChanged")

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Handle the change to landscape
            Log.d("wow", "onConfigurationChanged: ORIENTATION_LANDSCAPE")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Handle the change to portrait
            Log.d("wow", "onConfigurationChanged: ORIENTATION_PORTRAIT")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        arguments?.apply {
            getString(Constants.VIDEO_FRAG_VIDEO_ID_PARAM)?.let { videoId ->
                viewModel.setCurrentVideo(videoId)
            }
        }
        // Handle back press to navigate to WebViewFragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
                urlToLoad = ""
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(youTubePlayerListener == null) {
            youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    videoPlayer = youTubePlayer
                    initObservers()

                    Log.d(TAG, "onReady: update video $currentVideoId to second : $currentSecond")
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    super.onCurrentSecond(youTubePlayer, second)
                    currentSecond = second.toDouble()

                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    super.onStateChange(youTubePlayer, state)
                    currentState = state

                    Log.d(TAG, "onStateChange: currentState $currentState video $currentVideoId to second : $currentSecond")

//                    Log.d(TAG, "onCurrentSecond: currentState $currentState video $currentVideoId to second : $currentSecond")
                    if (
//                        currentState == PlayerConstants.PlayerState.BUFFERING ||
                        currentState == PlayerConstants.PlayerState.ENDED ||
                        currentState == PlayerConstants.PlayerState.PAUSED
//                        || currentState == PlayerConstants.PlayerState.UNSTARTED
                        ) {

                        if(::videoPlayer.isInitialized && didStartedToPlay) {
                            Log.d(TAG, "onCurrentSecond: aaa  check41 $currentSecond ${currentSecond::class.java.simpleName}")
                            WatchlistAgent.getInstance(requireContext()).updateWatchSecondInVideo(currentVideoId, currentSecond)
                        }
                    }
                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                    super.onVideoId(youTubePlayer, videoId)
                    currentVideoId = videoId
                }
            }
        }

        initUi()
    }

    private fun initObservers() = with(viewModel) {
        currentVideoData.observe(viewLifecycleOwner) { currentVideoId ->
            playVideo(currentVideoId)
        }
    }

    private fun playVideo(videoId: String) = with(binding) {
        if (::videoPlayer.isInitialized) {
            currentVideoId = videoId
            currentSecond = try {
                WatchlistAgent.getInstance(requireContext())
                    .getCurrentSecondForVideo(currentVideoId) ?: 0.0
            } catch (e: Exception) {
                e.printStackTrace()
                -1.0
            }
            Log.d(TAG, "playVideo: aaa currentSecond $currentSecond")

            videoPlayer.loadVideo(videoId, currentSecond.toFloat())
            didStartedToPlay = true
            Log.d(TAG, "playVideo: play video $currentVideoId to second : $currentSecond")

        } else {
            // Handle the case where videoPlayer is not initialized yet.
            // You can log an error or take appropriate action here.
            Log.e("VideoFragment", "Attempted to play video before player initialization.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.youTubePlayerView.release()
        youTubePlayerListener?.let { binding.youTubePlayerView.removeYouTubePlayerListener(it) }
        _binding = null
    }
}

//TODO:: Zoom in and out on the video
//TODO:: implement leanback