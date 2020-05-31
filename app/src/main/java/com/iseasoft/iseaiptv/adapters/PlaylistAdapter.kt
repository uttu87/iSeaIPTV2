package com.iseasoft.iseaiptv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import java.util.*

class PlaylistAdapter(private val mItems: ArrayList<Playlist>, private val mItemListener: OnPlaylistListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        mContext = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, null)

        return PlaylistHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val playlistHolder = holder as PlaylistHolder
        val playlist = mItems[position]
        playlistHolder.setContent(playlist)
        playlistHolder.listener = mItemListener
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class PlaylistHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        internal var mainView: ConstraintLayout
        internal var icon: ImageView
        internal var name: TextView
        internal var link: TextView
        internal var more: ImageView

        private var playlist: Playlist? = null
        var listener: OnPlaylistListener? = null

        init {
            mainView = view.findViewById(R.id.mainView)
            mainView.setOnClickListener(this)
            icon = view.findViewById(R.id.icon)
            name = view.findViewById(R.id.name)
            link = view.findViewById(R.id.link)
            more = view.findViewById(R.id.more)
            more.setOnClickListener(this)
        }

        fun setContent(playlist: Playlist) {
            this.playlist = playlist
            if (playlist.link?.trim { it <= ' ' }?.startsWith("http")!!) {
                icon.setImageResource(R.drawable.ic_link_black_24dp)
            } else {
                icon.setImageResource(R.drawable.ic_file_black_24dp)
            }
            name.text = playlist.name
            link.text = playlist.link
        }

        override fun onClick(v: View) {
            if (v.id == R.id.mainView) {
                if (listener != null) {
                    playlist?.let { listener!!.onPlaylistItemClicked(it) }
                }
            } else if (v.id == R.id.more) {
                val popupMenu = PopupMenu(mContext!!, more)
                popupMenu.inflate(R.menu.menu_playlist_options)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_open -> if (listener != null) {
                            playlist?.let { listener!!.onPlaylistItemClicked(it) }
                        }
                        R.id.action_delete -> delete()
                    }
                    false
                }
                popupMenu.show()
            }
        }

        private fun delete() {
            val pos = layoutPosition
            notifyItemRemoved(pos)
            mItems.remove(playlist)
            Collections.reverse(mItems)
            PreferencesUtility.getInstance(mContext).savePlaylist(mItems)
            Collections.reverse(mItems)
        }
    }
}
