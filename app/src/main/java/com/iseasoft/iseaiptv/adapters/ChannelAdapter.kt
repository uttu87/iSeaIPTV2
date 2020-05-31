package com.iseasoft.iseaiptv.adapters

/*
  Created by fedor on 28.11.2016.
 */


import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.OnChannelListener
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.squareup.picasso.Picasso


class ChannelAdapter(private val mContext: Context, private val layoutId: Int, private val listener: OnChannelListener) : AdsAdapter(), Filterable {
    private val mInflater: LayoutInflater
    private val generator = ColorGenerator.MATERIAL

    init {
        dataSet = ArrayList()
        mInflater = LayoutInflater.from(mContext)
        isGrid = layoutId == R.layout.item_channel_grid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            return super.onCreateViewHolder(parent, viewType)
        }
        val sView = mInflater.inflate(layoutId, parent, false)
        return ItemHolder(sView)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == NATIVE_EXPRESS_AD_VIEW_TYPE) {
            super.onBindViewHolder(viewHolder, position)
            return
        }
        val holder = viewHolder as ItemHolder
        val item = dataSet!![position] as M3UItem
        if (item != null) {
            holder.update(item)
        }
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    fun update(_list: ArrayList<M3UItem>) {
        this.dataSet!!.clear()
        this.dataSet!!.addAll(_list)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() { //TODO search it on github
            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults?) {
                if (results == null || results.values == null) {
                    return
                }
                if (results.values is ArrayList<*>) {
                    dataSet!!.clear()
                    dataSet!!.addAll(results.values as ArrayList<M3UItem>)
                }
                notifyDataSetChanged()
            }

            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val results = Filter.FilterResults()
                val resultList = ArrayList<M3UItem>()
                if (constraint.length != 0) {
                    val filtePatt = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (itm in dataSet!!) {
                        if (itm is M3UItem) {
                            if (itm.itemName?.toLowerCase()?.contains(filtePatt)!!) {
                                resultList.add(itm)
                            }
                        }
                    }
                }
                results.values = resultList
                results.count = resultList.size
                return results
            }
        }
    }

    inner class ItemHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        internal var name: TextView
        internal var cImg: ImageView

        private val item: Any
            get() {
                val position = layoutPosition
                return dataSet!![position]
            }

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            name = view.findViewById(R.id.item_name)
            cImg = view.findViewById(R.id.cimg)
        }

        internal fun update(item: M3UItem) {
            try {
                name.text = item.itemName

                if (TextUtils.isEmpty(item.itemIcon)) {
                    cImg.setImageResource(R.drawable.ic_logo)
                } else {
                    Picasso.get()
                            .load(item.itemIcon)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(cImg)
                }

            } catch (ignored: Exception) {
            }

        }

        override fun onClick(v: View) {
            try {
                callbackListener()
            } catch (ignored: Exception) {
            }

        }

        private fun callbackListener() {
            if (listener != null && item is M3UItem) {
                listener.onChannelClicked(item as M3UItem)
            }
        }

        override fun onLongClick(v: View): Boolean {
            val selectedItem = item
            if (selectedItem is M3UItem) {
                val popupMenu = PopupMenu(mContext, name)
                popupMenu.inflate(R.menu.menu_options)
                val favoriteItem = popupMenu.menu.findItem(R.id.action_favorite)
                val faved = PreferencesUtility.getInstance(mContext).checkFavorite(selectedItem)
                favoriteItem.setTitle(if (faved) R.string.action_remove_favorite else R.string.action_add_favorite)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_play -> callbackListener()
                        R.id.action_favorite -> favorite(selectedItem)
                    }
                    false
                }
                popupMenu.show()
                return true
            }
            return false
        }

        private fun favorite(m3UItem: M3UItem) {
            val preferencesUtility = PreferencesUtility.getInstance(mContext)
            preferencesUtility.favorite(m3UItem)
        }
    }
}
