package com.iseasoft.iseaiptv.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.models.M3UItem
import java.util.*

object Utils {

    val isOreo: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP


    val isJellyBeanMR2: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2

    val isJellyBean: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

    val isJellyBeanMR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun isTablet(context: Context?): Boolean {
        return if (context == null) false else context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun convertDp2Px(context: Context, dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }

    fun changeToDisplayTime(msec: Long): String {
        var result: String? = null
        val time = msec / 1000

        val hour = time / 3600
        val tmp = time % 3600

        val min = tmp / 60
        val sec = tmp % 60

        if (hour > 0) {
            result = String.format("%02d:%02d:%02d", hour, min, sec)
        } else {
            result = String.format("%01d:%02d", min, sec)
        }
        return result
    }

    //    public static void setupToolbar(AppCompatActivity activity, String title) {
    //        TextView toolbarTitle = activity.findViewById(R.id.toolbar_title);
    //        toolbarTitle.setText(title);
    //        Toolbar toolbar = activity.findViewById(R.id.toolbar);
    //        activity.setSupportActionBar(toolbar);
    //        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
    //        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    //        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
    //        activity.setTitle("");
    //    }

    fun modifyListViewForHorizontal(context: Context, recyclerView: RecyclerView) {
        modifyListView(context, recyclerView, LinearLayoutManager.HORIZONTAL)
    }

    fun modifyListViewForVertical(context: Context, recyclerView: RecyclerView) {
        modifyListView(context, recyclerView, LinearLayoutManager.VERTICAL)
    }

    private fun modifyListView(context: Context, recyclerView: RecyclerView, orientation: Int) {
        val llm = LinearLayoutManager(context)
        llm.orientation = orientation
        recyclerView.layoutManager = llm
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    fun modifyRecylerViewForGridView(recyclerView: RecyclerView,
                                     spanCount: Int,
                                     columnWidthInDp: Int) {
        var spanCount = spanCount
        if (spanCount == 0) {
            spanCount = getOptimalSpanCount(recyclerView, columnWidthInDp)
        }
        val layoutManager = GridLayoutManager(recyclerView.context, spanCount)
        recyclerView.layoutManager = layoutManager
        //recyclerView.setNestedScrollingEnabled(false);
    }

    fun getOptimalSpanCount(recyclerView: RecyclerView, columnWidthInDp: Int): Int {
        var spanCount = getOptimalSpanCount(recyclerView.width,
                Utils.convertDp2Px(recyclerView.context, columnWidthInDp.toFloat()).toInt())
        if (spanCount == 0) {
            spanCount = getOptimalSpanCount(screenWidth,
                    Utils.convertDp2Px(recyclerView.context, columnWidthInDp.toFloat()).toInt())
        }
        return spanCount
    }

    fun getOptimalSpanCount(recyclerViewWidth: Int, columnWidthInPx: Int): Int {
        return Math.floor((recyclerViewWidth / columnWidthInPx).toDouble()).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(
                    context.packageName, PackageManager.GET_ACTIVITIES)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return ""
        }

    }

    fun getSpecialUserAgent(context: Context): String {
        return String.format("%s %s/%s",
                WebView(context).settings.userAgentString,
                context.packageName,
                getVersionName(context))
    }

    @SuppressLint("DefaultLocale")
    fun getVersionString(context: Context): String {
        var versionName = ""
        var versionCode = 0
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = pInfo.versionName
            versionCode = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {

        }

        return String.format("%s(%d)", versionName, versionCode)

    }

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork: NetworkInfo? = null
        if (manager != null) {
            activeNetwork = manager.activeNetworkInfo
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else
            Color.BLACK
    }

    fun getItems(catalog: String): ArrayList<M3UItem>? {
        if (TextUtils.isEmpty(catalog)) {
            return ArrayList()
        }

        if (catalog == "Favorites") {
            return PreferencesUtility.getInstance(App.context).favoriteChannels
        }

        if (catalog == "History Watching") {
            return PreferencesUtility.getInstance(App.context).historyChannels
        }

        if (App.channelList == null) {
            return ArrayList()
        }

        val allChannels = App.channelList
        if (allChannels.size == 0) {
            return ArrayList()
        }

        if (catalog == "All channels") {
            return allChannels
        }
        val list = ArrayList<M3UItem>()
        for (i in allChannels.indices) {
            val item = allChannels[i]
            if (catalog == item.itemGroup) {
                list.add(item)
            }
        }
        return list
    }
}
