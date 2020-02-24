package com.iseasoft.iseaiptv.adapters;

/*
  Created by fedor on 28.11.2016.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.OnChannelListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChannelAdapter extends AdsAdapter implements Filterable {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private OnChannelListener listener;
    private int layoutId;

    public ChannelAdapter(Context c, int layoutId, OnChannelListener listener) {
        mContext = c;
        mItems = new ArrayList<>();
        this.listener = listener;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(mContext);
        isGrid = PreferencesUtility.getInstance(c).isGridViewMode();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            return super.onCreateViewHolder(parent, viewType);
        }
        final View sView = mInflater.inflate(layoutId, parent, false);
        return new ItemHolder(sView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            super.onBindViewHolder(viewHolder, position);
            return;
        }
        ItemHolder holder = (ItemHolder) viewHolder;
        final M3UItem item = (M3UItem) mItems.get(position);
        if (item != null) {
            holder.update(item);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void update(ArrayList<M3UItem> _list) {
        this.mItems.clear();
        this.mItems.addAll(_list);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() { //TODO search it on github
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results == null || results.values == null) {
                    return;
                }
                if (results.values instanceof ArrayList) {
                    mItems.clear();
                    mItems.addAll((ArrayList<M3UItem>) results.values);
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<M3UItem> resultList = new ArrayList<>();
                if (!(constraint.length() == 0)) {
                    final String filtePatt = constraint.toString().toLowerCase().trim();
                    for (Object itm : mItems) {
                        if (itm instanceof M3UItem) {
                            M3UItem m3UItem = (M3UItem) itm;
                            if (m3UItem.getItemName().toLowerCase().contains(filtePatt)) {
                                resultList.add(m3UItem);
                            }
                        }
                    }
                }
                results.values = resultList;
                results.count = resultList.size();
                return results;
            }
        };
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        TextView name;
        ImageView cImg;

        ItemHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            name = view.findViewById(R.id.item_name);
            cImg = view.findViewById(R.id.cimg);
        }

        void update(final M3UItem item) {
            try {
                name.setText(item.getItemName());

                if (TextUtils.isEmpty(item.getItemIcon())) {
                    cImg.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get()
                            .load(item.getItemIcon())
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(cImg);
                }

            } catch (Exception ignored) {
            }
        }

        public void onClick(View v) {
            try {
                callbackListener();
            } catch (Exception ignored) {
            }
        }

        private void callbackListener() {
            if (listener != null && getItem() instanceof M3UItem) {
                listener.onChannelClicked((M3UItem) getItem());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Object selectedItem = getItem();
            if (selectedItem instanceof M3UItem) {
                M3UItem m3UItem = (M3UItem) selectedItem;
                PopupMenu popupMenu = new PopupMenu(mContext, name);
                popupMenu.inflate(R.menu.menu_options);
                MenuItem favoriteItem = popupMenu.getMenu().findItem(R.id.action_favorite);
                boolean faved = PreferencesUtility.getInstance(mContext).checkFavorite(m3UItem);
                favoriteItem.setTitle(faved ? R.string.action_remove_favorite : R.string.action_add_favorite);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.action_play:
                            callbackListener();
                            break;
                        case R.id.action_favorite:
                            favorite(m3UItem);
                            break;
                    }
                    return false;
                });
                popupMenu.show();
                return true;
            }
            return false;
        }

        private Object getItem() {
            int position = getLayoutPosition();
            return mItems.get(position);
        }

        private void favorite(M3UItem m3UItem) {
            PreferencesUtility preferencesUtility = PreferencesUtility.getInstance(mContext);
            preferencesUtility.favorite(m3UItem);
        }
    }
}
