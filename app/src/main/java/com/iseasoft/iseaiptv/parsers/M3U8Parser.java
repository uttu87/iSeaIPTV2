package com.iseasoft.iseaiptv.parsers;

import com.iseasoft.iseaiptv.models.M3UItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class M3U8Parser {

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

    public static ArrayList<M3UItem> createMatchFromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<M3UItem> channels = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) {
            return channels;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            M3UItem channel = createMatchFromJSONObject(jsonObject);
            if (channel != null) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public static M3UItem createMatchFromJSONObject(JSONObject jsonObject) throws JSONException {
        M3UItem channel = new M3UItem();
        if (jsonObject.has(NAME)) {
            channel.setItemName(jsonObject.getString(NAME));
        }

        if (jsonObject.has(STREAM_URL)) {
            channel.setItemUrl(jsonObject.getString(STREAM_URL));
        }
        if (jsonObject.has(IMAGE_URL)) {
            channel.setItemIcon(jsonObject.getString(IMAGE_URL));
        }
        return channel;
    }
}
