package com.iseasoft.iseaiptv.dialogs

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.OnConfirmationDialogListener
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.utils.PreferencesUtility

class AddUrlDialog : ConfirmationDialog() {

    private var txtPlaylistUrl: TextInputEditText? = null
    private var txtPlaylistName: TextInputEditText? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            txtPlaylistUrl = view.findViewById(R.id.txt_url)
            txtPlaylistName = view.findViewById(R.id.txt_name)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "", e)
        }

    }

    companion object {
        val TAG = AddUrlDialog::class.java.simpleName

        fun newInstance(context: Activity): AddUrlDialog {
            val fragment = AddUrlDialog()
            fragment.title = context.getString(R.string.action_add_url)
            fragment.description = context.getString(R.string.add_url_message)
            fragment.okText = context.getString(R.string.ok)
            fragment.cancelText = context.getString(R.string.cancel)
            fragment.moduleLayout = R.layout.item_add_url_popup
            fragment.isQuitPopup = true
            fragment.onConfirmationDialogListener = object : OnConfirmationDialogListener {
                override fun onConfirmed() {
                    val playlistUrl = fragment.txtPlaylistUrl!!.text!!.toString()
                    if (!TextUtils.isEmpty(playlistUrl)) {
                        val playlist = Playlist()
                        val link = playlistUrl.trim { it <= ' ' }
                        playlist.link = link
                        val playlistName = fragment.txtPlaylistName!!.text!!.toString()
                        if (!TextUtils.isEmpty(playlistName)) {
                            playlist.name = playlistName
                        } else {
                            playlist.name = playlistUrl.substring(playlistUrl.lastIndexOf("/") + 1)
                        }
                        PreferencesUtility.getInstance(context).savePlaylist(playlist)
                        Router.navigateToMainScreen(context, true)
                    }

                }

                override fun onCanceled() {

                }
            }
            return fragment
        }
    }
}
