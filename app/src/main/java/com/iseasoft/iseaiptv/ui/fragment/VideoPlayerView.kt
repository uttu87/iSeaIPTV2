package com.iseasoft.iseaiptv.ui.fragment

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.VideoPlayerListener
import java.util.*

class VideoPlayerView : FrameLayout {
    internal var videoSetErrorEventToken = 0
    private var proxyUrl: String? = null
    private var playerListener: VideoPlayerListener? = null
    private var playerVideoView: VideoView? = null
    private var isFullScreen: Boolean = false
    private var pt: Long = 0
    private var isSendFirstPlay: Boolean = false
    private var playStartTime: Long = 0
    private val resumeTime = -1
    private val playing = false
    var additionalHeaders: Map<String, String> = HashMap()

    val duration: Long
        get() = playerVideoView!!.duration

    val currentTime: Long
        get() = playerVideoView!!.currentPosition

    val bufferedPosition: Int
        get() = playerVideoView!!.bufferPercentage

    //TODO
    //TODO setLimitBitrate
    var limitBitrate: Int
        get() = 0
        set(limitBitrate) {}

    val isPlaying: Boolean
        get() = if (playerVideoView != null) playerVideoView!!.isPlaying else false

    private val displaySizeString: String
        get() {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (windowManager != null) {
                val display = windowManager.defaultDisplay
                val point = Point(0, 0)
                display.getRealSize(point)
                return String.format("%dx%d", point.x, point.y)
            } else {
                return ""
            }
        }


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun setProxyUrl(url: String) {
        this.proxyUrl = url
    }

    fun setPlayerListener(playerListener: VideoPlayerListener) {
        this.playerListener = playerListener
    }

    private fun resetValues() {
        resetPt()
        isSendFirstPlay = false
        playStartTime = 0
    }

    fun play() {
        if (playerVideoView != null)
            playerVideoView!!.start()
    }

    fun pause() {
        playerVideoView!!.pause()
    }

    fun seek(position: Long) {
        var position = position
        if (position < 0) {
            position = 0
        } else if (position > playerVideoView!!.duration) {
            position = playerVideoView!!.duration
        }
        playerVideoView!!.seekTo(position)
    }

    fun skipBackward() {
        seek(playerVideoView!!.currentPosition - SEEK_TIME)
    }

    fun skipForward() {
        seek(playerVideoView!!.currentPosition + SEEK_TIME)
    }

    fun setSubtitle(language: String) {
        //TODO
    }

    fun resetPt() {
        pt = 0
    }

    fun resetPlayer() {
        playerVideoView!!.reset()
        playerListener = null
        resetValues()
    }

    fun seekStart() {
        //TODO Check start seek
    }

    fun seekEnd() {
        //TODO check end seek
    }

    fun setFullScreen(isFullScreen: Boolean) {
        this.isFullScreen = isFullScreen
        if (isFullScreen) {
            //TODO Enter fullscreen
        } else {
            //TODO exit fullscreen
        }
    }

    private fun init(context: Context) {

        View.inflate(getContext(), R.layout.view_player, this)
        playerVideoView = findViewById(R.id.player_video_view)

        playerVideoView!!.setControls(null as VideoControlsCore?)

    }

    private fun setVideoFinished() {
        resetValues()
    }

    private fun sendPlayerError(@PlayerType.PlayerErrorType error: Int) {
        if (playerListener != null) {
            playerListener!!.onError(this, error)
        }
    }

    private fun sendPlayerStateUpdateEvent(@PlayerType.PlayerStatusType status: Int) {
        if (playerListener != null) {
            playerListener!!.onChangeStatus(this, status)
        }
    }

    private fun sendPlayEvent(currentTime: Int) {
        if (playerListener != null) {
            playerListener!!.playbackProgress(this, currentTime)
        }
    }

    private fun sendBufferedPosition(position: Int) {
        if (playerListener != null) {
            playerListener!!.playbackBufferProgress(this, position)
        }
    }

    private fun upPt() {
        if (playStartTime > 0) {
            val currentTime = Date().time
            pt += currentTime - playStartTime
        }
        playStartTime = 0
    }

    companion object {
        private val TAG = VideoPlayerView::class.java.simpleName
        private val SEEK_TIME = 10 * 1000
    }
}
