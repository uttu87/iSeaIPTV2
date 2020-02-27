package com.iseasoft.iseaiptv.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.listeners.OnChannelListener;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CanvasAdapter extends RecyclerView.Adapter<CanvasAdapter.ViewHolder> {

    private static final int MAX_VISIBLE_PALETTE_ITEM_COUNT = 10;
    private OnChannelListener itemClickListener;
    private OnCanvasListener onCanvasListener;
    private WeakReference<Context> context;
    private ArrayList<String> data;
    private RecyclerView.RecycledViewPool mSharedPool = new RecyclerView.RecycledViewPool();

    public CanvasAdapter(Context context, ArrayList<String> data) {
        this.context = new WeakReference<>(context);
        this.data = data;
    }

    private OnCanvasListener getOnCanvasListener() {
        return onCanvasListener;
    }

    public void setOnCanvasListener(OnCanvasListener onCanvasListener) {
        this.onCanvasListener = onCanvasListener;
    }

    private OnChannelListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnChannelListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(context.get()).inflate(R.layout.fragment_horizontal_league,
                        parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String catalog = data.get(position);
        ArrayList<M3UItem> list = Utils.getItems(catalog);
        if (list == null || list.size() == 0) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        holder.itemView.setVisibility(View.VISIBLE);
        holder.tvLeagueName.setText(catalog);
        if (list.size() < MAX_VISIBLE_PALETTE_ITEM_COUNT) {
            holder.tvShowMore.setVisibility(View.GONE);
        } else {
            holder.tvShowMore.setVisibility(View.VISIBLE);
            holder.tvShowMore.setOnClickListener(v -> {
                if (getOnCanvasListener() != null) {
                    getOnCanvasListener().onShowMoreClicked(catalog);
                }
            });
        }

        ChannelAdapter dataAdapter = new ChannelAdapter(context.get(), R.layout.item_channel_grid,
                getItemClickListener());
        holder.rvLeague.setAdapter(dataAdapter);
        dataAdapter.update(list);
        Utils.modifyListViewForHorizontal(context.get(), holder.rvLeague);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<String> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    public interface OnCanvasListener {
        void onShowMoreClicked(String league);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLeagueName;
        final TextView tvShowMore;
        final RecyclerView rvLeague;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLeagueName = itemView.findViewById(R.id.tv_league_title);
            tvShowMore = itemView.findViewById(R.id.tv_show_more);
            rvLeague = itemView.findViewById(R.id.list);
            rvLeague.setRecycledViewPool(mSharedPool);
            Utils.modifyListViewForHorizontal(context.get(), rvLeague);
        }
    }
}
