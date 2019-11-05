package com.iseasoft.iseaiptv.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.utils.Utils;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AdsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Defining variables for view types
    protected static final int DATA_VIEW_TYPE = 100;
    protected static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 101;

    protected List<Object> mItems;
    protected boolean isGrid;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(isGrid ? R.layout.item_channel_grid_ads : R.layout.item_channel_list_ads, null);
        return new NativeExpressAdViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final NativeExpressAdViewHolder nativeExpressHolder = (NativeExpressAdViewHolder) viewHolder;
        NativeAdDetails nativeAdDetails = (NativeAdDetails) mItems.get(i);
        nativeExpressHolder.setContent(nativeAdDetails);
    }

    @Override
    public int getItemCount() {
        return (null != mItems ? mItems.size() : 0);
    }

    public List<Object> getDataSet() {
        return mItems;
    }

    @Override
    public int getItemViewType(int position) {
        // Logic for returning view type based on spaceBetweenAds variable
        // Here if remainder after dividing the position with (spaceBetweenAds + 1) comes equal to spaceBetweenAds,
        // then return NATIVE_EXPRESS_AD_VIEW_TYPE otherwise DATA_VIEW_TYPE
        // By the logic defined below, an ad unit will be showed after every spaceBetweenAds numbers of data items
        Object item = mItems.get(position);
        if (item instanceof NativeAdDetails) {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        }
        return DATA_VIEW_TYPE;
    }

    // View Holder for Admob Native Express Ad Unit
    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {
        protected View templateView;
        protected TextView title, artist;
        protected ImageView albumArt;
        protected RatingBar ratingBar;
        protected View footer;

        NativeExpressAdViewHolder(View view) {
            super(view);
            this.templateView =  view.findViewById(R.id.template_ads);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.artist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            this.footer = view.findViewById(R.id.footer);
        }

        public void setContent(NativeAdDetails nativeAdDetails) {
            //templateView.setNativeAd(unifiedNativeAd);
            nativeAdDetails.registerViewForInteraction(templateView);
            title.setText(nativeAdDetails.getTitle());
            artist.setText(nativeAdDetails.getDescription());
            if (nativeAdDetails.getImageBitmap() != null) {
                albumArt.setImageBitmap(nativeAdDetails.getImageBitmap());
            } else {
                albumArt.setBackgroundResource(R.mipmap.ic_launcher);
            }
            //templateView.setCallToActionView(templateView);
            float starRating = nativeAdDetails.getRating();
            if (starRating > 0) {
                ratingBar.setVisibility(VISIBLE);
                ratingBar.setRating(starRating);
                ratingBar.setMax(5);
                //templateView.setStarRatingView(ratingBar);
                artist.setVisibility(GONE);
            } else {
                ratingBar.setVisibility(GONE);
            }

            if (isGrid) {
                try {
                    if (albumArt.getDrawable() == null) {
                        return;
                    }
                    Bitmap bitmap = ((BitmapDrawable) albumArt.getDrawable()).getBitmap();
                    new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                int color = swatch.getRgb();
                                footer.setBackgroundColor(color);
                                int textColor = Utils.getBlackWhiteColor(swatch.getTitleTextColor());
                                title.setTextColor(textColor);
                                artist.setTextColor(textColor);
                            } else {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                if (mutedSwatch != null) {
                                    int color = mutedSwatch.getRgb();
                                    footer.setBackgroundColor(color);
                                    int textColor = Utils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                    title.setTextColor(textColor);
                                    artist.setTextColor(textColor);
                                }
                            }
                        }
                    });
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
