package com.iseasoft.iseaiptv.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.adapters.ChannelAdapter
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.OnChannelListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity.Companion.CHANNEL_KEY
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration
import java.util.*

/**
 * Created by nv95 on 10.11.16.
 */

class ChannelFragment : AdsFragment() {

    internal lateinit var unbinder: Unbinder

    @BindView(R.id.recyclerview)
    @JvmField
    var recyclerView: RecyclerView? = null

    @BindView(R.id.progressBar)
    @JvmField
    var mProgressBar: ProgressBar? = null

    @BindView(R.id.favorite_placeholder_container)
    @JvmField
    var favoritePlaceholderContainer: LinearLayout? = null

    @BindView(R.id.placeholder_container)
    @JvmField
    var placeholderContainer: ConstraintLayout? = null
    internal lateinit var switchListView: MenuItem

    private var channelAdapter: ChannelAdapter? = null

    private var groupName: String? = null
    private var searchView: SearchView? = null

    private val playlistItems: ArrayList<M3UItem>?
        get() {
            if (activity == null || TextUtils.isEmpty(groupName)) {
                return ArrayList()
            }

            if (groupName == getString(R.string.favorites)) {
                return PreferencesUtility.getInstance(activity).favoriteChannels
            }

            if (groupName == getString(R.string.history_watching)) {
                return PreferencesUtility.getInstance(activity).historyChannels
            }

            val allChannels = App.channelList
            if (allChannels == null || allChannels.size == 0) {
                return ArrayList()
            }

            if (groupName == getString(R.string.all_channels)) {
                return allChannels
            }
            val list = ArrayList<M3UItem>()
            for (i in allChannels.indices) {
                val item = allChannels[i]
                if (groupName == item.itemGroup) {
                    list.add(item)
                }
            }
            return list
        }

    private var isGridView: Boolean
        get() = PreferencesUtility.getInstance(activity).isGridViewMode
        set(value) {
            PreferencesUtility.getInstance(activity).isGridViewMode = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
                R.layout.fragment_folders, container, false)
        unbinder = ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spaceBetweenAds = if (isGridView) AdsFragment.GRID_VIEW_ADS_COUNT else AdsFragment.LIST_VIEW_ADS_COUNT
        showChannels()
    }

    private fun showChannels() {
        if (TextUtils.isEmpty(groupName) || activity == null) {
            return
        }

        if (playlistItems == null || playlistItems!!.size == 0) {
            if (groupName == getString(R.string.favorites)) {
                showFavoritePlaceholder()
            } else {
                showPlaceholder()
            }
            return
        }
        if (channelAdapter == null) {
            channelAdapter = ChannelAdapter(activity!!, R.layout.item_channel_list, object : OnChannelListener {
                override fun onChannelClicked(item: M3UItem) {
                    if (activity != null) {
                        if (searchView != null) {
                            searchView!!.clearFocus()
                        }
                        val bundle = Bundle()
                        bundle.putSerializable(CHANNEL_KEY, item)
                        //bundle.putSerializable(PLAYLIST_KEY, getPlaylistItems());
                        Router.navigateTo(activity!!, Router.Screens.PLAYER, bundle, false)
                    }
                }
            })
        }
        hideAllView()
        recyclerView?.visibility = View.VISIBLE
        spaceBetweenAds = AdsFragment.LIST_VIEW_ADS_COUNT
        channelAdapter?.update(playlistItems!!)
        //generateDataSet(channelAdapter);
        recyclerView?.adapter = channelAdapter
        recyclerView?.let { Utils.modifyListViewForVertical(activity!!, it) }
    }

    private fun showPlaceholder() {
        hideAllView()
        placeholderContainer!!.visibility = View.VISIBLE
    }

    private fun hideAllView() {
        recyclerView!!.visibility = View.GONE
        mProgressBar!!.visibility = View.GONE
        favoritePlaceholderContainer!!.visibility = View.GONE
        placeholderContainer!!.visibility = View.GONE
    }

    private fun setupGridView() {
        val observer = recyclerView!!.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (recyclerView == null) {
                    return
                }
                val o = recyclerView!!.viewTreeObserver
                o.removeOnGlobalLayoutListener(this)
                val columnWidthInDp = COLUMN_WIDTH
                val spanCount = Utils.getOptimalSpanCount(recyclerView!!, columnWidthInDp)
                Utils.modifyRecylerViewForGridView(recyclerView!!, spanCount, columnWidthInDp)
            }
        })
    }

    private fun showFavoritePlaceholder() {
        hideAllView()
        favoritePlaceholderContainer!!.visibility = View.VISIBLE

    }

    private fun setItemDecoration() {
        recyclerView!!.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL_LIST))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main, menu)
        switchListView = menu.findItem(R.id.action_switch_view)
        val search = menu.findItem(R.id.app_bar_search)
        searchView = MenuItemCompat.getActionView(search) as SearchView
        searchView!!.queryHint = "Search channel name"
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (channelAdapter != null) {
                    channelAdapter!!.update(playlistItems!!)
                    //generateDataSet(channelAdapter);
                }
                return if (!TextUtils.isEmpty(newText)) {
                    filter(newText)
                } else false
            }
        })

        if (isGridView) {
            val grid = menu.findItem(R.id.grid)
            grid.isChecked = true
            switchListView.setIcon(R.drawable.ic_grid)
        } else {
            val list = menu.findItem(R.id.list)
            list.isChecked = true
            switchListView.setIcon(R.drawable.ic_list)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> {
            }
            R.id.list -> {
                switchListView.setIcon(R.drawable.ic_list)
                item.isChecked = true
                isGridView = false
                channelAdapter = null
                showChannels()
            }
            R.id.grid -> {
                switchListView.setIcon(R.drawable.ic_grid)
                item.isChecked = true
                isGridView = true
                channelAdapter = null
                showChannels()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun filter(newText: String): Boolean {
        if (channelAdapter != null) {
            channelAdapter!!.filter.filter(newText)
            return true
        } else {
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        channelAdapter = null
        unbinder.unbind()

    }

    @OnClick(R.id.btn_add_playlist)
    fun onClick() {
        if (activity == null) {
            return
        }
        Router.navigateTo(activity!!, Router.Screens.PLAYLIST, false)
    }

    companion object {

        private val COLUMN_WIDTH = 160

        fun newInstance(groupName: String): ChannelFragment {
            val fragment = ChannelFragment()
            fragment.groupName = groupName
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
