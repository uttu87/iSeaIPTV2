package com.iseasoft.iseaiptv.listeners


import com.iseasoft.iseaiptv.models.M3UItem

interface OnChannelListener {
    fun onChannelClicked(item: M3UItem)
}
