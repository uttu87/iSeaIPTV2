package com.iseasoft.iseaiptv.ui.fragment;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
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
                        generateDataSet(adapter);
                    }
                });

    }

    private boolean isExistAds(final AdsAdapter adsAdapter) {
        for (Object item : adsAdapter.getDataSet()) {
            if (item instanceof UnifiedNativeAd || item instanceof NativeAdDetails) {
                return true;
            }
        }
        return false;
    }
}
