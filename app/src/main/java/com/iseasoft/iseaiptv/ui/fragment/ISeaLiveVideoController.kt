package com.iseasoft.iseaiptv.ui.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout

import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile
import com.iseasoft.iseaiptv.R

class ISeaLiveVideoController : VideoControlsMobile {

    private var screenModeChangeButton: ImageView? = null
    private var btnReload: ImageButton? = null
    private var btnPlaylist: ImageButton? = null
    private var btnFavorite: ImageButton? = null
    private var playErrorContainer: RelativeLayout? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    fun setScreenModeChangeButtonClickListener(screenModeChangeButtonClickListener: View.OnClickListener) {
        if (screenModeChangeButton != null) {
            screenModeChangeButton!!.setOnClickListener(screenModeChangeButtonClickListener)
        }
    }

    fun setReloadButtonClickListener(reloadButtonClickListener: View.OnClickListener) {
        if (btnReload != null) {
            btnReload!!.setOnClickListener(reloadButtonClickListener)
        }
    }

    fun setPlaylistButtonClickListener(playlistButtonClickListener: View.OnClickListener) {
        if (btnPlaylist != null) {
            btnPlaylist!!.setOnClickListener(playlistButtonClickListener)
        }
    }

    fun setFavoriteButtonClickListener(playlistButtonClickListener: View.OnClickListener) {
        if (btnFavorite != null) {
            btnFavorite!!.setOnClickListener(playlistButtonClickListener)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.isealive_video_controls
    }

    override fun retrieveViews() {
        super.retrieveViews()
        screenModeChangeButton = findViewById(R.id.button_screen_mode_change)
        btnReload = findViewById(R.id.exomedia_controls_reload_btn)
        btnPlaylist = findViewById(R.id.playlist_play)
        btnFavorite = findViewById(R.id.favorite)
        playErrorContainer = findViewById(R.id.play_error_container)
    }

    fun updateScreenModeChangeImage(isFullScreen: Boolean) {
        screenModeChangeButton!!.setImageResource(if (isFullScreen) R.drawable.ic_fullscreen_exit else R.drawable.ic_fullscreen)
    }

    fun setReloadButtonVisible(visible: Boolean) {
        btnReload!!.visibility = if (visible) View.VISIBLE else View.GONE
        playPauseButton.visibility = if (visible) View.GONE else View.VISIBLE
    }

    fun showPlayErrorMessage(show: Boolean) {
        playErrorContainer!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setFavorited(faved: Boolean) {
        btnFavorite!!.setBackgroundResource(if (faved) R.drawable.ic_favorited else R.drawable.ic_favorite)
    }

    override fun showLoading(initialLoad: Boolean) {
        super.showLoading(initialLoad)
        controlsContainer.visibility = View.VISIBLE
    }
}
