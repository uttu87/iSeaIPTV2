package com.iseasoft.iseaiptv.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iseasoft.iseaiptv.Constants;
import com.iseasoft.iseaiptv.R;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int allChannelTabIndex = 1;
    private CoordinatorLayout panelLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ConstraintLayout placeholderContainer;
    private ProgressBar progressBar;

    private M3UPlaylist mPlaylist;
    private GroupChannelAdapter adapter;
    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadChannels();
        }

        @Override
        public void permissionRefused() {
            requestStoragePermission();
        }
    };

    public M3UPlaylist getPlaylist() {
        return mPlaylist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        panelLayout = findViewById(R.id.panel_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        placeholderContainer = findViewById(R.id.placeholder_container);
        progressBar = findViewById(R.id.progressBar);

        checkToPlayFromPushNotification();
        loadChannels();
    }

    private void checkToPlayFromPushNotification() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(Constants.PUSH_URL_KEY);
            String message = intent.getStringExtra(Constants.PUSH_MESSAGE);

            if (!TextUtils.isEmpty(url)) {
                if (checkPlayableUrl(url)) {
                    Intent playerIntent = new Intent(this, PlayerActivity.class);
                    playerIntent.putExtra(Constants.PUSH_URL_KEY, url);
                    playerIntent.putExtra(Constants.PUSH_MESSAGE, message);
                    startActivity(playerIntent);
                } else {
                    Playlist playlist = new Playlist();
                    playlist.setName(message);
                    playlist.setLink(url);
                    PreferencesUtility.getInstance(this).savePlaylist(playlist);
                }
            }
        }
    }

    private boolean checkPlayableUrl(String url) {
        if (url.contains(".m3u8") || url.contains(".ts") || url.contains(".mp4")) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    parsePlaylist(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    updateUI();
                }
            }
        } else {
            updateUI();
        }

    }

    private void displayPlaylistInfo(Playlist lastPlaylist) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView playlistName = header.findViewById(R.id.nav_header_title);
        TextView playlistLink = header.findViewById(R.id.nav_header_description);
        playlistName.setText(lastPlaylist.getName());
        //playlistLink.setText(lastPlaylist.getLink());
        getSupportActionBar().setTitle(lastPlaylist.getName());
    }

    private void showChannelPlaceholder() {
        tabLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        placeholderContainer.setVisibility(View.VISIBLE);
        Button btnAdd = findViewById(R.id.btn_add_playlist);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPlaylist();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        if (adapter == null) {
            adapter = new GroupChannelAdapter(getSupportFragmentManager());
        }
        adapter.addFragment(getString(R.string.favorites));
        if (!PreferencesUtility.getInstance(this).hasNoHistoryWatching()) {
            adapter.addFragment(getString(R.string.history_watching));
            allChannelTabIndex = 2;
        }

        LinkedList<String> groupList = new LinkedList<>();
        if (mPlaylist != null) {
            for (int i = 0; i < mPlaylist.getPlaylistItems().size(); i++) {
                M3UItem m3UItem = mPlaylist.getPlaylistItems().get(i);
                if (groupList.contains(m3UItem.getItemGroup())) {
                    continue;
                }
                groupList.add(m3UItem.getItemGroup());
            }
            adapter.addFragment(getString(R.string.all_channels));
            for (int i = 0; i < groupList.size(); i++) {
                String groupTitle = groupList.get(i);
                if (!TextUtils.isEmpty(groupTitle)) {
                    adapter.addFragment(groupTitle);
                }
            }
        }

        viewPager.setAdapter(adapter);
        tabLayout.setVisibility(View.VISIBLE);
        placeholderContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
//            case R.id.nav_playlist:
//                navigateToPlaylist();
//                break;
//            case R.id.nav_live_stream:
//                openPlayStreamDialog();
//                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_rate:
                launchMarket();
                break;
            case R.id.nav_about:
                showAbout();
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
                Snackbar.make(panelLayout, getString(R.string.request_storage_permission_message_load,
                        getString(R.string.app_name)),
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

    private void parsePlaylist(InputStream inputStream) {

        M3UParser m3UParser = new M3UParser();
        try {
            mPlaylist = m3UParser.parseFile(inputStream);
            updateUI();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (viewPager != null) {
                setupViewPager(viewPager);
                viewPager.setCurrentItem(allChannelTabIndex, true);//Set All channels tab
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        IseaSoft.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

            parsePlaylist(inputStream);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
