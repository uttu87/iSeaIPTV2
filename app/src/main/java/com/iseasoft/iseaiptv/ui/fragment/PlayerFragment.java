package com.iseasoft.iseaiptv.ui.fragment;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.listeners.FragmentEventListener;
import com.iseasoft.iseaiptv.listeners.OnChannelListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;
import com.startapp.android.publish.ads.banner.Banner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ONE;
import static com.iseasoft.iseaiptv.ui.activity.PlayerActivity.CHANNEL_KEY;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends BaseFragment implements OnPreparedListener, View.OnClickListener,
        OnCompletionListener, OnErrorListener, VideoControlsButtonListener, VideoControlsVisibilityListener {
    public static final String TAG = PlayerFragment.class.getSimpleName();
    private static final float BALANCED_VISIBLE_FRACTION = 0.5625f;
    private static final long OSD_DISP_TIME = 3000;
    private static final int MAX_RETRY_COUNT = 3;

    Unbinder unbinder;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.thumbnail_layout)
    FrameLayout thumbnailLayout;
    @BindView(R.id.thumbnail_image_view)
    ImageView thumbnailImage;
    @BindView(R.id.thumbnail_seek_time)
    TextView thumbnailSeekTextView;
    @BindView(R.id.playlist_container)
    RelativeLayout playlistContainer;
    @BindView(R.id.rv_playlist)
    RecyclerView rvPlaylist;

    private AdView adView;
    private PublisherAdView publisherAdView;
    private Banner banner;

    private M3UItem mChannel;
    private ArrayList<M3UItem> mPlaylist;
    private String mVideoUrl;
    private int playerStatus;
    private boolean isFixedScreen;
    private boolean isFullscreen;
    private long currentPosition;
    private boolean isSeeking;
    private boolean isReloadStatus;
    private int mHeight;
    private FragmentEventListener fragmentEventListener;
    private ISeaLiveVideoController mVideoController;

    private long lastOsdDispTime;
    private boolean nowOn;
    private int mRetryCount = 0;
    private boolean isShowingPlaylist = false;
    private ChannelAdapter adapter;

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(M3UItem item, ArrayList<M3UItem> playlist) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHANNEL_KEY, item);
        //args.putSerializable(PLAYLIST_KEY, playlist);
        fragment.setArguments(args);
        return fragment;
    }

    public static PlayerFragment newInstance(M3UItem item) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHANNEL_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragmentEventListener(FragmentEventListener fragmentEventListener) {
        this.fragmentEventListener = fragmentEventListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChannel = (M3UItem) getArguments().getSerializable(CHANNEL_KEY);
            mPlaylist = App.getChannelList();
            mVideoUrl = mChannel.getItemUrl();

        }
        mHeight = 0;
    }

    private int getChannelPosition() {
        int pos = 0;
        for (int i = 0; i < mPlaylist.size(); i++) {
            M3UItem item = mPlaylist.get(i);
            if (mChannel.getItemName().equals(item.getItemName()) &&
                    mChannel.getItemUrl().equals(item.getItemUrl())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        isFullscreen = true;
        if (savedInstanceState == null) {
            setupVideoView();
            setupPlaylist();
            setupStartAppBanner();
            //setupAdmobBannerAds();
        }

        return view;
    }

    private void setupAdmobBannerAds() {
        adView = new AdView(getActivity());
        adView.setAdUnitId(App.getAdmobBannerId());
        adView.setAdSize(AdSize.BANNER);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (adView != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    playlistContainer.addView(adView, params);

                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupPublisherBannerAds();
            }
        });

    }

    private void setupPublisherBannerAds() {
        publisherAdView = new PublisherAdView(getActivity());
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
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    playlistContainer.addView(publisherAdView, params);

                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                setupStartAppBanner();
            }
        });
    }

    private void setupStartAppBanner() {
        banner = new Banner(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        playlistContainer.addView(banner, params);
        banner.loadAd();
    }

    private void setupPlaylist() {
        if (adapter == null) {
            adapter = new ChannelAdapter(getActivity(), R.layout.item_channel_list, new OnChannelListener() {
                @Override
                public void onChannelClicked(M3UItem item) {
                    mChannel = item;
                    playChannel(mChannel);
                }
            });
        }
        adapter.update(mPlaylist);
        rvPlaylist.setAdapter(adapter);
        adapter.notifyItemChanged(getChannelPosition());
        Utils.modifyListViewForVertical(getActivity(), rvPlaylist);
        isShowingPlaylist = false;
    }

    private void setupVideoView() {
        mRetryCount = 0;
        setUpVideoViewSize(isFullscreen);
        // Make sure to use the correct VideoView import
        if (mVideoController == null) {
            mVideoController = new ISeaLiveVideoController(getContext());
        }
        videoView.setControls(mVideoController);
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.setAnalyticsListener(new EventLogger(null));
        mVideoController.setScreenModeChangeButtonClickListener(this);
        mVideoController.setReloadButtonClickListener(this);
        mVideoController.setPlaylistButtonClickListener(this);
        mVideoController.setFavoriteButtonClickListener(this);
        mVideoController.setButtonListener(this);
        mVideoController.setVisibilityListener(this);
        mVideoController.setPreviousButtonEnabled(true);
        mVideoController.setNextButtonEnabled(true);

        mVideoController.setTitle(mChannel.getItemName());

        //For now we just picked an arbitrary item to play
        playChannel(mChannel);
    }

    private void updateFavoriteIcon() {
        PreferencesUtility preferencesUtility = PreferencesUtility.getInstance(getActivity());
        boolean isFaved = preferencesUtility.checkFavorite(mChannel);
        mVideoController.setFavorited(isFaved);
    }

    private void playChannel(M3UItem channel) {
        mRetryCount = 0;
        videoView.setVideoURI(Uri.parse(channel.getItemUrl()));
        mVideoController.setTitle(channel.getItemName());
        mVideoController.showPlayErrorMessage(false);
        updateFavoriteIcon();
    }

    private void setUpVideoViewSize(boolean isFullscreen) {
        if (isFullscreen) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            videoView.setLayoutParams(params);
        } else {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (mHeight == 0) {
                mHeight = (int) (metrics.widthPixels * BALANCED_VISIBLE_FRACTION + 0.5f);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mHeight
            );

            videoView.setLayoutParams(params);
        }
    }

    @Override
    public void onPrepared() {
        if (!isStateSafe()) {
            return;
        }
        showAds();
        if (videoView != null) {
            videoView.start();
            mRetryCount = 0;
            videoView.setRepeatMode(REPEAT_MODE_ONE);

            if (mVideoController != null) {
                mVideoController.updatePlayPauseImage(true);
                mVideoController.updateScreenModeChangeImage(isFullscreen);
            }
            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            }
        }
    }

    private void showAds() {
        if (mRetryCount == 0) {
            ((PlayerActivity) getActivity()).setupFullScreenAds();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.start();

        }
        if (mVideoController != null) {
            mVideoController.updatePlayPauseImage(true);
        }

        screenModeChange(isFullscreen, false);

        if (publisherAdView != null) {
            publisherAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }

        if (publisherAdView != null) {
            publisherAdView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoController != null) {
            mVideoController.setReloadButtonClickListener(null);
            mVideoController.setScreenModeChangeButtonClickListener(null);
            mVideoController.setButtonListener(null);
            mVideoController.setPlaylistButtonClickListener(null);
            mVideoController.setVisibilityListener(null);
        }
        mVideoController = null;
        mChannel = null;
        mPlaylist = null;
        fragmentEventListener = null;
        if (publisherAdView != null) {
            publisherAdView.destroy();
        }
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        controllerButtonClick(v.getId());
    }

    public void controllerButtonClick(int id) {
        switch (id) {
            case R.id.button_screen_mode_change:
                screenModeChange(!isFullscreen, true);
                break;
            case R.id.exomedia_controls_reload_btn:
                mVideoController.setReloadButtonVisible(false);
                videoView.restart();
                break;
            case R.id.playlist_play:
                showPlaylist();
                break;
            case R.id.favorite:
                favorite();
                break;
        }
    }

    private void favorite() {
        PreferencesUtility preferencesUtility = PreferencesUtility.getInstance(getActivity());
        preferencesUtility.favorite(mChannel);
        updateFavoriteIcon();
    }

    public void showPlaylist() {
        isShowingPlaylist = !isShowingPlaylist;
        playlistContainer.setVisibility(isShowingPlaylist ? View.VISIBLE : View.GONE);
    }

    public void screenModeChange(boolean fullscreen, boolean isUserChange) {
        if (isFixedScreen && !fullscreen) {
            return;
        }
        isFullscreen = fullscreen;
        if (mVideoController != null) {
            mVideoController.updateScreenModeChangeImage(isFullscreen);
        }
        if (videoView != null) {
            currentPosition = videoView.getCurrentPosition();
        }
        if (fragmentEventListener != null) {
            fragmentEventListener.changeScreenMode(isFullscreen, isUserChange);
        }

        setUpVideoViewSize(isFullscreen);
    }

    @Override
    public void onCompletion() {
        if (!isStateSafe()) {
            return;
        }

        if (mRetryCount < MAX_RETRY_COUNT) {
            mRetryCount++;
            videoView.restart();
            return;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenModeChange(true, false);
        }

    }

    @Override
    public boolean onError(Exception e) {
        Log.i(TAG, e.getMessage());
        if (!isStateSafe()) {
            return false;
        }

        if (mRetryCount < MAX_RETRY_COUNT) {
            mRetryCount++;
            videoView.restart();
            return false;
        }

        if (mVideoController != null) {
            mVideoController.showPlayErrorMessage(true);
            mVideoController.finishLoading();
        }
        return false;
    }

    @Override
    public boolean onPlayPauseClicked() {
        return false;
    }

    @Override
    public boolean onPreviousClicked() {
        final int position = getChannelPosition();
        if (position > 1) {
            mChannel = mPlaylist.get(position - 1);
            mVideoUrl = mChannel.getItemUrl();
            playChannel(mChannel);
        }
        return true;
    }

    @Override
    public boolean onNextClicked() {
        final int position = getChannelPosition();
        if (position < mPlaylist.size() - 1) {
            mChannel = mPlaylist.get(position + 1);
            mVideoUrl = mChannel.getItemUrl();
            playChannel(mChannel);
        }
        return true;
    }

    @Override
    public boolean onRewindClicked() {
        return false;
    }

    @Override
    public boolean onFastForwardClicked() {
        return false;
    }

    @Override
    public void onControlsShown() {
        playlistContainer.setVisibility(View.GONE);
    }

    @Override
    public void onControlsHidden() {
    }

    public boolean isShowingPlaylist() {
        return isShowingPlaylist;
    }
}
