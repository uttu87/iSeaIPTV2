package com.iseasoft.iseaiptv;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.permissions.IseaSoft;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class App extends Application {

    public static int screenCount = 0;
    private static App mSelf;
    private static String baseUrl = "https://raw.githubusercontent.com/uttu87/livetv/master/cn.m3u";
    private static boolean useOnlineData = true;
    private static boolean activeAds = true;
    private static boolean useAdMob = true;
    private static boolean useStartApp = false;
    private static boolean useRichAdx = false;
    private static String todayHighlightStatus;
    private static long interstitialAdsLimit = 5;
    private static long adsType = 1;
    private static String admobBannerId = "";
    private static String admobInterstitialId = "";
    private static String startAppId = "204703090";

    private static ArrayList<M3UItem> channelList = new ArrayList<>();

    public static ArrayList<M3UItem> getChannelList() {
        return channelList;
    }

    public static void setChannelList(ArrayList<M3UItem> channelList) {
        App.channelList.clear();
        App.channelList.addAll(channelList);
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl)) {
            App.baseUrl = baseUrl;
        }
    }

    public static boolean isUseOnlineData() {
        return useOnlineData;
    }

    public static void setUseOnlineData(boolean useOnlineData) {
        App.useOnlineData = useOnlineData;
    }

    public static boolean isDebugBuild() {
        return BuildConfig.BUILD_TYPE.equals("debug");
    }

    public static boolean isActiveAds() {
        return activeAds;
    }

    public static void setActiveAds(boolean activeAds) {
        App.activeAds = activeAds;
    }

    public static boolean isUseAdMob() {
        return useAdMob;
    }

    public static void setUseAdMob(boolean useAdMob) {
        App.useAdMob = useAdMob;
    }

    public static boolean isUseStartApp() {
        return useStartApp;
    }

    public static void setUseStartApp(boolean useStartApp) {
        App.useStartApp = useStartApp;
    }

    public static boolean isUseRichAdx() {
        return useRichAdx;
    }

    public static void setUseRichAdx(boolean useRichAdx) {
        App.useRichAdx = useRichAdx;
    }

    public static long getInterstitialAdsLimit() {
        return interstitialAdsLimit;
    }

    public static void setInterstitialAdsLimit(long interstitialAdsLimit) {
        App.interstitialAdsLimit = interstitialAdsLimit;
    }

    public static long getAdsType() {
        return adsType;
    }

    public static void setAdsType(long adsType) {
        App.adsType = adsType;
    }

    public static String getTodayHighlightStatus() {
        return todayHighlightStatus;
    }

    public static void setTodayHighlightStatus(String todayHighlightStatus) {
        App.todayHighlightStatus = todayHighlightStatus;
    }

    public static String getAdmobBannerId() {
        return admobBannerId;
    }

    public static void setAdmobBannerId(String admobBannerId) {
        App.admobBannerId = admobBannerId;
    }

    public static String getAdmobInterstitialId() {
        return admobInterstitialId;
    }

    public static void setAdmobInterstitialId(String admobInterstitialId) {
        App.admobInterstitialId = admobInterstitialId;
    }

    public static String getStartAppId() {
        return startAppId;
    }

    public static void setStartAppId(String startappId) {
        if (!TextUtils.isEmpty(startappId)) {
            App.startAppId = startappId;
        }
    }

    public static App self() {
        return mSelf;
    }

    public static App getApplication() {
        return self();
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).imageDownloader(new BaseImageDownloader(this) {
            PreferencesUtility prefs = PreferencesUtility.getInstance(App.this);

            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                if (prefs.loadArtistAndAlbumImages())
                    return super.getStreamFromNetwork(imageUri, extra);
                throw new IOException();
            }
        }).build();

        ImageLoader.getInstance().init(localImageLoaderConfiguration);
        IseaSoft.init(this);
    }
}
