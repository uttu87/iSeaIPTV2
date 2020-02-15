package com.iseasoft.iseaiptv.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.iseasoft.iseaiptv.App;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

public class InterstitialActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private PublisherInterstitialAd publisherInterstitialAd;
    private StartAppAd startAppAd;


    private void setupAdmobInterstitialAds() {
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(App.getAdmobInterstitialId());
        }
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (interstitialAd != null) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupPublisherInterstitialAds();
            }
        });
    }

    private void setupPublisherInterstitialAds() {
        if (publisherInterstitialAd == null) {
            publisherInterstitialAd = new PublisherInterstitialAd(this);
            publisherInterstitialAd.setAdUnitId(App.getPublisherInterstitialId());
        }
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();

        publisherInterstitialAd.loadAd(adRequest);

        publisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (publisherInterstitialAd != null) {
                    publisherInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupStartAppAd();
            }
        });
    }

    protected void setupStartAppAd() {
        if (startAppAd == null) {
            startAppAd = new StartAppAd(this);
        }
        startAppAd.loadAd(StartAppAd.AdMode.VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                setupStartAppAd();
            }
        });
    }

    public void setupFullScreenAds() {
        //setupAdmobInterstitialAds();
        setupPublisherInterstitialAds();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        interstitialAd = null;
        publisherInterstitialAd = null;
        startAppAd = null;
    }
}
