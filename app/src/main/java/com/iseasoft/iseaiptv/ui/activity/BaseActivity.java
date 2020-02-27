package com.iseasoft.iseaiptv.ui.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.Lifecycle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.BuildConfig;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.dialogs.PlayStreamDialog;
import com.iseasoft.iseaiptv.ui.fragment.AboutFragment;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public abstract class BaseActivity extends InterstitialActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private static final String GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    Unbinder unbinder;
    @BindView(R.id.footer_container)
    LinearLayout footerContainer;
    PublisherAdView publisherAdView;
    Banner banner;
    private AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        //initAdmob();
        initStartAppSdk();
        setupStartAppBanner();
        //setupAdmob();

    }

    protected void setupAdmob() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(App.getAdmobBannerId());
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (adView != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    adView.setLayoutParams(params);
                    footerContainer.removeView(adView);
                    footerContainer.addView(adView);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupPublisherAds();
            }
        });
    }

    private void initAdmob() {
        MobileAds.initialize(this, App.getAdmobAppId());
    }

    private void setupPublisherAds() {
        setupPublisherBannerAds();
    }


    private void setupPublisherBannerAds() {
        publisherAdView = new PublisherAdView(this);
        publisherAdView.setAdUnitId(App.getPublisherBannerId());
        publisherAdView.setAdSizes(AdSize.BANNER);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();
        publisherAdView.loadAd(adRequest);
        publisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (publisherAdView != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    publisherAdView.setLayoutParams(params);
                    footerContainer.removeView(publisherAdView);
                    footerContainer.addView(publisherAdView);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupStartAppBanner();
            }
        });
    }

    protected void setupStartAppBanner() {
        banner = new Banner(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        banner.setLayoutParams(params);
        footerContainer.removeView(banner);
        footerContainer.addView(banner);
        banner.loadAd();
    }

    protected void initStartAppSdk() {
        StartAppSDK.init(this, App.getStartAppId(), true);
        StartAppAd.disableSplash();
        requestNativeAds();
    }

    private void requestNativeAds() {
        if (App.getNativeAdDetails().size() > 0) {
            return;
        }
        int numberOfAds = 3;
        StartAppNativeAd mStartAppNativeAd = new StartAppNativeAd(this);
        mStartAppNativeAd.loadAd(
                new NativeAdPreferences()
                        .setAdsNumber(numberOfAds)
                        .setAutoBitmapDownload(true)
                        .setPrimaryImageSize(2),
                new AdEventListener() {
                    @Override
                    public void onReceiveAd(Ad ad) {
                        App.setNativeAdDetails(mStartAppNativeAd.getNativeAds());
                    }

                    @Override
                    public void onFailedToReceiveAd(Ad ad) {
                        requestNativeAds();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (publisherAdView != null) {
            publisherAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
        if (publisherAdView != null) {
            publisherAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        if (publisherAdView != null) {
            publisherAdView.destroy();
        }
        unbinder.unbind();
        unbinder = null;
    }


    protected boolean isStateSafe() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }

    public void shareApp() {
        String[] blacklist = new String[]{"com.any.package", "net.other.package"};
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = getString(R.string.share_boday, getString(R.string.app_name));

        shareBody = shareBody + " at: " + GOOGLE_PLAY_APP_LINK;

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject, getString(R.string.app_name)));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        String title = getString(R.string.share_app_title);

        startActivity(Intent.createChooser(sharingIntent, title));
    }

    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;

        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;

                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.share_app_title));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, getString(R.string.share_app_title));
    }

    protected void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    private boolean isLastBackStack() {
        return isLastActivityStack() && isLastFragmentStack();
    }

    private boolean isLastActivityStack() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> latestTask = am.getRunningTasks(1);
        if (latestTask == null && latestTask.size() != 1) {
            return false;
        }
        ActivityManager.RunningTaskInfo task = latestTask.get(0);
        return task.numActivities == 1
                && task.topActivity.getClassName().equals(this.getClass().getName());
    }

    private boolean isLastFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        return fm.getBackStackEntryCount() == 0;
    }

    protected void showAbout() {
        AboutFragment fragment = new AboutFragment();
        fragment.show(getSupportFragmentManager(), AboutFragment.TAG);
    }

    protected void openPlayStreamDialog() {
        PlayStreamDialog dialog = PlayStreamDialog.newInstance(this);
        dialog.show(getSupportFragmentManager(), PlayStreamDialog.TAG);
    }

    @Optional()
    @OnClick({R.id.btn_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_share:
                shareApp();
                break;
        }
    }

}
