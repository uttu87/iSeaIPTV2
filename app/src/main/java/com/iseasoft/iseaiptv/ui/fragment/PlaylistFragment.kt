package com.iseasoft.iseaiptv.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.adapters.PlaylistAdapter
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.listeners.OnPlaylistListener
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class PlaylistFragment : Fragment() {

    internal lateinit var unbinder: Unbinder

    @BindView(R.id.list)
    @JvmField
    var list: RecyclerView? = null

    @BindView(R.id.empty_container)
    @JvmField
    var emptyContainer: LinearLayout? = null

    private var playlistAdapter: PlaylistAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { list?.let { it1 -> Utils.modifyListViewForVertical(it, it1) } }
        loadPlaylist()
    }

    private fun loadPlaylist() {
        val playlists = PreferencesUtility.getInstance(activity).playlist
        if (playlists == null || playlists.size == 0) {
            emptyContainer!!.visibility = View.VISIBLE
            list!!.visibility = View.GONE
        } else {
            emptyContainer!!.visibility = View.GONE
            list!!.visibility = View.VISIBLE
            Collections.reverse(playlists)
            playlistAdapter = PlaylistAdapter(playlists, object : OnPlaylistListener {
                override fun onPlaylistItemClicked(item: Playlist) {
                    PreferencesUtility.getInstance(activity).savePlaylist(item)
                    activity?.let { Router.navigateToMainScreen(it, true) }
                }
            })
            list!!.adapter = playlistAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    @OnClick(R.id.add_playlist)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.add_playlist -> activity!!.openOptionsMenu()
        }
    }

    companion object {

        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }


}
