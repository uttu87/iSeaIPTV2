package com.iseasoft.iseaiptv.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.BuildConfig
import com.iseasoft.iseaiptv.Constants
import com.iseasoft.iseaiptv.Constants.ACTIVE_ADS_KEY
import com.iseasoft.iseaiptv.Constants.ADMOB_APP_ID
import com.iseasoft.iseaiptv.Constants.ADMOB_BANNER_ID
import com.iseasoft.iseaiptv.Constants.ADMOB_INTERSTITIAL_ID
import com.iseasoft.iseaiptv.Constants.ADS_TYPE
import com.iseasoft.iseaiptv.Constants.BASE_URL
import com.iseasoft.iseaiptv.Constants.INTERSTITIAL_ADS_LIMIT
import com.iseasoft.iseaiptv.Constants.PUBLISHER_BANNER_ID
import com.iseasoft.iseaiptv.Constants.PUBLISHER_INTERSTITIAL_ID
import com.iseasoft.iseaiptv.Constants.PUBLISHER_NATIVE_ID
import com.iseasoft.iseaiptv.Constants.START_APP_ID
import com.iseasoft.iseaiptv.Constants.TIME_DELAY_TO_SHOW_ADS
import com.iseasoft.iseaiptv.Constants.TODAY_HIGHLIGHT_STATUS
import com.iseasoft.iseaiptv.Constants.USE_ADMOB
import com.iseasoft.iseaiptv.Constants.USE_ONLINE_DATA_FLAG_KEY
import com.iseasoft.iseaiptv.Constants.USE_RICHADX
import com.iseasoft.iseaiptv.Constants.USE_STARTAPP
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.api.APIListener
import com.iseasoft.iseaiptv.api.IndiaTvAPI
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        setupFirebaseRemoteConfig()
    }

    private fun getAppConfig() {
        IndiaTvAPI.getInstance().getConfig(object : APIListener<Task<QuerySnapshot>> {
            override fun onRequestCompleted(tasks: Task<QuerySnapshot>, json: String) {
                var isActiveAds = false
                var useOnlineData = false
                if (tasks.isSuccessful) {
                    for (document in tasks.result!!) {
                        try {
                            val jsonObject = JSONObject(document.data)
                            if (jsonObject.has(ACTIVE_ADS_KEY)) {
                                isActiveAds = jsonObject.getBoolean(ACTIVE_ADS_KEY)
                            }

                            if (jsonObject.has(USE_ONLINE_DATA_FLAG_KEY)) {
                                useOnlineData = jsonObject.getBoolean(USE_ONLINE_DATA_FLAG_KEY)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }

                App.isActiveAds = isActiveAds
                App.isUseOnlineData = useOnlineData

                navigationToMainScreen()

            }

            override fun onError(e: Error) {
                navigationToMainScreen()
            }
        })
    }

    private fun setupFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig!!.setConfigSettings(configSettings)

        mFirebaseRemoteConfig!!.setDefaults(R.xml.remote_config_defaults)

        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        val cacheExpiration: Long = 0 // seconds.
        mFirebaseRemoteConfig!!.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        if (mFirebaseRemoteConfig != null) {
                            mFirebaseRemoteConfig!!.activateFetched()
                        }
                    }
                    applyRemoteConfig()
                    navigationToMainScreen()
                }
        // [END fetch_config_with_callback]
    }

    private fun applyRemoteConfig() {
        //App.setUseOnlineData(mFirebaseRemoteConfig.getBoolean(USE_ONLINE_DATA_FLAG_KEY));
        App.todayHighlightStatus = mFirebaseRemoteConfig!!.getString(TODAY_HIGHLIGHT_STATUS)
        App.isUseAdMob = mFirebaseRemoteConfig!!.getBoolean(USE_ADMOB)
        App.isUseStartApp = mFirebaseRemoteConfig!!.getBoolean(USE_STARTAPP)
        App.isUseRichAdx = mFirebaseRemoteConfig!!.getBoolean(USE_RICHADX)
        App.interstitialAdsLimit = mFirebaseRemoteConfig!!.getLong(INTERSTITIAL_ADS_LIMIT)
        App.adsType = mFirebaseRemoteConfig!!.getLong(ADS_TYPE)
        App.admobAppId = mFirebaseRemoteConfig!!.getString(ADMOB_APP_ID)
        App.admobBannerId = mFirebaseRemoteConfig!!.getString(ADMOB_BANNER_ID)
        App.admobInterstitialId = mFirebaseRemoteConfig!!.getString(ADMOB_INTERSTITIAL_ID)
        App.publisherBannerId = mFirebaseRemoteConfig!!.getString(PUBLISHER_BANNER_ID)
        App.publisherInterstitialId = mFirebaseRemoteConfig!!.getString(PUBLISHER_INTERSTITIAL_ID)
        App.publisherNativeId = mFirebaseRemoteConfig!!.getString(PUBLISHER_NATIVE_ID)
        App.setStartAppId(mFirebaseRemoteConfig!!.getString(START_APP_ID))
        if (!TextUtils.isEmpty(mFirebaseRemoteConfig!!.getString(BASE_URL))) {
            App.setBaseUrl(mFirebaseRemoteConfig!!.getString(BASE_URL))
        }
        App.timeDelayToShowAds = mFirebaseRemoteConfig!!.getLong(TIME_DELAY_TO_SHOW_ADS)
        savePlaylist()
    }

    private fun savePlaylist() {
        val playlist = Playlist()
        playlist.link = App.getBaseUrl()
        playlist.name = getString(R.string.app_name)
        PreferencesUtility.getInstance(this).savePlaylist(playlist)
    }

    private fun navigationToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        val launchIntent = getIntent()
        if (launchIntent != null) {
            val url = launchIntent.getStringExtra(Constants.PUSH_URL_KEY)
            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(Constants.PUSH_URL_KEY, url)
            }

            val message = launchIntent.getStringExtra(Constants.PUSH_MESSAGE)
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(Constants.PUSH_MESSAGE, message)
            }
        }

        startActivity(intent)
        finish()
    }
}
