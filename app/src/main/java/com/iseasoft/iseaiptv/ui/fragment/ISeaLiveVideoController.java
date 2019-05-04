package com.iseasoft.iseaiptv.ui.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile;
import com.iseasoft.iseaiptv.R;

public class ISeaLiveVideoController extends VideoControlsMobile {

    private ImageView screenModeChangeButton;
    private ImageButton btnReload;
    private ImageButton btnPlaylist;
    private ImageButton btnFavorite;
    private RelativeLayout playErrorContainer;

    public ISeaLiveVideoController(Context context) {
        super(context);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ISeaLiveVideoController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScreenModeChangeButtonClickListener(OnClickListener screenModeChangeButtonClickListener) {
        if (screenModeChangeButton != null) {
            screenModeChangeButton.setOnClickListener(screenModeChangeButtonClickListener);
        }
    }

    public void setReloadButtonClickListener(OnClickListener reloadButtonClickListener) {
        if (btnReload != null) {
            btnReload.setOnClickListener(reloadButtonClickListener);
        }
    }

    public void setPlaylistButtonClickListener(OnClickListener playlistButtonClickListener) {
        if (btnPlaylist != null) {
            btnPlaylist.setOnClickListener(playlistButtonClickListener);
        }
    }

    public void setFavoriteButtonClickListener(OnClickListener playlistButtonClickListener) {
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(playlistButtonClickListener);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.isealive_video_controls;
    }

    @Override
    protected void retrieveViews() {
        super.retrieveViews();
        screenModeChangeButton = findViewById(R.id.button_screen_mode_change);
        btnReload = findViewById(R.id.exomedia_controls_reload_btn);
        btnPlaylist = findViewById(R.id.playlist_play);
        btnFavorite = findViewById(R.id.favorite);
        playErrorContainer = findViewById(R.id.play_error_container);
    }

    public void updateScreenModeChangeImage(boolean isFullScreen) {
        screenModeChangeButton.setImageResource(isFullScreen ? R.drawable.ic_fullscreen_exit : R.drawable.ic_fullscreen);
    }

    public void setReloadButtonVisible(boolean visible) {
        btnReload.setVisibility(visible ? VISIBLE : GONE);
        playPauseButton.setVisibility(visible ? GONE : VISIBLE);
    }

    public void showPlayErrorMessage(boolean show) {
        playErrorContainer.setVisibility(show ? VISIBLE : GONE);
    }

    public void setFavorited(boolean faved) {
        btnFavorite.setBackgroundResource(faved ? R.drawable.ic_favorited : R.drawable.ic_favorite);
    }

    @Override
    public void showLoading(boolean initialLoad) {
        super.showLoading(initialLoad);
        controlsContainer.setVisibility(VISIBLE);
    }
}
