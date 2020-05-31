package com.iseasoft.iseaiptv.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iseasoft.iseaiptv.models.Song

/**
 * Created by naman on 7/12/17.
 */

abstract class BaseSongAdapter<V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

    override abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V

    override fun onBindViewHolder(holder: V, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    /*
    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final Utils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        if (context instanceof BaseActivity) {
            CastSession castSession = ((BaseActivity) context).getCastSession();
            if (castSession != null) {
                navigateNowPlaying = false;
                iSeaMusicCastHelper.startCasting(castSession, currentSong);
            } else {
                MusicPlayer.playAll(context, list, position, -1, Utils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, Utils.IdType.NA, false);
        }

        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }


    }
    */
    fun removeSongAt(i: Int) {}

    fun updateDataSet(arraylist: List<Song>) {}

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view)

}
