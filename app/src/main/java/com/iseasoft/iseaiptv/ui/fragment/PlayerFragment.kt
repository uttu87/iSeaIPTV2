package com.iseasoft.iseaiptv.ui.fragment


import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.devbrackets.android.exomedia.listener.*
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.adapters.ChannelAdapter
import com.iseasoft.iseaiptv.listeners.FragmentEventListener
import com.iseasoft.iseaiptv.listeners.OnChannelListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity.Companion.CHANNEL_KEY
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import com.startapp.sdk.ads.banner.Banner
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [PlayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerFragment : AdsFragment(), OnPreparedListener, View.OnClickListener, OnCompletionListener, OnErrorListener, VideoControlsButtonListener, VideoControlsVisibilityListener {

    internal lateinit var unbinder: Unbinder

    @BindView(R.id.video_view)
    @JvmField
    var videoView: VideoView? = null

    @BindView(R.id.thumbnail_layout)
    @JvmField
    var thumbnailLayout: FrameLayout? = null

    @BindView(R.id.thumbnail_image_view)
    @JvmField
    var thumbnailImage: ImageView? = null

    @BindView(R.id.thumbnail_seek_time)
    @JvmField
    var thumbnailSeekTextView: TextView? = null

    @BindView(R.id.playlist_container)
    @JvmField
    var playlistContainer: RelativeLayout? = null

    @BindView(R.id.rv_playlist)
    @JvmField
    var rvPlaylist: RecyclerView? = null

    private var publisherAdView: PublisherAdView? = null
    private var banner: Banner? = null

    private var mChannel: M3UItem? = null
    private var mPlaylist: ArrayList<M3UItem>? = null
    private var mVideoUrl: String? = null
    private val playerStatus: Int = 0
    private val isFixedScreen: Boolean = false
    private var isFullscreen: Boolean = false
    private var currentPosition: Long = 0
    private val isSeeking: Boolean = false
    private val isReloadStatus: Boolean = false
    private var mHeight: Int = 0
    private var fragmentEventListener: FragmentEventListener? = null
    private var mVideoController: ISeaLiveVideoController? = null

    private val lastOsdDispTime: Long = 0
    private val nowOn: Boolean = false
    private var mRetryCount = 0
    var isShowingPlaylist = false
        private set
    private var adapter: ChannelAdapter? = null

    private val channelPosition: Int
        get() {
            var pos = 0
            for (i in mPlaylist!!.indices) {
                val item = mPlaylist!![i]
                if (mChannel!!.itemName == item.itemName && mChannel!!.itemUrl == item.itemUrl) {
                    pos = i
                    break
                }
            }
            return pos
        }

    fun setFragmentEventListener(fragmentEventListener: FragmentEventListener) {
        this.fragmentEventListener = fragmentEventListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mChannel = arguments!!.getSerializable(CHANNEL_KEY) as M3UItem
            mPlaylist = App.channelList
            mVideoUrl = mChannel!!.itemUrl

        }
        mHeight = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        unbinder = ButterKnife.bind(this, view)
        isFullscreen = true
        if (savedInstanceState == null) {
            setupVideoView()
            setupPlaylist()
            //setupPublisherBannerAds();
            //setupStartAppBanner();
        }

        return view
    }

//    private fun setupPublisherBannerAds() {
//        publisherAdView = PublisherAdView(activity)
//        publisherAdView!!.adUnitId = App.getPublisherBannerId()
//        publisherAdView!!.setAdSizes(AdSize.BANNER)
//        val adRequest = PublisherAdRequest.Builder()
//                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
//                .build()
//        publisherAdView!!.loadAd(adRequest)
//        publisherAdView!!.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//                if (publisherAdView != null) {
//                    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT)
//                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
//                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
//                    playlistContainer!!.removeView(publisherAdView)
//                    playlistContainer!!.addView(publisherAdView, params)
//
//                }
//            }
//
//            override fun onAdFailedToLoad(i: Int) {
//                super.onAdFailedToLoad(i)
//                setupStartAppBanner()
//            }
//        }
//    }

    private fun setupStartAppBanner() {
        banner = Banner(activity)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        playlistContainer!!.removeView(banner)
        playlistContainer!!.addView(banner, params)
        banner!!.loadAd()
    }

    private fun setupPlaylist() {
        if (adapter == null) {
            adapter = activity?.let {
                ChannelAdapter(it, R.layout.item_channel_list, object : OnChannelListener {
                    override fun onChannelClicked(item: M3UItem) {
                        mChannel = item
                        playChannel(mChannel!!)
                    }
                })
            }
        }
        mPlaylist?.let { adapter!!.update(it) }
        spaceBetweenAds = AdsFragment.LIST_VIEW_ADS_COUNT / 2
        //generateDataSet(adapter);
        rvPlaylist?.adapter = adapter
        adapter?.notifyItemChanged(channelPosition)
        activity?.let { rvPlaylist?.let { it1 -> Utils.modifyListViewForVertical(it, it1) } }
        isShowingPlaylist = false
    }

    private fun setupVideoView() {
        mRetryCount = 0
        setUpVideoViewSize(isFullscreen)
        // Make sure to use the correct VideoView import
        if (mVideoController == null) {
            mVideoController = context?.let { ISeaLiveVideoController(it) }
        }
        videoView?.setControls(mVideoController)
        videoView?.setOnPreparedListener(this)
        videoView?.setOnCompletionListener(this)
        videoView?.setOnErrorListener(this)
        videoView?.setAnalyticsListener(EventLogger(null))
        mVideoController?.setScreenModeChangeButtonClickListener(this)
        mVideoController?.setReloadButtonClickListener(this)
        mVideoController?.setPlaylistButtonClickListener(this)
        mVideoController?.setFavoriteButtonClickListener(this)
        mVideoController?.setButtonListener(this)
        mVideoController?.setVisibilityListener(this)
        mVideoController?.setPreviousButtonEnabled(true)
        mVideoController?.setNextButtonEnabled(true)

        mVideoController?.setTitle(mChannel!!.itemName)

        //For now we just picked an arbitrary item to play
        playChannel(mChannel!!)
    }

    private fun updateFavoriteIcon() {
        val preferencesUtility = PreferencesUtility.getInstance(activity)
        val isFaved = preferencesUtility.checkFavorite(mChannel)
        mVideoController!!.setFavorited(isFaved)
    }

    private fun playChannel(channel: M3UItem) {
        mRetryCount = 0
        videoView?.setVideoURI(Uri.parse(channel.itemUrl))
        mVideoController?.setTitle(channel.itemName)
        mVideoController?.showPlayErrorMessage(false)
        updateFavoriteIcon()
        PreferencesUtility.getInstance(activity).addHistory(channel)
        if (fragmentEventListener != null) {
            fragmentEventListener!!.onPlayChannel()
        }
    }

    private fun setUpVideoViewSize(isFullscreen: Boolean) {
        if (isFullscreen) {
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            videoView?.layoutParams = params
        } else {
            val metrics = resources.displayMetrics
            if (mHeight == 0) {
                mHeight = (metrics.widthPixels * BALANCED_VISIBLE_FRACTION + 0.5f).toInt()
            }
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeight
            )

            videoView?.layoutParams = params
        }
    }

    override fun onPrepared() {
        if (!isStateSafe) {
            return
        }
        if (videoView != null) {
            videoView!!.start()
            mRetryCount = 0
            videoView!!.setRepeatMode(REPEAT_MODE_ONE)

            if (mVideoController != null) {
                mVideoController!!.updatePlayPauseImage(true)
                mVideoController!!.updateScreenModeChangeImage(isFullscreen)
            }
            if (currentPosition > 0) {
                videoView!!.seekTo(currentPosition)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (videoView != null) {
            videoView!!.start()

        }
        if (mVideoController != null) {
            mVideoController!!.updatePlayPauseImage(true)
        }

        screenModeChange(isFullscreen, false)

        if (publisherAdView != null) {
            publisherAdView!!.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()

        if (publisherAdView != null) {
            publisherAdView!!.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if (mVideoController != null) {
//            mVideoController!!.setReloadButtonClickListener(null)
//            mVideoController!!.setScreenModeChangeButtonClickListener(null)
//            mVideoController!!.setButtonListener(null)
//            mVideoController!!.setPlaylistButtonClickListener(null)
//            mVideoController!!.setVisibilityListener(null)
//        }
        mVideoController = null
        mChannel = null
        mPlaylist = null
        fragmentEventListener = null

        if (publisherAdView != null) {
            publisherAdView!!.destroy()
        }

        banner = null
        unbinder.unbind()
    }

    override fun onClick(v: View) {
        controllerButtonClick(v.id)
    }

    fun controllerButtonClick(id: Int) {
        when (id) {
            R.id.button_screen_mode_change -> screenModeChange(!isFullscreen, true)
            R.id.exomedia_controls_reload_btn -> {
                mVideoController?.setReloadButtonVisible(false)
                videoView?.restart()
            }
            R.id.playlist_play -> showPlaylist()
            R.id.favorite -> favorite()
        }
    }

    private fun favorite() {
        val preferencesUtility = PreferencesUtility.getInstance(activity)
        preferencesUtility.favorite(mChannel)
        updateFavoriteIcon()
    }

    fun showPlaylist() {
        isShowingPlaylist = !isShowingPlaylist
        playlistContainer!!.visibility = if (isShowingPlaylist) View.VISIBLE else View.GONE
    }

    fun screenModeChange(fullscreen: Boolean, isUserChange: Boolean) {
        if (isFixedScreen && !fullscreen) {
            return
        }
        isFullscreen = fullscreen
        if (mVideoController != null) {
            mVideoController!!.updateScreenModeChangeImage(isFullscreen)
        }
        if (videoView != null) {
            currentPosition = videoView!!.currentPosition
        }
        if (fragmentEventListener != null) {
            fragmentEventListener!!.changeScreenMode(isFullscreen, isUserChange)
        }

        setUpVideoViewSize(isFullscreen)
    }

    override fun onCompletion() {
        if (!isStateSafe) {
            return
        }

        if (mRetryCount < MAX_RETRY_COUNT) {
            mRetryCount++
            videoView?.restart()
            return
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenModeChange(true, false)
        }

    }

    override fun onError(e: Exception): Boolean {
        Log.i(TAG, e.message)
        if (!isStateSafe) {
            return false
        }

        if (mRetryCount < MAX_RETRY_COUNT) {
            mRetryCount++
            videoView?.restart()
            return false
        }

        if (mVideoController != null) {
            mVideoController?.showPlayErrorMessage(true)
            mVideoController?.finishLoading()
        }
        return false
    }

    override fun onPlayPauseClicked(): Boolean {
        return false
    }

    override fun onPreviousClicked(): Boolean {
        val position = channelPosition
        if (position > 1) {
            mChannel = mPlaylist!![position - 1]
            mVideoUrl = mChannel!!.itemUrl
            playChannel(mChannel!!)
        }
        return true
    }

    override fun onNextClicked(): Boolean {
        val position = channelPosition
        if (position < mPlaylist!!.size - 1) {
            mChannel = mPlaylist!![position + 1]
            mVideoUrl = mChannel!!.itemUrl
            playChannel(mChannel!!)
        }
        return true
    }

    override fun onRewindClicked(): Boolean {
        return false
    }

    override fun onFastForwardClicked(): Boolean {
        return false
    }

    override fun onControlsShown() {
        playlistContainer!!.visibility = View.GONE
    }

    override fun onControlsHidden() {}

    companion object {
        val TAG = PlayerFragment::class.java.simpleName
        private val BALANCED_VISIBLE_FRACTION = 0.5625f
        private val OSD_DISP_TIME: Long = 3000
        private val MAX_RETRY_COUNT = 3

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param item
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(item: M3UItem, playlist: ArrayList<M3UItem>): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putSerializable(CHANNEL_KEY, item)
            //args.putSerializable(PLAYLIST_KEY, playlist);
            fragment.arguments = args
            return fragment
        }

        fun newInstance(item: M3UItem): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putSerializable(CHANNEL_KEY, item)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
