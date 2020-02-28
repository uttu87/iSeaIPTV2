package com.iseasoft.iseaiptv.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.ui.fragment.ChannelFragment;

public class ChannelActivity extends BaseActivity {
    public static final String TAG = ChannelActivity.class.getSimpleName();
    public static final String CATALOG_KEY = "catalog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_channel);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        String catalog = getString(R.string.all_channels);
        if (getIntent().getExtras() != null) {
            String extraValue = getIntent().getExtras().getString(CATALOG_KEY);
            if(!TextUtils.isEmpty(extraValue)) {
                catalog = extraValue;
            }
        }

        getSupportActionBar().setTitle(catalog);

        setupPlaylist(catalog);
    }

    private void setupPlaylist(String catalog) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ChannelFragment playlistFragment = ChannelFragment.newInstance(catalog);
        ft.replace(R.id.playlist_container, playlistFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
