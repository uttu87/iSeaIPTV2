package com.iseasoft.iseaiptv.dialogs;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.listeners.OnConfirmationDialogListener;
import com.iseasoft.iseaiptv.models.M3UItem;

import java.util.ArrayList;

import static com.iseasoft.iseaiptv.ui.activity.PlayerActivity.CHANNEL_KEY;

public class PlayStreamDialog extends ConfirmationDialog {
    public static final String TAG = PlayStreamDialog.class.getSimpleName();

    private TextInputEditText txtPlaylistUrl;

    public static PlayStreamDialog newInstance(Activity context) {
        PlayStreamDialog fragment = new PlayStreamDialog();
        fragment.title = context.getString(R.string.open_network_stream);
        fragment.description = null;
        fragment.okText = context.getString(R.string.action_play);
        fragment.cancelText = context.getString(R.string.cancel);
        fragment.moduleLayout = R.layout.item_play_stream_popup;
        fragment.isQuitPopup = true;
        fragment.onConfirmationDialogListener = new OnConfirmationDialogListener() {
            @Override
            public void onConfirmed() {
                final String playlistUrl = fragment.txtPlaylistUrl.getText().toString();
                if (!TextUtils.isEmpty(playlistUrl)) {
                    M3UItem channel = new M3UItem();
                    channel.setItemUrl(playlistUrl);
                    channel.setItemName(playlistUrl.substring(playlistUrl.lastIndexOf("/") + 1));

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CHANNEL_KEY, channel);
                    ArrayList<M3UItem> playlist = new ArrayList<>();
                    playlist.add(channel);
                    //bundle.putSerializable(PLAYLIST_KEY, playlist);
                    App.setChannelList(playlist);
                    Router.navigateTo(context, Router.Screens.PLAYER, bundle, false);
                }

            }

            @Override
            public void onCanceled() {

            }
        };
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            txtPlaylistUrl = view.findViewById(R.id.txt_url);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "", e);
        }
    }
}
