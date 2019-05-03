package com.iseasoft.iseaiptv.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.http.HttpHandler;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.models.M3UPlaylist;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.parsers.M3UParser;
import com.iseasoft.iseaiptv.permissions.IseaSoft;
import com.iseasoft.iseaiptv.permissions.PermissionCallback;
import com.iseasoft.iseaiptv.ui.fragment.ChannelFragment;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ALL_CHANNELS_TAB = 1;
    private CoordinatorLayout panelLayout;

    private ChannelAdapter channelAdapter;
    private M3UPlaylist mPlaylist;
    private ViewPager viewPager;
    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadChannels();
        }

        @Override
        public void permissionRefused() {

        }
    };

    public M3UPlaylist getPlaylist() {
        return mPlaylist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        panelLayout = findViewById(R.id.panel_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (Utils.isMarshmallow()) {
            requestStoragePermission();
        } else {
            loadChannels();
        }
    }

    private void loadChannels() {
        final Playlist lastPlaylist = PreferencesUtility.getInstance(this).getLastPlaylist();
        if (lastPlaylist != null) {
            displayPlaylistInfo(lastPlaylist);
            if (lastPlaylist.getLink().startsWith("http")) {
                loadServer(lastPlaylist.getLink());
            } else {
                try {
                    File file = new File(lastPlaylist.getLink());
                    InputStream inputStream = new FileInputStream(file);
                    parseAndUpdateUI(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showChannelPlaceholder();
            Router.navigateTo(this, Router.Screens.PLAYLIST);
        }

    }

    private void displayPlaylistInfo(Playlist lastPlaylist) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView playlistName = header.findViewById(R.id.nav_header_title);
        TextView playlistLink = header.findViewById(R.id.nav_header_description);
        playlistName.setText(lastPlaylist.getName());
        playlistLink.setText(lastPlaylist.getLink());
    }

    private void showChannelPlaceholder() {
        //TODO show Main Placeholder
    }

    private void setupViewPager(ViewPager viewPager) {
        GroupChannelAdapter adapter = new GroupChannelAdapter(getSupportFragmentManager());
        LinkedList<String> groupList = new LinkedList<>();
        for (int i = 0; i < mPlaylist.getPlaylistItems().size(); i++) {
            M3UItem m3UItem = mPlaylist.getPlaylistItems().get(i);
            if (groupList.contains(m3UItem.getItemGroup())) {
                continue;
            }
            groupList.add(m3UItem.getItemGroup());
        }
        adapter.addFragment(getString(R.string.favorites));
        adapter.addFragment(getString(R.string.all_channels));
        for (int i = 0; i < groupList.size(); i++) {
            String groupTitle = groupList.get(i);
            if (!TextUtils.isEmpty(groupTitle)) {
                adapter.addFragment(groupTitle);
            }
        }
        viewPager.setAdapter(adapter);
    }

    private void loadServer(String url) {
        //mProgressBar.setVisibility(View.VISIBLE);
        new LoadServer().execute(url);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_playlist:
                navigateToPlaylist();
                break;
            case R.id.nav_share:
                //TODO Share
                break;
            case R.id.nav_about:
                //TODO About
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToPlaylist() {
        Router.navigateTo(this, Router.Screens.PLAYLIST);
    }

    private void requestStoragePermission() {
        if (IseaSoft.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && IseaSoft.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadChannels();
        } else {
            if (IseaSoft.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "iSeaMusic will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                IseaSoft.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                IseaSoft.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    private void parseAndUpdateUI(InputStream inputStream) {

        M3UParser m3UParser = new M3UParser();
        try {
            mPlaylist = m3UParser.parseFile(inputStream);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (viewPager != null) {
                    setupViewPager(viewPager);
                    viewPager.setCurrentItem(ALL_CHANNELS_TAB, true);//Set All channels tab
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class GroupChannelAdapter extends FragmentStatePagerAdapter {
        private final List<String> mFragmentTitles = new ArrayList<>();

        public GroupChannelAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(String title) {
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return ChannelFragment.newInstance(mFragmentTitles.get(position));
        }

        @Override
        public int getCount() {
            return mFragmentTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadServer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

            HttpHandler hh = new HttpHandler();
            InputStream inputStream = hh.makeServiceCall(urls[0]);

            parseAndUpdateUI(inputStream);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        IseaSoft.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
