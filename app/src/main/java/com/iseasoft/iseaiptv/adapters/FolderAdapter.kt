package com.iseasoft.iseaiptv.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.dataloaders.FolderLoader
import com.iseasoft.iseaiptv.dataloaders.SongLoader
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.FolderListener
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.models.Song
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import com.iseasoft.iseaiptv.widgets.BubbleTextGetter
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import java.io.File
import java.util.*

/**
 * Created by nv95 on 10.11.16.
 */

class FolderAdapter(private val mContext: Activity, root: File) : BaseSongAdapter<FolderAdapter.ItemHolder>(), BubbleTextGetter {

    private val mIcons: Array<Drawable>
    private var mFileSet: List<File>? = null
    private val mSongs: MutableList<Song>
    private var mRoot: File? = null
    private var mBusy = false

    var folderListener: FolderListener? = null

    init {
        mIcons = arrayOf<Drawable>(ContextCompat.getDrawable(mContext, R.drawable.ic_folder_open_black_24dp)!!,
                ContextCompat.getDrawable(mContext, R.drawable.ic_folder_parent_dark)!!,
                ContextCompat.getDrawable(mContext, R.drawable.ic_file_music_dark)!!,
                ContextCompat.getDrawable(mContext, R.drawable.ic_timer_wait)!!)
        mSongs = ArrayList()
        updateDataSet(root)
    }

    fun applyTheme(dark: Boolean) {
        val cf = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        for (d in mIcons) {
            if (dark) {
                d.colorFilter = cf
            } else {
                d.clearColorFilter()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FolderAdapter.ItemHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_folder_list, viewGroup, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(itemHolder: FolderAdapter.ItemHolder, i: Int) {
        val localItem = mFileSet!![i]
        val song = mSongs[i]
        itemHolder.title.text = localItem.name
        if (localItem.isDirectory) {
            itemHolder.albumArt.setImageDrawable(if (".." == localItem.name) mIcons[1] else mIcons[0])
        } else {
            ImageLoader.getInstance().displayImage(Utils.getAlbumArtUri(song.albumId).toString(),
                    itemHolder.albumArt,
                    DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(mIcons[2])
                            .resetViewBeforeLoading(true).build())
        }
    }

    override fun getItemCount(): Int {
        return mFileSet!!.size
    }

    @Deprecated("")
    fun updateDataSet(newRoot: File) {
        if (mBusy) {
            return
        }
        if (".." == newRoot.name) {
            goUp()
            return
        }
        mRoot = newRoot
        mFileSet = FolderLoader.getMediaFiles(newRoot, true)
        getSongsForFiles(mFileSet!!)
    }

    @Deprecated("")
    fun goUp(): Boolean {
        if (mRoot == null || mBusy) {
            return false
        }
        val parent = mRoot!!.parentFile
        if (parent != null && parent.canRead()) {
            updateDataSet(parent)
            return true
        } else {
            return false
        }
    }

    fun goUpAsync(): Boolean {
        if (mRoot == null || mBusy) {
            return false
        }
        val parent = mRoot!!.parentFile
        return if (parent != null && parent.canRead()) {
            updateDataSetAsync(parent)
        } else {
            false
        }
    }

    fun updateDataSetAsync(newRoot: File): Boolean {
        if (mBusy) {
            return false
        }
        if (".." == newRoot.name) {
            goUpAsync()
            return false
        }
        mRoot = newRoot
        NavigateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRoot)
        return true
    }

    override fun getTextToShowInBubble(pos: Int): String {
        if (mBusy || mFileSet!!.size == 0)
            return ""
        try {
            val f = mFileSet!![pos]
            return if (f.isDirectory) {
                f.name[0].toString()
            } else {
                Character.toString(f.name[0])
            }
        } catch (e: Exception) {
            return ""
        }

    }

    private fun getSongsForFiles(files: List<File>) {
        mSongs.clear()
        for (file in files) {
            mSongs.add(SongLoader.getSongFromPath(file.absolutePath, mContext))
        }
    }


    private inner class NavigateTask : AsyncTask<File, Void, List<File>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            mBusy = true
        }

        override fun doInBackground(vararg params: File): List<File> {
            val files = FolderLoader.getMediaFiles(params[0], true)
            getSongsForFiles(files)
            return files
        }

        override fun onPostExecute(files: List<File>) {
            super.onPostExecute(files)
            mFileSet = files
            notifyDataSetChanged()
            mBusy = false
            PreferencesUtility.getInstance(mContext).storeLastFolder(mRoot!!.path)
            if (folderListener != null) {
                folderListener!!.onDirChanged(mRoot!!)
            }
        }
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var title: TextView
        var albumArt: ImageView

        init {
            this.title = view.findViewById<View>(R.id.folder_title) as TextView
            this.albumArt = view.findViewById<View>(R.id.album_art) as ImageView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (mBusy) {
                return
            }
            val f = mFileSet!![adapterPosition]

            if (f.isDirectory && updateDataSetAsync(f)) {
                albumArt.setImageDrawable(mIcons[3])
            } else if (f.isFile) {

                val playlist = Playlist()
                playlist.name = f.name
                playlist.link = f.path

                PreferencesUtility.getInstance(mContext).savePlaylist(playlist)
                if (folderListener != null) {
                    folderListener!!.onFileSelected(f)
                }

                Router.navigateToMainScreen(mContext, true)
            }
        }

    }


}