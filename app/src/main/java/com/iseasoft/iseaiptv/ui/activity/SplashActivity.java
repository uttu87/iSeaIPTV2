package com.iseasoft.iseaiptv.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.BuildConfig;
import com.iseasoft.iseaiptv.Constants;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.api.APIListener;
import com.iseasoft.iseaiptv.api.IndiaTvAPI;

import org.json.JSONException;
import org.json.JSONObject;

import static com.iseasoft.iseaiptv.Constants.ACTIVE_ADS_KEY;
import static com.iseasoft.iseaiptv.Constants.ADMOB_APP_ID;
import static com.iseasoft.iseaiptv.Constants.ADMOB_BANNER_ID;
import static com.iseasoft.iseaiptv.Constants.ADMOB_INTERSTITIAL_ID;
import static com.iseasoft.iseaiptv.Constants.ADS_TYPE;
import static com.iseasoft.iseaiptv.Constants.INTERSTITIAL_ADS_LIMIT;
import static com.iseasoft.iseaiptv.Constants.PUBLISHER_BANNER_ID;
import static com.iseasoft.iseaiptv.Constants.PUBLISHER_INTERSTITIAL_ID;
import static com.iseasoft.iseaiptv.Constants.PUBLISHER_NATIVE_ID;
import static com.iseasoft.iseaiptv.Constants.START_APP_ID;
import static com.iseasoft.iseaiptv.Constants.TIME_DELAY_TO_SHOW_ADS;
import static com.iseasoft.iseaiptv.Constants.TODAY_HIGHLIGHT_STATUS;
import static com.iseasoft.iseaiptv.Constants.USE_ADMOB;
import static com.iseasoft.iseaiptv.Constants.USE_ONLINE_DATA_FLAG_KEY;
import static com.iseasoft.iseaiptv.Constants.USE_RICHADX;
import static com.iseasoft.iseaiptv.Constants.USE_STARTAPP;

public class SplashActivity extends AppCompatActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebaseRemoteConfig();
    }

    private void getAppConfig() {
        IndiaTvAPI.getInstance().getConfig(new APIListener<Task<QuerySnapshot>>() {
            @Override
            public void onRequestCompleted(Task<QuerySnapshot> tasks, String json) {
                boolean isActiveAds = false;
                boolean useOnlineData = false;
                if (tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        try {
                            JSONObject jsonObject = new JSONObject(document.getData());
                            if (jsonObject.has(ACTIVE_ADS_KEY)) {
                                isActiveAds = jsonObject.getBoolean(ACTIVE_ADS_KEY);
                            }

                            if (jsonObject.has(USE_ONLINE_DATA_FLAG_KEY)) {
                                useOnlineData = jsonObject.getBoolean(USE_ONLINE_DATA_FLAG_KEY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                App.setActiveAds(isActiveAds);
                App.setUseOnlineData(useOnlineData);

                navigationToMainScreen();

            }

            @Override
            public void onError(Error e) {
                navigationToMainScreen();
            }
        });
    }

    private void setupFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfig();
    }

    private void fetchRemoteConfig() {
        long cacheExpiration = 3600; // seconds.

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            if (mFirebaseRemoteConfig != null) {
                                mFirebaseRemoteConfig.activateFetched();
                            }
                        }
                        applyRemoteConfig();
                        navigationToMainScreen();
                    }
                });
        // [END fetch_config_with_callback]
    }

    private void applyRemoteConfig() {
        //App.setUseOnlineData(mFirebaseRemoteConfig.getBoolean(USE_ONLINE_DATA_FLAG_KEY));
        App.setTodayHighlightStatus(mFirebaseRemoteConfig.getString(TODAY_HIGHLIGHT_STATUS));
        App.setUseAdMob(mFirebaseRemoteConfig.getBoolean(USE_ADMOB));
        App.setUseStartApp(mFirebaseRemoteConfig.getBoolean(USE_STARTAPP));
        App.setUseRichAdx(mFirebaseRemoteConfig.getBoolean(USE_RICHADX));
        App.setInterstitialAdsLimit(mFirebaseRemoteConfig.getLong(INTERSTITIAL_ADS_LIMIT));
        App.setAdsType(mFirebaseRemoteConfig.getLong(ADS_TYPE));
        App.setAdmobAppId(mFirebaseRemoteConfig.getString(ADMOB_APP_ID));
        App.setAdmobBannerId(mFirebaseRemoteConfig.getString(ADMOB_BANNER_ID));
        App.setAdmobInterstitialId(mFirebaseRemoteConfig.getString(ADMOB_INTERSTITIAL_ID));
        App.setPublisherBannerId(mFirebaseRemoteConfig.getString(PUBLISHER_BANNER_ID));
        App.setPublisherInterstitialId(mFirebaseRemoteConfig.getString(PUBLISHER_INTERSTITIAL_ID));
        App.setPublisherNativeId(mFirebaseRemoteConfig.getString(PUBLISHER_NATIVE_ID));
        App.setStartAppId(mFirebaseRemoteConfig.getString(START_APP_ID));
        App.setTimeDelayToShowAds(mFirebaseRemoteConfig.getLong(TIME_DELAY_TO_SHOW_ADS));
    }

    private void navigationToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        Intent launchIntent = getIntent();
        if (launchIntent != null) {
            String url = launchIntent.getStringExtra(Constants.PUSH_URL_KEY);
            if (!TextUtils.isEmpty(url)) {
                intent.putExtra(Constants.PUSH_URL_KEY, url);
            }

            String message = launchIntent.getStringExtra(Constants.PUSH_MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                intent.putExtra(Constants.PUSH_MESSAGE, message);
            }
        }

        startActivity(intent);
        finish();
    }
}
