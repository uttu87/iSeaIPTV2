package com.iseasoft.iseaiptv.parsers;


import com.iseasoft.iseaiptv.models.Catalog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CatalogParser {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CHANNEL = "channels";

    public static ArrayList<Catalog> createLeagueFromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Catalog> catalogs = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) return catalogs;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Catalog catalog = createLeagueFromJSONObject(jsonObject);
            if (catalog.getChannels().size() > 0) {
                catalogs.add(catalog);
            }
        }
        return catalogs;
    }

    public static Catalog createLeagueFromJSONObject(JSONObject jsonObject) throws JSONException {
        Catalog catalog = new Catalog();
        if (jsonObject.has(ID)) {
            catalog.setId(jsonObject.getInt(ID));
        }
        if (jsonObject.has(NAME)) {
            catalog.setName(jsonObject.getString(NAME));
        }
        if (jsonObject.has(DESCRIPTION)) {
            catalog.setDescription(jsonObject.getString(DESCRIPTION));
        }
        if (jsonObject.has(CHANNEL)) {
            catalog.setChannels(M3U8Parser.createMatchFromJSONArray(jsonObject.getJSONArray(CHANNEL)));
        }
        return catalog;
    }
}
