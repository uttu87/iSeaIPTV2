package com.iseasoft.iseaiptv.ui.activity;

import android.app.ActivityManager;
import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.iseasoft.iseaiptv.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    Unbinder unbinder;
    @BindView(R.id.publisherAdView)
    PublisherAdView publisherAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        setupPublisherAds();
    }

    private void setupPublisherAds() {
        setupPublisherBannerAds();
    }


    private void setupPublisherBannerAds() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("FB536EF8C6F97686372A2C5A5AA24BC5")
                .build();
        publisherAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (publisherAdView != null) {
            publisherAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (publisherAdView != null) {
            publisherAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (publisherAdView != null) {
            publisherAdView.destroy();
        }
        unbinder.unbind();
        unbinder = null;
    }


    protected boolean isStateSafe() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }

    /*

    public void shareApp(Channel channel) {
        String[] blacklist = new String[]{"com.any.package", "net.other.package"};
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = getString(R.string.share_boday);
        if (channel != null) {
            shareBody = "Watch " + channel.getName();
        }
        shareBody = shareBody + " at: " + GOOGLE_PLAY_APP_LINK;

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        String title = getString(channel != null ? R.string.share_video_title : R.string.share_app_title);

        startActivity(Intent.createChooser(sharingIntent, title));
    }

    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;

        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;

                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.share_app_title));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, getString(R.string.share_app_title));
    }

    protected void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    */

    private boolean isLastBackStack() {
        return isLastActivityStack() && isLastFragmentStack();
    }

    private boolean isLastActivityStack() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> latestTask = am.getRunningTasks(1);
        if (latestTask == null && latestTask.size() != 1) {
            return false;
        }
        ActivityManager.RunningTaskInfo task = latestTask.get(0);
        return task.numActivities == 1
                && task.topActivity.getClassName().equals(this.getClass().getName());
    }

    private boolean isLastFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        return fm.getBackStackEntryCount() == 0;
    }

}
