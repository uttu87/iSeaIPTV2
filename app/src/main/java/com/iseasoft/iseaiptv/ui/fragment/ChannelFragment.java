package com.iseasoft.iseaiptv.ui.fragment;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.listeners.FolderListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.ui.activity.MainActivity;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class ChannelFragment extends Fragment {

    private static final int COLUMN_WIDTH = 70;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private LinearLayout placeholderContainer;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folders, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        placeholderContainer = (LinearLayout) rootView.findViewById(R.id.placeholder_container);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showChannels();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void showChannels() {
        if (groupName.equals(getString(R.string.favorites))) {
            if (getPlaylistItems() == null || getPlaylistItems().size() == 0) {
                showFavoritePlaceholder();
                return;
            }
        }
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
        placeholderContainer.setVisibility(View.GONE);
    }

    private void showFavoritePlaceholder() {
        recyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        placeholderContainer.setVisibility(View.VISIBLE);

    }

    private List<M3UItem> getPlaylistItems() {
        MainActivity mainActivity = (MainActivity) getActivity();
        List<M3UItem> allChannels = mainActivity.getPlaylist().getPlaylistItems();
        if (groupName.equals(getString(R.string.all_channels))) {
            return allChannels;
        } else if (groupName.equals(getString(R.string.favorites))) {
            return PreferencesUtility.getInstance(getActivity()).getFavoriteChannels();
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                channelAdapter.update(getPlaylistItems());
                if (!TextUtils.isEmpty(newText)) {
                    return filter(newText);
                }
                return false;
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

    public void setKeyboardVisibility(boolean show) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
