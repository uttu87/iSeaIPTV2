package com.iseasoft.iseaiptv.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.appthemeengine.ATE;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.adapters.FolderAdapter;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.http.HttpHandler;
import com.iseasoft.iseaiptv.listeners.FolderListener;
import com.iseasoft.iseaiptv.models.M3UPlaylist;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.parsers.M3UParser;
import com.iseasoft.iseaiptv.permissions.IseaSoft;
import com.iseasoft.iseaiptv.permissions.PermissionCallback;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by nv95 on 10.11.16.
 */

public class ChannelFragment extends Fragment {

    private static final int COLUMN_WIDTH = 70;
    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadChannels();
        }

        @Override
        public void permissionRefused() {

        }
    };
    private RelativeLayout panelLayout;
    private FolderAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private ChannelAdapter channelAdapter;

    private FolderListener listener;

    public static ChannelFragment newInstance() {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FolderListener getListener() {
        return listener;
    }

    public void setListener(FolderListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folders, container, false);

        panelLayout = (RelativeLayout) rootView.findViewById(R.id.panel_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean dark = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false);
        if (dark) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
        if (mAdapter != null) {
            mAdapter.applyTheme(dark);
            mAdapter.notifyDataSetChanged();
        }

        if (Utils.isMarshmallow()) {
            requestStoragePermission();
        } else {
            loadChannels();
        }
    }

    private void loadChannels() {
        final Playlist lastPlaylist = PreferencesUtility.getInstance(getActivity()).getLastPlaylist();
        if (lastPlaylist != null) {
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
            Router.navigateTo(getActivity(), Router.Screens.PLAYLIST);
        }

    }

    private void showChannelPlaceholder() {

    }

    private void setItemDecoration() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
       /*
        inflater.inflate(R.menu.menu_folders, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search channel name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return filter("");
                } else {
                    return filter(query);
                }
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //TODO here changes the search text)
                if (TextUtils.isEmpty(newText)) {
                    return filter("");
                } else {
                    return filter(newText);
                }
            }
        });
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        switch (item.getItemId()) {
            case R.id.action_storages:
                loadFolders();
                break;
            case R.id.action_server:
                loadServer();
                break;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    private boolean filter(final String newText) {
        if (channelAdapter != null) {
            channelAdapter.getFilter().filter(newText);
            return true;
        } else {
            return false;
        }
    }

    private void loadServer(String url) {
        mProgressBar.setVisibility(View.VISIBLE);
        new LoadServer().execute(url);
    }

    public void updateTheme() {
        Context context = getActivity();
        if (context != null) {
            boolean dark = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
            mAdapter.applyTheme(dark);
        }
    }

    private void requestStoragePermission() {
        if (IseaSoft.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && IseaSoft.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadChannels();
        } else {
            if (IseaSoft.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "iSeaMusic will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                IseaSoft.askForPermission(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                IseaSoft.askForPermission(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    private void parseAndUpdateUI(InputStream inputStream) {

        M3UParser m3UParser = new M3UParser();
        try {
            M3UPlaylist playlist = m3UParser.parseFile(inputStream);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (channelAdapter == null) {
                    channelAdapter = new ChannelAdapter(getActivity());
                }
                channelAdapter.update(playlist.getPlaylistItems());
                recyclerView.setAdapter(channelAdapter);
                //int columnWidthInDp = COLUMN_WIDTH;
                //int spanCount = Utils.getOptimalSpanCount(recyclerView, columnWidthInDp);
                //Utils.modifyRecylerViewForGridView(recyclerView, spanCount, columnWidthInDp);
                Utils.modifyListViewForVertical(getActivity(), recyclerView);
                mProgressBar.setVisibility(View.GONE);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


}
