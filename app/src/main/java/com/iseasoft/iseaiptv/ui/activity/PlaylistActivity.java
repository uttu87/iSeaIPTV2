package com.iseasoft.iseaiptv.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.dialogs.AddUrlDialog;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.ui.fragment.PlaylistFragment;

public class PlaylistActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_playlist);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupPlaylist();
        setupPublisherAds();
    }

    private void setupPlaylist() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        PlaylistFragment playlistFragment = PlaylistFragment.newInstance();
        ft.replace(R.id.playlist_container, playlistFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_url) {
            showAddUrlDialog();
            return true;
        }

        if (id == R.id.action_select_file) {
            navigateToSelectFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAddUrlDialog() {
        AddUrlDialog.newInstance(this).show(getSupportFragmentManager(), AddUrlDialog.TAG);
    }

    private void navigateToSelectFile() {
        Router.navigateTo(this, Router.Screens.SELECT_FILE);
    }

}
