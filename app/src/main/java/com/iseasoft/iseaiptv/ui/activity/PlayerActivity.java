package com.iseasoft.iseaiptv.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.Constants;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.FragmentEventListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.ui.fragment.PlayerFragment;
import com.startapp.android.publish.adsCommon.StartAppAd;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements FragmentEventListener {

    public static final String CHANNEL_KEY = "channel";
    public static final String PLAYLIST_KEY = "playlist";

    private PublisherInterstitialAd publisherInterstitialAd;
    private StartAppAd startAppAd;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setupPublisherInterstitialAds();
        }
    };

    private boolean isImmersiveAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
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

    private void setupStartAppAd() {
        if (startAppAd == null) {
            startAppAd = new StartAppAd(this);
        }
        startAppAd.showAd();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player);
        super.onCreate(savedInstanceState);
        M3UItem mChannel;
        ArrayList<M3UItem> mPlaylist = new ArrayList<>();
        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra(Constants.PUSH_URL_KEY))) {
            String matchUrl = getIntent().getStringExtra(Constants.PUSH_URL_KEY);
            String message = getIntent().getStringExtra(Constants.PUSH_MESSAGE);
            mChannel = new M3UItem();
            mChannel.setItemUrl(matchUrl);
            mChannel.setItemName(message);
            //mPlaylist.add(mChannel);
        } else {
            mChannel = (M3UItem) getIntent().getExtras().getSerializable(CHANNEL_KEY);
            //mPlaylist.addAll((ArrayList<M3UItem>) getIntent().getExtras().getSerializable(PLAYLIST_KEY));
        }

        setupPlayer(mChannel);
        mHandler.postDelayed(runnable, App.getTimeDelayToShowAds() * 1000);
    }

    private void setupPlayer(M3UItem channel) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        PlayerFragment playerFragment = PlayerFragment.newInstance(channel);
        playerFragment.setFragmentEventListener(this);
        ft.replace(R.id.player_view, playerFragment, PlayerFragment.TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    private void setupPlayer(M3UItem channel, ArrayList<M3UItem> playlist) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        PlayerFragment playerFragment = PlayerFragment.newInstance(channel, playlist);
        playerFragment.setFragmentEventListener(this);
        ft.replace(R.id.player_view, playerFragment, PlayerFragment.TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }

    public void setFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (isImmersiveAvailable()) {
                flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void exitFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    public void changeScreenMode(boolean isFullScreen, boolean isUserSelect) {
        if (isFullScreen) {
            if (isUserSelect) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            setFullscreen(this);
        } else {
            if (isUserSelect) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            exitFullscreen(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        publisherInterstitialAd = null;
        startAppAd = null;
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        runnable = null;
    }

    @Override
    public void onBackPressed() {
        PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag(PlayerFragment.TAG);
        if (fragment != null && fragment.isShowingPlaylist()) {
            fragment.showPlaylist();
            return;
        }
        super.onBackPressed();
    }
}
