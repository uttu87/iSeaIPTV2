package com.iseasoft.iseaiptv.parsers;

import android.text.TextUtils;
import android.util.Log;

import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.models.M3UPlaylist;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class M3UParser {

    private static final String EXT_M3U = "#EXTM3U";
    private static final String EXT_INF = "#EXTINF:";
    private static final String EXT_PLAYLIST_NAME = "#PLAYLIST";
    private static final String EXT_LOGO = "tvg-logo";
    private static final String EXT_GROUP = "group-title";
    private static final String EXT_URL = "http";

    public String convertStreamToString(InputStream is) {
        if (is == null) {
            return "";
        }
        try {
            return new Scanner(is).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public M3UPlaylist parseFile(InputStream inputStream) throws FileNotFoundException {
        M3UPlaylist m3UPlaylist = new M3UPlaylist();
        ArrayList<M3UItem> playlistItems = new ArrayList<>();
        String stream = convertStreamToString(inputStream);
        String linesArray[] = stream.split(EXT_INF);
        for (int i = 0; i < linesArray.length; i++) {
            String currLine = linesArray[i];
            if (TextUtils.isEmpty(currLine.trim())) {
                continue;
            }
            try {
                if (currLine.contains(EXT_M3U)) {
                    //header of file
                    if (currLine.contains(EXT_PLAYLIST_NAME)) {
                        String fileParams = currLine.substring(EXT_M3U.length(), currLine.indexOf(EXT_PLAYLIST_NAME));
                        String playListName = currLine.substring(currLine.indexOf(EXT_PLAYLIST_NAME) + EXT_PLAYLIST_NAME.length()).replace(":", "");
                        m3UPlaylist.setPlaylistName(playListName);
                        m3UPlaylist.setPlaylistParams(fileParams);
                    } else {
                        m3UPlaylist.setPlaylistName("Noname Playlist");
                        m3UPlaylist.setPlaylistParams("No Params");
                    }
                } else {
                    M3UItem playlistItem = new M3UItem();
                    String[] dataArray = currLine.split(",");
                    if (dataArray[0].contains(EXT_LOGO)) {
                        String duration = dataArray[0].substring(0, dataArray[0].indexOf(EXT_LOGO)).replace(":", "").replace("\n", "");
                        String icon = dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO) + EXT_LOGO.length()).replace("=", "").replace("\"", "").replace("\n", "");
                        if (icon.contains(EXT_GROUP)) {
                            String[] strings = icon.split(" ");
                            icon = strings[0];
                        }

                        playlistItem.setItemDuration(duration);
                        playlistItem.setItemIcon(icon);
                    } else {
                        String duration = dataArray[0].replace(":", "").replace("\n", "");
                        playlistItem.setItemDuration(duration);
                        playlistItem.setItemIcon("");
                    }

                    if (dataArray[0].contains(EXT_GROUP)) {
                        String group = dataArray[0].substring(dataArray[0].indexOf(EXT_GROUP) + EXT_GROUP.length())
                                .replace("=", "")
                                .replace("\"", "")
                                .replace("\n", "")
                                .replace(":", "");
                        if (group.contains(EXT_LOGO)) {
                            String[] strings = group.split(" ");
                            group = strings[0];
                        }
                        playlistItem.setItemGroup(group);
                    }

                    try {
                        String url = dataArray[1].substring(dataArray[1].indexOf(EXT_URL)).replace("\n", "").replace("\r", "");
                        String name = dataArray[1].substring(0, dataArray[1].indexOf(EXT_URL)).replace("\n", "");
                        playlistItem.setItemName(name);
                        playlistItem.setItemUrl(url);
                    } catch (Exception fdfd) {
                        Log.e("M3UParser", "Error: " + fdfd.fillInStackTrace());
                    }
                    if (!TextUtils.isEmpty(playlistItem.getItemUrl())) {
                        playlistItems.add(playlistItem);
                    }
                }
            } catch (Exception e) {
                Log.e("M3UParser", "Error: " + e.fillInStackTrace());
            }
        }
        m3UPlaylist.setPlaylistItems(playlistItems);
        return m3UPlaylist;
    }
}
