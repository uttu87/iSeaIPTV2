package com.iseasoft.iseaiptv.listeners

import com.iseasoft.iseaiptv.ui.fragment.PlayerType
import com.iseasoft.iseaiptv.ui.fragment.VideoPlayerView

interface VideoPlayerListener {

    fun onChangeStatus(playerView: VideoPlayerView, @PlayerType.PlayerStatusType type: Int)

    fun onError(playerView: VideoPlayerView, @PlayerType.PlayerStatusType error: Int)

    fun playbackProgress(playerView: VideoPlayerView, progress: Int)

    fun playbackBufferProgress(playerView: VideoPlayerView, buffer: Int)
}
