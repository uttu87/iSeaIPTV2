package com.iseasoft.iseaiptv.ui.fragment;

import com.iseasoft.iseaiptv.adapters.AdsAdapter;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import java.util.List;
import java.util.Random;

public class AdsFragment extends BaseFragment {
    protected final static int LIST_VIEW_ADS_COUNT = 10;
    protected final static int GRID_VIEW_ADS_COUNT = 7;
    private final static int ADS_ITEM_START_INDEX = 1;
    protected int spaceBetweenAds;

    protected void generateDataSet(final AdsAdapter adapter) {
        if (getActivity() == null || adapter == null
                || adapter.getDataSet().size() < ADS_ITEM_START_INDEX) {
            return;
        }

        if (isExistAds(adapter)) {
            return;
        }
        int numberOfAds = 3;
        final List<Object> mDataSet = adapter.getDataSet();
        StartAppNativeAd mStartAppNativeAd = new StartAppNativeAd(getActivity());
        mStartAppNativeAd.loadAd(
                new NativeAdPreferences()
                        .setAdsNumber(numberOfAds)
                        .setAutoBitmapDownload(true)
                        .setPrimaryImageSize(2),
                new AdEventListener() {
                    @Override
                    public void onReceiveAd(Ad ad) {
                        if (isExistAds(adapter)) {
                            return;
                        }
                        for (int i = ADS_ITEM_START_INDEX; i <= mDataSet.size(); i += (spaceBetweenAds + 1)) {
                            final int index = new Random().nextInt(mStartAppNativeAd.getNativeAds().size());
                            adapter.getDataSet().add(i, mStartAppNativeAd.getNativeAds().get(index));
                            adapter.notifyItemRangeChanged(i, spaceBetweenAds);
                        }
                    }

                    @Override
                    public void onFailedToReceiveAd(Ad ad) {

                    }
                });

        /*
        AdLoader adLoader = new AdLoader.Builder(getActivity(), getString(R.string.native_ads_id))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        if (isExistAds(adapter)) {
                            return;
                        }
                        final List<Object> mDataSet = adapter.getDataSet();
                        for (int i = ADS_ITEM_START_INDEX; i <= mDataSet.size(); i += (spaceBetweenAds + 1)) {
                            adapter.getDataSet().add(i, unifiedNativeAd);
                            adapter.notifyItemRangeChanged(i, spaceBetweenAds);
                        }
                    }
                })
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5").build());

         */
    }

    private boolean isExistAds(final AdsAdapter adsAdapter) {
        for (Object item : adsAdapter.getDataSet()) {
            if (item instanceof NativeAdDetails) {
                return true;
            }
        }
        return false;
    }
}
