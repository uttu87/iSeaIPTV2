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

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.listeners.OnConfirmationDialogListener;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;

public class AddUrlDialog extends ConfirmationDialog {
    public static final String TAG = AddUrlDialog.class.getSimpleName();

    private TextInputEditText txtPlaylistUrl;
    private TextInputEditText txtPlaylistName;

    public static AddUrlDialog newInstance(Activity context) {
        AddUrlDialog fragment = new AddUrlDialog();
        fragment.title = context.getString(R.string.action_add_url);
        fragment.description = context.getString(R.string.add_url_message);
        fragment.okText = context.getString(R.string.ok);
        fragment.cancelText = context.getString(R.string.cancel);
        fragment.moduleLayout = R.layout.item_add_url_popup;
        fragment.isQuitPopup = true;
        fragment.onConfirmationDialogListener = new OnConfirmationDialogListener() {
            @Override
            public void onConfirmed() {
                final String playlistUrl = fragment.txtPlaylistUrl.getText().toString();
                if (!TextUtils.isEmpty(playlistUrl)) {
                    Playlist playlist = new Playlist();
                    playlist.setLink(playlistUrl);
                    final String playlistName = fragment.txtPlaylistName.getText().toString();
                    if (!TextUtils.isEmpty(playlistName)) {
                        playlist.setName(playlistName);
                    } else {
                        playlist.setName(playlistUrl.substring(playlistUrl.lastIndexOf("/") + 1));
                    }
                    PreferencesUtility.getInstance(context).savePlaylist(playlist);
                    Router.navigateToMainScreen(context, true);
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
            txtPlaylistName = view.findViewById(R.id.txt_name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "", e);
        }
    }
}
