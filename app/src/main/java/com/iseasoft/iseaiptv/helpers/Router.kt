package com.iseasoft.iseaiptv.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.iseasoft.iseaiptv.ui.activity.*

object Router {

    /**
     * Navigates to the screen with finishing previous activity or not
     *
     * @param activity
     * @param screen
     * @param finish
     */
    fun navigateTo(activity: Activity, @Screens screen: Int, finish: Boolean) {
        navigateTo(activity, screen, null, finish)
    }

    /**
     * Navigates to the screen with bundle along with finishing previous activity or not
     *
     * @param activity
     * @param screen
     * @param bundle
     * @param finish
     */
    @JvmOverloads
    fun navigateTo(activity: Activity, @Screens screen: Int, bundle: Bundle? = null, finish: Boolean = false) {
        var clasz: Class<*>? = null
        when (screen) {
            Screens.MAIN -> clasz = MainActivity::class.java
            Screens.PLAYLIST -> clasz = PlaylistActivity::class.java
            Screens.SELECT_FILE -> clasz = SelectFileActivity::class.java
            Screens.PLAYER -> clasz = PlayerActivity::class.java
            Screens.CHANNEL -> clasz = ChannelActivity::class.java
            else -> {
            }
        }
        val intent = Intent(activity, clasz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        activity.startActivity(intent)
        if (!finish) return
        activity.finishAffinity()
    }

    fun navigateToMainScreen(activity: Activity, finish: Boolean) {
        navigateTo(activity, Screens.MAIN, finish)
    }

//    @IntDef(flag = true, value = {
//        Screens.MAIN,
//        Screens.PLAYLIST,
//        Screens.SELECT_FILE,
//        Screens.PLAYER,
//        Screens.CHANNEL
//
//    })
    annotation class Screens {
        companion object {
            val MAIN = 1 shl 1
            val PLAYLIST = 5
            val SELECT_FILE = 6
            val PLAYER = 7
            val CHANNEL = 8
        }
    }
}
/**
 * Navigates to the screen without finishing previous activity
 *
 * @param activity
 * @param screen
 */
