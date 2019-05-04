package com.iseasoft.iseaiptv.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener;
import com.iseasoft.iseaiptv.models.Playlist;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.Collections;

public class PlaylistAdapter extends RecyclerView.Adapter {


    private ArrayList<Playlist> mItems;
    private Context mContext;
    private OnPlaylistListener mItemListener;

    public PlaylistAdapter(ArrayList<Playlist> items, OnPlaylistListener listener) {
        this.mItems = items;
        this.mItemListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, null);

        PlaylistHolder playlistHolder = new PlaylistHolder(v);
        return playlistHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final PlaylistHolder playlistHolder = (PlaylistHolder) holder;
        final Playlist playlist = mItems.get(position);
        playlistHolder.setContent(playlist);
        playlistHolder.setListener(mItemListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ConstraintLayout mainView;
        ImageView icon;
        TextView name;
        TextView link;
        ImageView more;

        private Playlist playlist;
        private OnPlaylistListener listener;

        PlaylistHolder(View view) {
            super(view);
            mainView = view.findViewById(R.id.mainView);
            mainView.setOnClickListener(this);
            icon = view.findViewById(R.id.icon);
            name = view.findViewById(R.id.name);
            link = view.findViewById(R.id.link);
            more = view.findViewById(R.id.more);
            more.setOnClickListener(this);
        }

        public OnPlaylistListener getListener() {
            return listener;
        }

        public void setListener(OnPlaylistListener listener) {
            this.listener = listener;
        }

        public void setContent(Playlist playlist) {
            this.playlist = playlist;
            if (playlist.getLink().startsWith("http")) {
                icon.setImageResource(R.drawable.ic_link_black_24dp);
            } else {
                icon.setImageResource(R.drawable.ic_file_black_24dp);
            }
            name.setText(playlist.getName());
            link.setText(playlist.getLink());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mainView) {
                if (listener != null) {
                    listener.onPlaylistItemClicked(playlist);
                }
            } else if (v.getId() == R.id.more) {
                PopupMenu popupMenu = new PopupMenu(mContext, more);
                popupMenu.inflate(R.menu.menu_playlist_options);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_open:
                                if (listener != null) {
                                    listener.onPlaylistItemClicked(playlist);
                                }
                                break;
                            case R.id.action_delete:
                                delete();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        }

        private void delete() {
            int pos = getLayoutPosition();
            notifyItemRemoved(pos);
            mItems.remove(playlist);
            Collections.reverse(mItems);
            PreferencesUtility.getInstance(mContext).savePlaylist(mItems);
            Collections.reverse(mItems);
        }
    }
}
