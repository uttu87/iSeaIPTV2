package com.iseasoft.iseaiptv.parsers

import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.models.Channel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object ChannelParser {

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
    fun createMatchFromJSONArray(jsonArray: JSONArray?): ArrayList<Channel> {
        val channels = ArrayList<Channel>()
        if (jsonArray == null || jsonArray.length() == 0) {
            return channels
        }
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val channel = createMatchFromJSONObject(jsonObject)
            if (!channel.isHidden || App.isDebugBuild) {
                channels.add(channel)
            }
        }
        return channels
    }

    @Throws(JSONException::class)
    fun createMatchFromJSONObject(jsonObject: JSONObject): Channel {
        val channel = Channel()
        if (jsonObject.has(ID)) {
            channel.id = jsonObject.getInt(ID)
        }
        if (jsonObject.has(NAME)) {
            channel.name = jsonObject.getString(NAME)
            channel.itemName = jsonObject.getString(NAME)
        }

        if (jsonObject.has(DESCRIPTION)) {
            channel.description = jsonObject.getString(DESCRIPTION)
        }

        if (jsonObject.has(STREAM_URL)) {
            channel.streamUrl = jsonObject.getString(STREAM_URL)
            channel.itemUrl = jsonObject.getString(STREAM_URL)
        }
        if (jsonObject.has(IMAGE_URL)) {
            channel.thumbnailUrl = jsonObject.getString(IMAGE_URL)
            channel.itemIcon = jsonObject.getString(IMAGE_URL)
        }
        if (jsonObject.has(TYPE)) {
            channel.type = jsonObject.getString(TYPE)
        }
        if (jsonObject.has(CATALOG)) {
            channel.catalog = jsonObject.getString(CATALOG)
        }
        if (jsonObject.has(IS_LIVE)) {
            channel.isLive = jsonObject.getBoolean(IS_LIVE)
        }
        if (jsonObject.has(IS_YOUTUBE)) {
            channel.isYoutube = jsonObject.getBoolean(IS_YOUTUBE)
        }
        if (jsonObject.has(IS_HIDDEN)) {
            channel.isHidden = jsonObject.getBoolean(IS_HIDDEN)
        } else {
            channel.isHidden = false
        }
        return channel
    }
}
