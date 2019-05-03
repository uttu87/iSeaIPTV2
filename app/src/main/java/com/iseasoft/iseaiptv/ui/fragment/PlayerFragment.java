package com.iseasoft.iseaiptv.ui.fragment;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.exoplayer2.util.EventLogger;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.FragmentEventListener;
import com.iseasoft.iseaiptv.models.M3UItem;

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
        OnCompletionListener, OnErrorListener {
    private static final String TAG = PlayerFragment.class.getSimpleName();
    private static final float BALANCED_VISIBLE_FRACTION = 0.5625f;
    private static final long OSD_DISP_TIME = 3000;
    private static final int MAX_RETRY_COUNT = 5;

    Unbinder unbinder;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.thumbnail_layout)
    FrameLayout thumbnailLayout;
    @BindView(R.id.thumbnail_image_view)
    ImageView thumbnailImage;
    @BindView(R.id.thumbnail_seek_time)
    TextView thumbnailSeekTextView;

    private M3UItem mChannel;
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
            mVideoUrl = mChannel.getItemUrl();

        }
        mHeight = 0;
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
        }

        return view;
    }

    private void setupVideoView() {
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
        if (mChannel != null) {
            mVideoController.setTitle(mChannel.getItemName());
        }

        //For now we just picked an arbitrary item to play
        videoView.setVideoURI(Uri.parse(mVideoUrl));
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
        if (!isFullscreen) {
            //((PlayerActivity) getActivity()).setupFullScreenAds();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoController != null) {
            mVideoController.setReloadButtonClickListener(null);
            mVideoController.setScreenModeChangeButtonClickListener(null);
        }
        mVideoController = null;
        mChannel = null;
        fragmentEventListener = null;
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
        }
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
            showAds();
            return;
        }
        if (mVideoController != null) {
            mVideoController.setReloadButtonVisible(true);
        }
        showAds();
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
            showAds();
            return false;
        }

        if (mVideoController != null) {
            mVideoController.finishLoading();
            mVideoController.setReloadButtonVisible(true);
        }
        return false;
    }
}
