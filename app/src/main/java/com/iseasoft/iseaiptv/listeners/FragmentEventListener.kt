package com.iseasoft.iseaiptv.listeners

interface FragmentEventListener {
    fun changeScreenMode(isFullScreen: Boolean, isUserSelect: Boolean)
    fun onPlayChannel()
}
