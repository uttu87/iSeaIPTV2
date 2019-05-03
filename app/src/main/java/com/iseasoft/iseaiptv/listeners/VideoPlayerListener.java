package com.iseasoft.iseaiptv.listeners;

import com.iseasoft.iseaiptv.ui.fragment.PlayerType;
import com.iseasoft.iseaiptv.ui.fragment.VideoPlayerView;

public interface VideoPlayerListener {

    void onChangeStatus(VideoPlayerView playerView, @PlayerType.PlayerStatusType int type);

    void onError(VideoPlayerView playerView, @PlayerType.PlayerStatusType int error);

    void playbackProgress(VideoPlayerView playerView, int progress);

    void playbackBufferProgress(VideoPlayerView playerView, int buffer);
}
