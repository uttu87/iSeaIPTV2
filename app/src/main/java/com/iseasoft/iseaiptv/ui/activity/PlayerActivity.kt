package com.iseasoft.iseaiptv.ui.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.Constants
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.FragmentEventListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.ui.fragment.PlayerFragment
import com.startapp.sdk.adsbase.StartAppAd
import java.util.*

class PlayerActivity : BaseActivity(), FragmentEventListener {

    private var publisherInterstitialAd: PublisherInterstitialAd? = null

    private var mHandler: Handler? = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = Runnable { this.setupStartAppAd() }

    private val isImmersiveAvailable: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    private fun setupPublisherInterstitialAds() {
        if (publisherInterstitialAd == null) {
            publisherInterstitialAd = PublisherInterstitialAd(this)
            publisherInterstitialAd!!.adUnitId = App.publisherInterstitialId
        }
        requestNewInterstitial()
    }

    private fun requestNewInterstitial() {
        val adRequest = PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build()

        publisherInterstitialAd!!.loadAd(adRequest)

        publisherInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                if (publisherInterstitialAd != null) {
                    publisherInterstitialAd!!.show()
                }
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                setupStartAppAd()
            }
        }
    }

    private fun setupStartAppAd() {
        StartAppAd.showAd(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initStartAppSdk()
        setContentView(R.layout.activity_player)
        super.onCreate(savedInstanceState)
        val mChannel: M3UItem
        val mPlaylist = ArrayList<M3UItem>()
        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra(Constants.PUSH_URL_KEY))) {
            val matchUrl = intent.getStringExtra(Constants.PUSH_URL_KEY)
            val message = intent.getStringExtra(Constants.PUSH_MESSAGE)
            mChannel = M3UItem()
            mChannel.itemUrl = matchUrl
            mChannel.itemName = message
            //mPlaylist.add(mChannel);
        } else {
            mChannel = intent.extras!!.getSerializable(CHANNEL_KEY) as M3UItem
            //mPlaylist.addAll((ArrayList<M3UItem>) getIntent().getExtras().getSerializable(PLAYLIST_KEY));
        }

        setupPlayer(mChannel)
    }

    private fun setupPlayer(channel: M3UItem) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val playerFragment = PlayerFragment.newInstance(channel)
        playerFragment.setFragmentEventListener(this)
        ft.replace(R.id.player_view, playerFragment, PlayerFragment.TAG)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()

    }

    private fun setupPlayer(channel: M3UItem, playlist: ArrayList<M3UItem>) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val playerFragment = PlayerFragment.newInstance(channel, playlist)
        playerFragment.setFragmentEventListener(this)
        ft.replace(R.id.player_view, playerFragment, PlayerFragment.TAG)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()

    }

    fun setFullscreen(activity: Activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN

            if (isImmersiveAvailable) {
                flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }

            activity.window.decorView.systemUiVisibility = flags
        } else {
            activity.window
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun exitFullscreen(activity: Activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            activity.window
                    .setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }

    override fun changeScreenMode(isFullScreen: Boolean, isUserSelect: Boolean) {
        if (isFullScreen) {
            if (isUserSelect) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            setFullscreen(this)
        } else {
            if (isUserSelect) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            exitFullscreen(this)
        }
    }

    override fun onPlayChannel() {
        showFullscreenAds()
    }

    private fun showFullscreenAds() {
        if (mHandler == null || runnable == null) {
            return
        }
        mHandler!!.removeCallbacks(runnable)
        mHandler!!.postDelayed(runnable, App.timeDelayToShowAds * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        publisherInterstitialAd = null
        mHandler!!.removeCallbacks(runnable)
        mHandler = null
        runnable = null
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(PlayerFragment.TAG) as PlayerFragment?
        if (fragment != null && fragment.isShowingPlaylist) {
            fragment.showPlaylist()
            return
        }
        super.onBackPressed()
    }

    companion object {

        val CHANNEL_KEY = "channel"
        val PLAYLIST_KEY = "playlist"
    }
}
