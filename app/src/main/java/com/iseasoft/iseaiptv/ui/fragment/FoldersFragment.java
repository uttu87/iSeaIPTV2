package com.iseasoft.iseaiptv.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.appthemeengine.ATE;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.FolderAdapter;
import com.iseasoft.iseaiptv.dialogs.StorageSelectDialog;
import com.iseasoft.iseaiptv.listeners.FolderListener;
import com.iseasoft.iseaiptv.permissions.IseaSoft;
import com.iseasoft.iseaiptv.permissions.PermissionCallback;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration;

import java.io.File;

/**
 * Created by nv95 on 10.11.16.
 */

public class FoldersFragment extends Fragment implements StorageSelectDialog.OnDirSelectListener {

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadFolders();
        }

        @Override
        public void permissionRefused() {
            requestStoragePermission();
        }
    };
    private RelativeLayout panelLayout;
    private FolderAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;

    private FolderListener listener;

    public static FoldersFragment newInstance() {
        FoldersFragment fragment = new FoldersFragment();
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

    private void loadFolders() {
        if (getActivity() != null) {
            new LoadFolder().execute("");
        }
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
            loadFolders();
        }
    }

    private void setItemDecoration() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void updateTheme() {
        Context context = getActivity();
        if (context != null) {
            boolean dark = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
            mAdapter.applyTheme(dark);
        }
    }

    @Override
    public void onDirSelected(File dir) {
        mAdapter.updateDataSetAsync(dir);
    }

    private void requestStoragePermission() {
        if (IseaSoft.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && IseaSoft.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadFolders();
        } else {
            if (IseaSoft.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, R.string.request_storage_permission_message,
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

    @SuppressLint("StaticFieldLeak")
    private class LoadFolder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity != null) {
                mAdapter = new FolderAdapter(activity, new File(PreferencesUtility.getInstance(activity).getLastFolder()));
                mAdapter.setFolderListener(getListener());
                updateTheme();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
        }
    }
}
