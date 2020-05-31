package com.iseasoft.iseaiptv.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import butterknife.*
import butterknife.Optional
import com.google.android.gms.ads.*
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.BuildConfig
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.dialogs.PlayStreamDialog
import com.iseasoft.iseaiptv.ui.fragment.AboutFragment
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.nativead.NativeAdPreferences
import com.startapp.sdk.ads.nativead.StartAppNativeAd
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    internal var unbinder: Unbinder? = null
    @BindView(R.id.footer_container)
    @JvmField
    var footerContainer: LinearLayout? = null
    internal var publisherAdView: PublisherAdView? = null
    internal var banner: Banner? = null
    private var adView: AdView? = null


    protected val isStateSafe: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unbinder = ButterKnife.bind(this)
    }

    protected fun setupAdmob() {
        adView = AdView(this)
        adView!!.adSize = AdSize.BANNER
        adView!!.adUnitId = App.admobBannerId
        val adRequest = AdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build()
        adView!!.loadAd(adRequest)
        adView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                if (adView != null) {
                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    adView!!.layoutParams = params
                    footerContainer!!.removeView(adView)
                    footerContainer!!.addView(adView)
                }
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                setupPublisherAds()
            }
        }
    }

    private fun initAdmob() {
        MobileAds.initialize(this, App.admobAppId)
    }

    private fun setupPublisherAds() {
        setupPublisherBannerAds()
    }


    private fun setupPublisherBannerAds() {
        publisherAdView = PublisherAdView(this)
        publisherAdView!!.adUnitId = App.publisherBannerId
        publisherAdView!!.setAdSizes(AdSize.BANNER)
        val adRequest = PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build()
        publisherAdView!!.loadAd(adRequest)
        publisherAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                if (publisherAdView != null) {
                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    publisherAdView!!.layoutParams = params
                    footerContainer!!.removeView(publisherAdView)
                    footerContainer!!.addView(publisherAdView)
                }
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                setupStartAppBanner()
            }
        }
    }

    protected fun setupStartAppBanner() {
        //        banner = new Banner(this);
        //        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //                ViewGroup.LayoutParams.WRAP_CONTENT);
        //        banner.setLayoutParams(params);
        //        footerContainer.removeView(banner);
        //        footerContainer.addView(banner);
        //        banner.loadAd();
    }

    protected fun initStartAppSdk() {
        //StartAppSDK.init(this, App.getStartAppId(), true);
        //StartAppAd.disableSplash();
        //requestNativeAds();
    }

    private fun requestNativeAds() {
        if (App.nativeAdDetails.size > 0) {
            return
        }
        val numberOfAds = 3
        val mStartAppNativeAd = StartAppNativeAd(this)
        mStartAppNativeAd.loadAd(
                NativeAdPreferences()
                        .setAdsNumber(numberOfAds)
                        .setAutoBitmapDownload(true)
                        .setPrimaryImageSize(2),
                object : AdEventListener {
                    override fun onReceiveAd(ad: Ad) {
                        App.nativeAdDetails = mStartAppNativeAd.nativeAds
                    }

                    override fun onFailedToReceiveAd(ad: Ad) {
                        requestNativeAds()
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView!!.resume()
        }
        if (publisherAdView != null) {
            publisherAdView!!.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (adView != null) {
            adView!!.pause()
        }
        if (publisherAdView != null) {
            publisherAdView!!.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adView != null) {
            adView!!.destroy()
        }
        if (publisherAdView != null) {
            publisherAdView!!.destroy()
        }
        unbinder!!.unbind()
        unbinder = null
    }

    fun shareApp() {
        val blacklist = arrayOf("com.any.package", "net.other.package")
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"

        var shareBody = getString(R.string.share_boday)

        shareBody = "$shareBody at: $GOOGLE_PLAY_APP_LINK"

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        val title = getString(R.string.share_app_title)

        startActivity(Intent.createChooser(sharingIntent, title))
    }

    private fun generateCustomChooserIntent(prototype: Intent, forbiddenChoices: Array<String>): Intent {
        val targetedShareIntents = ArrayList<Intent>()
        val intentMetaInfo = ArrayList<HashMap<String, String>>()
        val chooserIntent: Intent

        val dummy = Intent(prototype.action)
        dummy.type = prototype.type
        val resInfo = packageManager.queryIntentActivities(dummy, 0)

        if (!resInfo.isEmpty()) {
            for (resolveInfo in resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(*forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue

                val info = HashMap<String, String>()
                info["packageName"] = resolveInfo.activityInfo.packageName
                info["className"] = resolveInfo.activityInfo.name
                info["simpleName"] = resolveInfo.activityInfo.loadLabel(packageManager).toString()
                intentMetaInfo.add(info)
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo) { map, map2 -> map["simpleName"]!!.compareTo(map2["simpleName"]!!) }

                // create the custom intent list
                for (metaInfo in intentMetaInfo) {
                    val targetedShareIntent = prototype.clone() as Intent
                    targetedShareIntent.setPackage(metaInfo["packageName"])
                    targetedShareIntent.setClassName(metaInfo["packageName"]!!, metaInfo["className"]!!)
                    targetedShareIntents.add(targetedShareIntent)
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.removeAt(targetedShareIntents.size - 1), getString(R.string.share_app_title))
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toTypedArray<Parcelable>())
                return chooserIntent
            }
        }

        return Intent.createChooser(prototype, getString(R.string.share_app_title))
    }

    protected fun launchMarket() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }

    }

    protected fun showAbout() {
        val fragment = AboutFragment()
        fragment.show(supportFragmentManager, AboutFragment.TAG)
    }

    protected fun openPlayStreamDialog() {
        val dialog = PlayStreamDialog.newInstance(this)
        dialog.show(supportFragmentManager, PlayStreamDialog.TAG)
    }

    @Optional
    @OnClick(R.id.btn_share)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_share -> shareApp()
        }
    }

    companion object {

        val TAG = BaseActivity::class.java.simpleName
        private val GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
    }

}
