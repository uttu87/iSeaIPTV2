package com.iseasoft.iseaiptv.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.adapters.PlaylistAdapter;
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.iseasoft.iseaiptv.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.list)
    RecyclerView list;

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
        Collections.reverse(playlists);
        playlistAdapter = new PlaylistAdapter(playlists, new OnPlaylistListener() {
            @Override
            public void onPlaylistItemClicked(Playlist item) {

            }
        });
        list.setAdapter(playlistAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
