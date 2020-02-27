package com.iseasoft.iseaiptv.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.ChannelAdapter;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.iseasoft.iseaiptv.ui.activity.PlayerActivity.CHANNEL_KEY;

/**
 * Created by nv95 on 10.11.16.
 */

public class ChannelFragment extends AdsFragment {

    private static final int COLUMN_WIDTH = 160;

    Unbinder unbinder;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.favorite_placeholder_container)
    LinearLayout favoritePlaceholderContainer;
    @BindView(R.id.placeholder_container)
    ConstraintLayout placeholderContainer;
    MenuItem switchListView;

    private ChannelAdapter channelAdapter;

    private String groupName;
    private SearchView searchView;

    public static ChannelFragment newInstance(String groupName) {
        ChannelFragment fragment = new ChannelFragment();
        fragment.groupName = groupName;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folders, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spaceBetweenAds = isGridView() ? GRID_VIEW_ADS_COUNT : LIST_VIEW_ADS_COUNT;
        showChannels();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isStateSafe()) {
            if (!TextUtils.isEmpty(groupName) &&
                    (groupName.equals(getString(R.string.favorites)) || groupName.equals(getString(R.string.history_watching)))) {
                showChannels();
            }
        }
    }

    private void showChannels() {
        if (TextUtils.isEmpty(groupName)) {
            return;
        }

        if (getPlaylistItems() == null || getPlaylistItems().size() == 0) {
            if (groupName.equals(getString(R.string.favorites))) {
                showFavoritePlaceholder();
            } else {
                showPlaceholder();
            }
            return;
        }
        if (channelAdapter == null) {
            channelAdapter = new ChannelAdapter(getActivity(),
                    isGridView() ? R.layout.item_channel_grid : R.layout.item_channel_list
                    , item -> {
                if (getActivity() == null) {
                    return;
                }
                if (searchView != null) {
                    searchView.clearFocus();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(CHANNEL_KEY, item);
                //bundle.putSerializable(PLAYLIST_KEY, getPlaylistItems());
                Router.navigateTo(getActivity(), Router.Screens.PLAYER, bundle, false);
            });
        }
        hideAllView();
        recyclerView.setVisibility(View.VISIBLE);
        spaceBetweenAds = isGridView() ? GRID_VIEW_ADS_COUNT : LIST_VIEW_ADS_COUNT;
        channelAdapter.update(getPlaylistItems());
        generateDataSet(channelAdapter);
        recyclerView.setAdapter(channelAdapter);
        if (isGridView()) {
            setupGridView();
        } else {
            Utils.modifyListViewForVertical(getActivity(), recyclerView);
        }
    }

    private void showPlaceholder() {
        hideAllView();
        placeholderContainer.setVisibility(View.VISIBLE);
    }

    private void hideAllView() {
        recyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        favoritePlaceholderContainer.setVisibility(View.GONE);
        placeholderContainer.setVisibility(View.GONE);
    }

    private void setupGridView() {
        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (recyclerView == null) {
                    return;
                }
                ViewTreeObserver o = recyclerView.getViewTreeObserver();
                o.removeOnGlobalLayoutListener(this);
                int columnWidthInDp = COLUMN_WIDTH;
                int spanCount = Utils.getOptimalSpanCount(recyclerView, columnWidthInDp);
                Utils.modifyRecylerViewForGridView(recyclerView, spanCount, columnWidthInDp);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateGridView();
    }

    private void updateGridView() {
        if (isGridView()) {
            setupGridView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showChannels();
    }

    private void showFavoritePlaceholder() {
        hideAllView();
        favoritePlaceholderContainer.setVisibility(View.VISIBLE);

    }

    private ArrayList<M3UItem> getPlaylistItems() {
        if (getActivity() == null || TextUtils.isEmpty(groupName)) {
            return new ArrayList<>();
        }

        if (groupName.equals(getString(R.string.favorites))) {
            return PreferencesUtility.getInstance(getActivity()).getFavoriteChannels();
        }

        if (groupName.equals(getString(R.string.history_watching))) {
            return PreferencesUtility.getInstance(getActivity()).getHistoryChannels();
        }

        ArrayList<M3UItem> allChannels = App.getChannelList();
        if (allChannels == null || allChannels.size() == 0) {
            return new ArrayList<>();
        }

        if (groupName.equals(getString(R.string.all_channels))) {
            return allChannels;
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
        switchListView = menu.findItem(R.id.action_switch_view);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search channel name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (channelAdapter != null) {
                    channelAdapter.update(getPlaylistItems());
                    generateDataSet(channelAdapter);
                }
                if (!TextUtils.isEmpty(newText)) {
                    return filter(newText);
                }
                return false;
            }
        });

        if (isGridView()) {
            MenuItem grid = menu.findItem(R.id.grid);
            grid.setChecked(true);
            switchListView.setIcon(R.drawable.ic_grid);
        } else {
            MenuItem list = menu.findItem(R.id.list);
            list.setChecked(true);
            switchListView.setIcon(R.drawable.ic_list);
        }
    }

    private boolean isGridView() {
        return PreferencesUtility.getInstance(getActivity()).isGridViewMode();
    }

    private void setGridView(boolean value) {
        PreferencesUtility.getInstance(getActivity()).setGridViewMode(value);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                break;
            case R.id.list:
                switchListView.setIcon(R.drawable.ic_list);
                item.setChecked(true);
                setGridView(false);
                channelAdapter = null;
                showChannels();
                break;
            case R.id.grid:
                switchListView.setIcon(R.drawable.ic_grid);
                item.setChecked(true);
                setGridView(true);
                channelAdapter = null;
                showChannels();
                break;
        }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        channelAdapter = null;
        unbinder.unbind();

    }

    @OnClick(R.id.btn_add_playlist)
    public void onClick() {
        if (getActivity() == null) {
            return;
        }
        Router.navigateTo(getActivity(), Router.Screens.PLAYLIST, false);
    }
}
