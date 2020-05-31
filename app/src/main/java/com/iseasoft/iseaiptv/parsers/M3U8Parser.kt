package com.iseasoft.iseaiptv.parsers

import com.iseasoft.iseaiptv.models.M3UItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object M3U8Parser {

    val IS_LIVE = "isLive"
    val IS_YOUTUBE = "isYoutube"
    val IS_HIDDEN = "isHidden"
    val CATALOG = "catalog"
    val TYPE = "type"
    val IMAGE_URL = "imageURL"
    val STREAM_URL = "streamURL"
    val NAME = "name"
    val DESCRIPTION = "description"
    val ID = "id"

    @Throws(JSONException::class)
    fun createMatchFromJSONArray(jsonArray: JSONArray?): ArrayList<M3UItem> {
        val channels = ArrayList<M3UItem>()
        if (jsonArray == null || jsonArray.length() == 0) {
            return channels
        }
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val channel = createMatchFromJSONObject(jsonObject)
            if (channel != null) {
                channels.add(channel)
            }
        }
        return channels
    }

    @Throws(JSONException::class)
    fun createMatchFromJSONObject(jsonObject: JSONObject): M3UItem {
        val channel = M3UItem()
        if (jsonObject.has(NAME)) {
            channel.itemName = jsonObject.getString(NAME)
        }

        if (jsonObject.has(STREAM_URL)) {
            channel.itemUrl = jsonObject.getString(STREAM_URL)
        }
        if (jsonObject.has(IMAGE_URL)) {
            channel.itemIcon = jsonObject.getString(IMAGE_URL)
        }
        return channel
    }
}
