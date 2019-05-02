package com.iseasoft.iseaiptv.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.PlaylistAdapter;
import com.iseasoft.iseaiptv.helpers.Router;
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.empty_container)
    LinearLayout emptyContainer;

    private PlaylistAdapter playlistAdapter;

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utils.modifyListViewForVertical(getContext(), list);
        loadPlaylist();
    }

    private void loadPlaylist() {
        ArrayList<Playlist> playlists = PreferencesUtility.getInstance(getActivity()).getPlaylist();
        if (playlists == null || playlists.size() == 0) {
            emptyContainer.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyContainer.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            Collections.reverse(playlists);
            playlistAdapter = new PlaylistAdapter(playlists, new OnPlaylistListener() {
                @Override
                public void onPlaylistItemClicked(Playlist item) {
                    PreferencesUtility.getInstance(getActivity()).savePlaylist(item);
                    Router.navigateToMainScreen(getActivity(), true);
                }
            });
            list.setAdapter(playlistAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.add_playlist)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_playlist:
                getActivity().openOptionsMenu();
                break;
        }
    }


}
