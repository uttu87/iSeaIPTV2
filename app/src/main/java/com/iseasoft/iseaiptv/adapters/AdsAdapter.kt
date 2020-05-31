package com.iseasoft.iseaiptv.adapters

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.utils.Utils
import com.startapp.sdk.ads.nativead.NativeAdDetails

open class AdsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataSet: MutableList<Any>? = null
        protected set
    protected var isGrid: Boolean = false

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(if (isGrid) R.layout.item_channel_grid_ads else R.layout.item_channel_list_ads, null)
        return NativeExpressAdViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val nativeExpressHolder = viewHolder as NativeExpressAdViewHolder
        nativeExpressHolder.setContent(dataSet!![i])
    }

    override fun getItemCount(): Int {
        return if (null != dataSet) dataSet!!.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        // Logic for returning view type based on spaceBetweenAds variable
        // Here if remainder after dividing the position with (spaceBetweenAds + 1) comes equal to spaceBetweenAds,
        // then return NATIVE_EXPRESS_AD_VIEW_TYPE otherwise DATA_VIEW_TYPE
        // By the logic defined below, an ad unit will be showed after every spaceBetweenAds numbers of data items
        val item = dataSet!![position]
        return if (item is UnifiedNativeAd || item is NativeAdDetails) {
            NATIVE_EXPRESS_AD_VIEW_TYPE
        } else DATA_VIEW_TYPE
    }

    // View Holder for Admob Native Express Ad Unit
    inner class NativeExpressAdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        protected var templateView: UnifiedNativeAdView
        protected var title: TextView
        protected var artist: TextView
        protected var albumArt: ImageView
        protected var ratingBar: RatingBar
        protected var footer: View

        init {
            this.templateView = view.findViewById<View>(R.id.template_ads) as UnifiedNativeAdView
            this.title = view.findViewById<View>(R.id.album_title) as TextView
            this.artist = view.findViewById<View>(R.id.album_artist) as TextView
            this.albumArt = view.findViewById<View>(R.id.album_art) as ImageView
            this.ratingBar = view.findViewById<View>(R.id.rating_bar) as RatingBar
            this.footer = view.findViewById(R.id.footer)
        }

        fun setContent(nativeAds: Any) {
            if (nativeAds is UnifiedNativeAd) {
                templateView.setNativeAd(nativeAds)
                title.text = nativeAds.headline
                artist.text = nativeAds.body
                if (nativeAds.icon != null) {
                    albumArt.setImageDrawable(nativeAds.icon.drawable)
                } else {
                    albumArt.setBackgroundResource(R.mipmap.ic_launcher)
                }
                templateView.callToActionView = templateView
                val starRating = nativeAds.starRating
                if (starRating != null && starRating > 0) {
                    ratingBar.visibility = VISIBLE
                    ratingBar.rating = starRating.toFloat()
                    ratingBar.max = 5
                    templateView.starRatingView = ratingBar
                    artist.visibility = GONE
                } else {
                    ratingBar.visibility = GONE
                }
            } else if (nativeAds is NativeAdDetails) {
                nativeAds.registerViewForInteraction(templateView)
                title.text = nativeAds.title
                artist.text = nativeAds.description
                if (nativeAds.imageBitmap != null) {
                    albumArt.setImageBitmap(nativeAds.imageBitmap)
                } else {
                    albumArt.setBackgroundResource(R.mipmap.ic_launcher)
                }
                //templateView.setCallToActionView(templateView);
                val starRating = nativeAds.rating
                if (starRating > 0) {
                    ratingBar.visibility = VISIBLE
                    ratingBar.rating = starRating
                    ratingBar.max = 5
                    //templateView.setStarRatingView(ratingBar);
                    artist.visibility = GONE
                } else {
                    ratingBar.visibility = GONE
                }
            }

            if (isGrid) {
                try {
                    if (albumArt.drawable == null) {
                        return
                    }
                    val bitmap = (albumArt.drawable as BitmapDrawable).bitmap
                    Palette.Builder(bitmap).generate { palette ->
                        val swatch = palette!!.vibrantSwatch
                        if (swatch != null) {
                            val color = swatch.rgb
                            footer.setBackgroundColor(color)
                            val textColor = Utils.getBlackWhiteColor(swatch.titleTextColor)
                            title.setTextColor(textColor)
                            artist.setTextColor(textColor)
                        } else {
                            val mutedSwatch = palette.mutedSwatch
                            if (mutedSwatch != null) {
                                val color = mutedSwatch.rgb
                                footer.setBackgroundColor(color)
                                val textColor = Utils.getBlackWhiteColor(mutedSwatch.titleTextColor)
                                title.setTextColor(textColor)
                                artist.setTextColor(textColor)
                            }
                        }
                    }
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }

            }
        }
    }

    companion object {
        // Defining variables for view types
        const val DATA_VIEW_TYPE = 100
        const val NATIVE_EXPRESS_AD_VIEW_TYPE = 101
    }
}
