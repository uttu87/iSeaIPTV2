package com.iseasoft.iseaiptv.adapters;

/*
  Created by fedor on 28.11.2016.
 */


import android.content.Context;
import android.content.pm.PackageManager;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.OnChannelListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.utils.PreferencesUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ItemHolder> implements Filterable {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<M3UItem> mItem = new ArrayList<>();
    private TextDrawable textDrawable;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private OnChannelListener listener;
    private int layoutId;

    public ChannelAdapter(Context c, int layoutId, OnChannelListener listener) {
        mContext = c;
        this.listener = listener;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View sView = mInflater.inflate(layoutId, parent, false);
        return new ItemHolder(sView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final M3UItem item = mItem.get(position);
        if (item != null) {
            holder.update(item);
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public void update(ArrayList<M3UItem> _list) {
        this.mItem.clear();
        this.mItem.addAll(_list);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() { //TODO search it on github
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItem.clear();
                mItem.addAll((ArrayList<M3UItem>) results.values);
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<M3UItem> resultList = new ArrayList<>();
                if (!(constraint.length() == 0)) {
                    final String filtePatt = constraint.toString().toLowerCase().trim();
                    for (M3UItem itm : mItem) {
                        if (itm.getItemName().toLowerCase().contains(filtePatt)) {
                            resultList.add(itm);
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

        final PackageManager pm = mContext.getPackageManager();
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
                int color = generator.getRandomColor();
                textDrawable = TextDrawable.builder()
                        .buildRoundRect(String.valueOf(item.getItemName().charAt(0)), color, 100);

                if (TextUtils.isEmpty(item.getItemIcon())) {
                    cImg.setImageDrawable(textDrawable);
                } else {
                    Picasso.get()
                            .load(item.getItemIcon())
                            .placeholder(textDrawable)
                            .error(textDrawable)
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
            if (listener != null) {
                listener.onChannelClicked(getItem());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, name);
            popupMenu.inflate(R.menu.menu_options);
            MenuItem favoriteItem = popupMenu.getMenu().findItem(R.id.action_favorite);
            boolean faved = PreferencesUtility.getInstance(mContext).checkFavorite(getItem());
            favoriteItem.setTitle(faved ? R.string.action_remove_favorite : R.string.action_add_favorite);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_play:
                            callbackListener();
                            break;
                        case R.id.action_favorite:
                            favorite();
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
            return true;
        }

        private M3UItem getItem() {
            int position = getLayoutPosition();
            return mItem.get(position);
        }

        private void favorite() {
            PreferencesUtility preferencesUtility = PreferencesUtility.getInstance(mContext);
            preferencesUtility.favorite(getItem());
        }
    }
}
