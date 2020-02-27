package com.iseasoft.iseaiptv.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.CanvasAdapter;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.listeners.OnChannelListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.ui.activity.ChannelActivity;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.iseasoft.iseaiptv.ui.activity.PlayerActivity.CHANNEL_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private static final int COVER_ADS_RANGE = 3;

    Unbinder unbinder;

    @BindView(R.id.list_league)
    RecyclerView rvLeagueList;
    private boolean init = false;
    private CanvasAdapter mCanvasAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utils.modifyListViewForVertical(getContext(), rvLeagueList);
        setupLeagueAdapter();

    }

    private void setupLeagueAdapter() {
        ArrayList<String> mLeagues = new ArrayList<>();
        if (!PreferencesUtility.getInstance(App.getContext()).hasNoFavorite()) {
            mLeagues.add(getString(R.string.favorites));
        }
        if (!PreferencesUtility.getInstance(App.getContext()).hasNoHistoryWatching()) {
            mLeagues.add(getString(R.string.history_watching));
        }
        mLeagues.add(getString(R.string.all_channels));
        mLeagues.add("ads");

        LinkedList<String> groupList = new LinkedList<>();
        ArrayList<M3UItem> channelList = App.getChannelList();
        if (channelList != null) {
            for (int i = 0; i < channelList.size(); i++) {
                M3UItem m3UItem = channelList.get(i);
                if (groupList.contains(m3UItem.getItemGroup())) {
                    continue;
                }
                groupList.add(m3UItem.getItemGroup());
            }
            for (int i = 0; i < groupList.size(); i++) {
                String groupTitle = groupList.get(i);
                if (!TextUtils.isEmpty(groupTitle)) {
                    mLeagues.add(groupTitle);
                    if (i == COVER_ADS_RANGE) {
                        mLeagues.add("ads");
                    }
                }
            }
        }

        mCanvasAdapter = new CanvasAdapter(getContext(), mLeagues);
        mCanvasAdapter.setOnCanvasListener(league -> {
            //TODO show league match
            Bundle bundle = new Bundle();
            bundle.putString(ChannelActivity.CATALOG_KEY, league);
            Router.navigateTo(getActivity(), Router.Screens.CHANNEL, bundle, false);
        });
        mCanvasAdapter.setItemClickListener(new OnChannelListener() {
            @Override
            public void onChannelClicked(M3UItem item) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(CHANNEL_KEY, item);
                //bundle.putSerializable(PLAYLIST_KEY, getPlaylistItems());
                Router.navigateTo(getActivity(), Router.Screens.PLAYER, bundle, false);
            }
        });
        rvLeagueList.setAdapter(mCanvasAdapter);
    }


}
