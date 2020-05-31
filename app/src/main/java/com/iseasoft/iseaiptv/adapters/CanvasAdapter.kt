package com.iseasoft.iseaiptv.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.OnChannelListener
import com.iseasoft.iseaiptv.utils.Utils
import java.lang.ref.WeakReference
import java.util.*

class CanvasAdapter(context: Context, private var data: ArrayList<String>?) : RecyclerView.Adapter<CanvasAdapter.ViewHolder>() {
    private var itemClickListener: OnChannelListener? = null
        set
    private var onCanvasListener: OnCanvasListener? = null
        set
    private val context: WeakReference<Context>
    private val mSharedPool = RecyclerView.RecycledViewPool()

    init {
        this.context = WeakReference(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layoutId = R.layout.fragment_horizontal_league
        when (viewType) {
            BANNER_TYPE -> layoutId = R.layout.item_banner_ads
            COVER_TYPE -> layoutId = R.layout.item_cover_ads
            MREC_TYPE -> layoutId = R.layout.item_mrec_ads
            else -> {
            }
        }
        val view = LayoutInflater.from(context.get()).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) != DATA_TYPE) {
            return
        }
        val catalog = data!![position]
        val list = Utils.getItems(catalog)
        if (list == null || list.size == 0) {
            holder.itemView.visibility = View.GONE
            return
        }

        holder.itemView.visibility = View.VISIBLE
        holder.tvLeagueName.text = catalog
        if (list.size < MAX_VISIBLE_PALETTE_ITEM_COUNT) {
            holder.tvShowMore.visibility = View.GONE
        } else {
            holder.tvShowMore.visibility = View.VISIBLE
            holder.tvShowMore.setOnClickListener { v ->
                if (onCanvasListener != null) {
                    onCanvasListener!!.onShowMoreClicked(catalog)
                }
            }
        }

        val dataAdapter = context.get()?.let {
            itemClickListener?.let { it1 ->
                ChannelAdapter(it, R.layout.item_channel_grid,
                        it1)
            }
        }
        holder.rvLeague.adapter = dataAdapter
        dataAdapter?.update(list)
        context.get()?.let { Utils.modifyListViewForHorizontal(it, holder.rvLeague) }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun getItemViewType(position: Int): Int {
        val catalog = data!![position]
        if (catalog.contains("banner")) {
            return BANNER_TYPE
        }

        if (catalog.contains("cover")) {
            return COVER_TYPE
        }

        return if (catalog.contains("mrec")) {
            MREC_TYPE
        } else DATA_TYPE

    }

    fun updateData(data: ArrayList<String>) {
        this.data = data
        this.notifyDataSetChanged()
    }

    fun setItemClickListener(listener: OnChannelListener) {
        itemClickListener = listener
    }

    fun setOnCanvasListener(listener: OnCanvasListener) {
        onCanvasListener = listener
    }

    interface OnCanvasListener {
        fun onShowMoreClicked(league: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val tvLeagueName: TextView
        internal val tvShowMore: TextView
        internal val rvLeague: RecyclerView

        init {
            tvLeagueName = itemView.findViewById(R.id.tv_league_title)
            tvShowMore = itemView.findViewById(R.id.tv_show_more)
            rvLeague = itemView.findViewById(R.id.list)
            rvLeague.setRecycledViewPool(mSharedPool)
            context.get()?.let { Utils.modifyListViewForHorizontal(it, rvLeague) }
        }
    }

    companion object {

        private val DATA_TYPE = 0
        private val BANNER_TYPE = 1
        private val COVER_TYPE = 2
        private val MREC_TYPE = 3

        private val MAX_VISIBLE_PALETTE_ITEM_COUNT = 10
    }
}
