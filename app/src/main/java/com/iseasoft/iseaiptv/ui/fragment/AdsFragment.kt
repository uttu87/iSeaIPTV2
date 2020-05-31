package com.iseasoft.iseaiptv.ui.fragment

import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.iseasoft.iseaiptv.adapters.AdsAdapter
import com.startapp.sdk.ads.nativead.NativeAdDetails

open class AdsFragment : BaseFragment() {
    protected var spaceBetweenAds: Int = 0

//    protected fun generateDataSet(adapter: AdsAdapter?) {
//        if (activity == null || adapter == null
//                || adapter.dataSet!!.size < ADS_ITEM_START_INDEX) {
//            return
//        }
//
//        if (isExistAds(adapter)) {
//            return
//        }
//
//        val mDataSet = adapter.dataSet
//
//        if (App.nativeAdDetails.size > 0) {
//            var i = ADS_ITEM_START_INDEX
//            while (i <= mDataSet!!.size) {
//                val index = Random().nextInt(App.nativeAdDetails.size)
//                adapter.dataSet!!.add(i, App.nativeAdDetails[index])
//                i += spaceBetweenAds + 1
//            }
//            adapter.notifyDataSetChanged()
//            return
//        }
//        val numberOfAds = 3
//        val mStartAppNativeAd = StartAppNativeAd(App.context)
//        mStartAppNativeAd.loadAd(
//                NativeAdPreferences()
//                        .setAdsNumber(numberOfAds)
//                        .setAutoBitmapDownload(true)
//                        .setPrimaryImageSize(2),
//                object : AdEventListener {
//                    override fun onReceiveAd(ad: Ad) {
//                        App.nativeAdDetails = mStartAppNativeAd.nativeAds
//                        if (isExistAds(adapter)) {
//                            return
//                        }
//                        var i = ADS_ITEM_START_INDEX
//                        while (i <= mDataSet!!.size) {
//                            val index = Random().nextInt(mStartAppNativeAd.nativeAds.size)
//                            adapter.dataSet!!.add(i, mStartAppNativeAd.nativeAds[index])
//                            adapter.notifyItemRangeChanged(i, spaceBetweenAds)
//                            i += spaceBetweenAds + 1
//                        }
//                    }
//
//                    override fun onFailedToReceiveAd(ad: Ad) {
//                        generateDataSet(adapter)
//                    }
//                })
//    }

    private fun isExistAds(adsAdapter: AdsAdapter): Boolean {
        for (item in adsAdapter.dataSet!!) {
            if (item is UnifiedNativeAd || item is NativeAdDetails) {
                return true
            }
        }
        return false
    }

    companion object {
        val LIST_VIEW_ADS_COUNT = 10
        val GRID_VIEW_ADS_COUNT = 7
        private val ADS_ITEM_START_INDEX = 1
    }
}
