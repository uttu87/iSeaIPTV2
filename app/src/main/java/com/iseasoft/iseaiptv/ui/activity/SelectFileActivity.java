package com.iseasoft.iseaiptv.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.FolderListener;
import com.iseasoft.iseaiptv.models.M3UPlaylist;
import com.iseasoft.iseaiptv.parsers.M3UParser;
import com.iseasoft.iseaiptv.ui.fragment.FoldersFragment;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SelectFileActivity extends AppCompatActivity implements FolderListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateTitle(PreferencesUtility.getInstance(this).getLastFolder());

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupSelectFileView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void updateTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void setupSelectFileView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FoldersFragment foldersFragment = FoldersFragment.newInstance();
        foldersFragment.setListener(this);
        ft.replace(R.id.select_file_container, foldersFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onFileSelected(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            parseAndUpdateUI(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirChanged(File dir) {
        updateTitle(dir.getPath());
    }

    private void parseAndUpdateUI(InputStream inputStream) {

        M3UParser m3UParser = new M3UParser();
        try {
            M3UPlaylist playlist = m3UParser.parseFile(inputStream);
            new Handler(Looper.getMainLooper()).post(() -> {
//                if (playlistAdapter == null) {
//                    playlistAdapter = new PlaylistAdapter(getActivity());
//                }
//                playlistAdapter.update(playlist.getPlaylistItems());
//                recyclerView.setAdapter(playlistAdapter);
//                int columnWidthInDp = COLUMN_WIDTH;
//                int spanCount = Utils.getOptimalSpanCount(recyclerView, columnWidthInDp);
//                Utils.modifyRecylerViewForGridView(recyclerView, spanCount, columnWidthInDp);
//                mProgressBar.setVisibility(View.GONE);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
