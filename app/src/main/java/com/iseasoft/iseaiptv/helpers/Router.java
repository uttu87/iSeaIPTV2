package com.iseasoft.iseaiptv.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.iseasoft.iseaiptv.ui.activity.ChannelActivity;
import com.iseasoft.iseaiptv.ui.activity.MainActivity;
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity;
import com.iseasoft.iseaiptv.ui.activity.PlaylistActivity;
import com.iseasoft.iseaiptv.ui.activity.SelectFileActivity;

public class Router {

    /**
     * Navigates to the screen without finishing previous activity
     *
     * @param activity
     * @param screen
     */
    public static void navigateTo(@NonNull Activity activity, @NonNull @Screens int screen) {
        navigateTo(activity, screen, null, false);
    }

    /**
     * Navigates to the screen with finishing previous activity or not
     *
     * @param activity
     * @param screen
     * @param finish
     */
    public static void navigateTo(@NonNull Activity activity, @NonNull @Screens int screen, boolean finish) {
        navigateTo(activity, screen, null, finish);
    }

    /**
     * Navigates to the screen with bundle along with finishing previous activity or not
     *
     * @param activity
     * @param screen
     * @param bundle
     * @param finish
     */
    public static void navigateTo(@NonNull Activity activity, @NonNull @Screens int screen, @Nullable Bundle bundle, boolean finish) {
        Class<?> clasz = null;
        switch (screen) {
            case Screens.MAIN:
                clasz = MainActivity.class;
                break;
            case Screens.PLAYLIST:
                clasz = PlaylistActivity.class;
                break;
            case Screens.SELECT_FILE:
                clasz = SelectFileActivity.class;
                break;
            case Screens.PLAYER:
                clasz = PlayerActivity.class;
                break;
            case Screens.CHANNEL:
                clasz = ChannelActivity.class;
                break;
            default:
                break;
        }
        Intent intent = new Intent(activity, clasz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        if (!finish) return;
        activity.finishAffinity();
    }

    public static void navigateToMainScreen(Activity activity, boolean finish) {
        navigateTo(activity, Screens.MAIN, finish);
    }

    @IntDef(flag = true, value = {
            Screens.MAIN,
            Screens.PLAYLIST,
            Screens.SELECT_FILE,
            Screens.PLAYER,
            Screens.CHANNEL

    })
    public @interface Screens {
        int MAIN = 1 << 1;
        int PLAYLIST = 5;
        int SELECT_FILE = 6;
        int PLAYER = 7;
        int CHANNEL = 8;
    }
}
