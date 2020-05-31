package com.iseasoft.iseaiptv.ui.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.adapters.CanvasAdapter
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.OnChannelListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.ui.activity.ChannelActivity
import com.iseasoft.iseaiptv.ui.activity.PlayerActivity.Companion.CHANNEL_KEY
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    internal lateinit var unbinder: Unbinder

    @BindView(R.id.list_league)
    @JvmField
    var rvLeagueList: RecyclerView? = null
    private val init = false
    private var mCanvasAdapter: CanvasAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { rvLeagueList?.let { it1 -> Utils.modifyListViewForVertical(it, it1) } }
        setupLeagueAdapter()

    }

    private fun setupLeagueAdapter() {
        val mLeagues = ArrayList<String>()
        if (!PreferencesUtility.getInstance(App.context).hasNoFavorite()) {
            mLeagues.add(getString(R.string.favorites))
        }
        if (!PreferencesUtility.getInstance(App.context).hasNoHistoryWatching()) {
            mLeagues.add(getString(R.string.history_watching))
        }
        mLeagues.add(getString(R.string.all_channels))

        val groupList = LinkedList<String>()
        val channelList = App.channelList
        if (channelList != null) {
            for (i in channelList.indices) {
                val m3UItem = channelList[i]
                if (groupList.contains(m3UItem.itemGroup)) {
                    continue
                }
                m3UItem.itemGroup?.let { groupList.add(it) }
            }
            for (i in groupList.indices) {
                val groupTitle = groupList[i]
                if (!TextUtils.isEmpty(groupTitle)) {
                    mLeagues.add(groupTitle)
                }
            }
        }

        mCanvasAdapter = CanvasAdapter(context!!, mLeagues)
        mCanvasAdapter!!.setOnCanvasListener (object : CanvasAdapter.OnCanvasListener {
            override fun onShowMoreClicked(league: String) {
                val bundle = Bundle()
                bundle.putString(ChannelActivity.CATALOG_KEY, league)
                activity?.let { Router.navigateTo(it, Router.Screens.CHANNEL, bundle, false) }
            }
        })
        mCanvasAdapter!!.setItemClickListener(object : OnChannelListener {
            override fun onChannelClicked(item: M3UItem) {
                val bundle = Bundle()
                bundle.putSerializable(CHANNEL_KEY, item)
                //bundle.putSerializable(PLAYLIST_KEY, getPlaylistItems());
                activity?.let { Router.navigateTo(it, Router.Screens.PLAYER, bundle, false) }
            }
        })
        rvLeagueList?.adapter = mCanvasAdapter
    }

    companion object {
        val TAG = HomeFragment::class.java.simpleName
        private val COVER_ADS_RANGE = 3
    }


}// Required empty public constructor
