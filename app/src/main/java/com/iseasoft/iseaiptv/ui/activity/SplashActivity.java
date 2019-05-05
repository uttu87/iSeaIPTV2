package com.iseasoft.iseaiptv.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.iseasoft.iseaiptv.helpers.Router;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(2000);
            navigationToMainScreen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void navigationToMainScreen() {
        Router.navigateTo(this, Router.Screens.MAIN, true);
    }
}
