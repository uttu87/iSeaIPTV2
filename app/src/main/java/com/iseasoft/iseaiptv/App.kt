package com.iseasoft.iseaiptv

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.permissions.IseaSoft
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import com.startapp.sdk.ads.nativead.NativeAdDetails
import java.io.IOException
import java.io.InputStream
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mSelf = this
        val localImageLoaderConfiguration = ImageLoaderConfiguration.Builder(this).imageDownloader(object : BaseImageDownloader(this) {
            internal var prefs = PreferencesUtility.getInstance(this@App)

            @Throws(IOException::class)
            override fun getStreamFromNetwork(imageUri: String, extra: Any): InputStream {
                if (prefs.loadArtistAndAlbumImages())
                    return super.getStreamFromNetwork(imageUri, extra)
                throw IOException()
            }
        }).build()

        ImageLoader.getInstance().init(localImageLoaderConfiguration)
        IseaSoft.init(this)
    }

    companion object {

        var screenCount = 0
        private var mSelf: App? = null
        private var baseUrl = "https://raw.githubusercontent.com/uttu87/livetv/master/am.m3u"
        var isUseOnlineData = true
        var isActiveAds = true
        var isUseAdMob = true
        var isUseStartApp = false
        var isUseRichAdx = false
        var todayHighlightStatus: String? = null
        var interstitialAdsLimit: Long = 5
        var adsType: Long = 1
        var admobAppId = ""
        var admobBannerId = ""
        var admobInterstitialId = ""
        var publisherBannerId = ""
        var publisherInterstitialId = ""
        var publisherNativeId = ""
        private var startAppId = "211383720"
        var timeDelayToShowAds: Long = 0
        var channelList = ArrayList<M3UItem>()
            set(channelList) {
                App.channelList.clear()
                App.channelList.addAll(channelList)
            }
        var nativeAdDetails = ArrayList<NativeAdDetails>()
            set(nativeAdDetails) {
                App.nativeAdDetails.clear()
                App.nativeAdDetails.addAll(nativeAdDetails)
            }

        fun getBaseUrl(): String {
            return baseUrl
        }

        fun setBaseUrl(baseUrl: String) {
            if (!TextUtils.isEmpty(baseUrl)) {
                App.baseUrl = baseUrl
            }
        }

        val isDebugBuild: Boolean
            get() = BuildConfig.BUILD_TYPE == "debug"

        fun getStartAppId(): String {
            return startAppId
        }

        fun setStartAppId(startappId: String) {
            if (!TextUtils.isEmpty(startappId)) {
                App.startAppId = startappId
            }
        }

        fun self(): App? {
            return mSelf
        }

        val application: App?
            get() = self()

        val context: Context
            get() = application!!.applicationContext
    }
}
