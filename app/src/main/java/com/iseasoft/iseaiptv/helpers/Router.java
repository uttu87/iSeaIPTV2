package com.iseasoft.iseaiptv.helpers;

import android.app.Activity;
import android.content.Intent;

import com.iseasoft.iseaiptv.ui.activity.MainActivity;

public class Router {

    public static void navigateToMainScreen(Activity activity, boolean finishAll) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        if (!finishAll) {
            return;
        }
        activity.finishAffinity();
    }
}
