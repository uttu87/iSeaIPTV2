package com.iseasoft.iseaiptv.listeners


import com.iseasoft.iseaiptv.models.Playlist

interface OnPlaylistListener {
    fun onPlaylistItemClicked(item: Playlist)
}
