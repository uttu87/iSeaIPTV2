package com.iseasoft.iseaiptv.dialogs

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.OnConfirmationDialogListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity.Companion.CHANNEL_KEY
import java.util.*

class PlayStreamDialog : ConfirmationDialog() {

    private var txtPlaylistUrl: TextInputEditText? = null

    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            txtPlaylistUrl = view.findViewById(R.id.txt_url)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "", e)
        }

    }

    companion object {
        val TAG = PlayStreamDialog::class.java.simpleName

        fun newInstance(context: Activity): PlayStreamDialog {
            val fragment = PlayStreamDialog()
            fragment.title = context.getString(R.string.open_network_stream)
            fragment.description = null
            fragment.okText = context.getString(R.string.action_play)
            fragment.cancelText = context.getString(R.string.cancel)
            fragment.moduleLayout = R.layout.item_play_stream_popup
            fragment.isQuitPopup = true
            fragment.onConfirmationDialogListener = object : OnConfirmationDialogListener {
                override fun onConfirmed() {
                    val playlistUrl = fragment.txtPlaylistUrl!!.text!!.toString()
                    if (!TextUtils.isEmpty(playlistUrl)) {
                        val channel = M3UItem()
                        channel.itemUrl = playlistUrl
                        channel.itemName = playlistUrl.substring(playlistUrl.lastIndexOf("/") + 1)

                        val bundle = Bundle()
                        bundle.putSerializable(CHANNEL_KEY, channel)
                        val playlist = ArrayList<M3UItem>()
                        playlist.add(channel)
                        //bundle.putSerializable(PLAYLIST_KEY, playlist);
                        App.channelList = playlist
                        Router.navigateTo(context, Router.Screens.PLAYER, bundle, false)
                    }

                }

                override fun onCanceled() {

                }
            }
            return fragment
        }
    }
}
