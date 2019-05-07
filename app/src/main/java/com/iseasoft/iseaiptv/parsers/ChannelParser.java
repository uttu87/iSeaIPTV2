package com.iseasoft.iseaiptv.parsers;

import com.iseasoft.iseaiptv.App;
import com.iseasoft.iseaiptv.models.Channel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelParser {

    public static final String IS_LIVE = "isLive";
    public static final String IS_YOUTUBE = "isYoutube";
    public static final String IS_HIDDEN = "isHidden";
    public static final String CATALOG = "catalog";
    public static final String TYPE = "type";
    public static final String IMAGE_URL = "imageURL";
    public static final String STREAM_URL = "streamURL";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ID = "id";

    public static ArrayList<Channel> createMatchFromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Channel> channels = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) {
            return channels;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Channel channel = createMatchFromJSONObject(jsonObject);
            if (!channel.isHidden() || App.isDebugBuild()) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public static Channel createMatchFromJSONObject(JSONObject jsonObject) throws JSONException {
        Channel channel = new Channel();
        if (jsonObject.has(ID)) {
            channel.setId(jsonObject.getInt(ID));
        }
        if (jsonObject.has(NAME)) {
            channel.setName(jsonObject.getString(NAME));
        }

        if (jsonObject.has(DESCRIPTION)) {
            channel.setDescription(jsonObject.getString(DESCRIPTION));
        }

        if (jsonObject.has(STREAM_URL)) {
            channel.setStreamUrl(jsonObject.getString(STREAM_URL));
        }
        if (jsonObject.has(IMAGE_URL)) {
            channel.setThumbnailUrl(jsonObject.getString(IMAGE_URL));
        }
        if (jsonObject.has(TYPE)) {
            channel.setType(jsonObject.getString(TYPE));
        }
        if (jsonObject.has(CATALOG)) {
            channel.setCatalog(jsonObject.getString(CATALOG));
        }
        if (jsonObject.has(IS_LIVE)) {
            channel.setLive(jsonObject.getBoolean(IS_LIVE));
        }
        if (jsonObject.has(IS_YOUTUBE)) {
            channel.setYoutube(jsonObject.getBoolean(IS_YOUTUBE));
        }
        if (jsonObject.has(IS_HIDDEN)) {
            channel.setHidden(jsonObject.getBoolean(IS_HIDDEN));
        } else {
            channel.setHidden(false);
        }
        return channel;
    }
}
