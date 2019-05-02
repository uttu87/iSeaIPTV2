package com.iseasoft.iseaiptv.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener;
import com.iseasoft.iseaiptv.models.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter {


    private List<Playlist> mItems;
    private Context mContext;
    private OnPlaylistListener mItemListener;

    public PlaylistAdapter(List<Playlist> items, OnPlaylistListener listener) {
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
                    listener.onPlaylistItemClicked(this.playlist);
                }
            } else if (v.getId() == R.id.more) {
                Toast.makeText(mContext, "Clicked on More button", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
