package com.iseasoft.iseaiptv.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.afollestad.appthemeengine.ATE;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.adapters.FolderAdapter;
import com.iseasoft.iseaiptv.listeners.FolderListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.ui.activity.MainActivity;
import com.iseasoft.iseaiptv.utils.Utils;
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class ChannelFragment extends Fragment {

    private static final int COLUMN_WIDTH = 70;
    private RelativeLayout panelLayout;
    private FolderAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private ChannelAdapter channelAdapter;

    private FolderListener listener;
    private String groupName;

    public static ChannelFragment newInstance(String groupName) {
        ChannelFragment fragment = new ChannelFragment();
        fragment.groupName = groupName;
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
        showChannels();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void showChannels() {
        if (channelAdapter == null) {
            channelAdapter = new ChannelAdapter(getActivity());
        }
        channelAdapter.update(getPlaylistItems());
        recyclerView.setAdapter(channelAdapter);
        //int columnWidthInDp = COLUMN_WIDTH;
        //int spanCount = Utils.getOptimalSpanCount(recyclerView, columnWidthInDp);
        //Utils.modifyRecylerViewForGridView(recyclerView, spanCount, columnWidthInDp);
        Utils.modifyListViewForVertical(getActivity(), recyclerView);
        mProgressBar.setVisibility(View.GONE);
    }

    private List<M3UItem> getPlaylistItems() {
        MainActivity mainActivity = (MainActivity) getActivity();
        List<M3UItem> allChannels = mainActivity.getPlaylist().getPlaylistItems();
        if (groupName.equals(getString(R.string.all_channels))) {
            return allChannels;
        } else if (groupName.equals(getString(R.string.favorites))) {
            //return PreferencesUtility.getInstance(getActivity()).getFavoriteChannels();
        }
        ArrayList<M3UItem> list = new ArrayList<>();
        for (int i = 0; i < allChannels.size(); i++) {
            M3UItem item = allChannels.get(i);
            if (groupName.equals(item.getItemGroup())) {
                list.add(item);
            }
        }
        return list;
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

        inflater.inflate(R.menu.main, menu);
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

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setKeyboardVisibility(false);
                showChannels();
                return true;
            }
        });

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

    public void updateTheme() {
        Context context = getActivity();
        if (context != null) {
            boolean dark = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
            mAdapter.applyTheme(dark);
        }
    }

    public void setKeyboardVisibility(boolean show) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
