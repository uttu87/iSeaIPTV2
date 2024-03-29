/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.iseasoft.iseaiptv.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iseasoft.iseaiptv.R;
import com.iseasoft.iseaiptv.models.M3UItem;
import com.iseasoft.iseaiptv.models.Playlist;

import java.lang.reflect.Type;
import java.util.ArrayList;

public final class PreferencesUtility {

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    public static final String LAST_ADDED_CUTOFF = "last_added_cutoff";
    public static final String GESTURES = "gestures";
    public static final String FULL_UNLOCKED = "full_version_unlocked";
    private static final String NOW_PLAYING_SELECTOR = "now_paying_selector";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    private static final String TOGGLE_ALBUM_GRID = "toggle_album_grid";
    private static final String TOGGLE_PLAYLIST_VIEW = "toggle_playlist_view";
    private static final String TOGGLE_SHOW_AUTO_PLAYLIST = "toggle_show_auto_playlist";
    private static final String LAST_FOLDER = "last_folder";
    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String START_PAGE_PREFERENCE_LASTOPENED = "start_page_preference_latopened";
    private static final String NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value";
    private static final String TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector";
    private static final String SHOW_LOCKSCREEN_ALBUMART = "show_albumart_lockscreen";
    private static final String ARTIST_ALBUM_IMAGE = "artist_album_image";
    private static final String ARTIST_ALBUM_IMAGE_MOBILE = "artist_album_image_mobile";
    private static final String ALWAYS_LOAD_ALBUM_IMAGES_LASTFM = "always_load_album_images_lastfm";
    private static final String PLAYLIST_KEY = "playlist";
    private static final String FAVORITE_CHANNEL_KEY = "favorite_channel_list";
    private static final String HISTORY_CHANNEL_KEY = "history_channel_list";
    private static final String LAST_PLAYLIST = "last_playlist";
    private static final String GRID_VIEW_MODE = "grid_view_mode";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;
    private static Context context;
    private ConnectivityManager connManager = null;

    public PreferencesUtility(final Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }


    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true);
    }

    public void setArtistsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ARTIST_GRID, b);
        editor.apply();
    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ALBUM_GRID, b);
        editor.apply();
    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }

    public void setStartPageIndex(final int index) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(START_PAGE_INDEX, index);
        editor.apply();
    }

    public void setLastOpenedAsStartPagePreference(boolean preference) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(START_PAGE_PREFERENCE_LASTOPENED, preference);
        editor.apply();
    }

    public boolean lastOpenedIsStartPagePreference() {
        return mPreferences.getBoolean(START_PAGE_PREFERENCE_LASTOPENED, true);
    }

    private void setSortOrder(final String key, final String value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public void setArtistSongSortOrder(final String value) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value);
    }

    public final String getArtistAlbumSortOrder() {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z);
    }

    public void setArtistAlbumSortOrder(final String value) {
        setSortOrder(ARTIST_ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }

    public final boolean didNowplayingThemeChanged() {
        return mPreferences.getBoolean(NOW_PLAYNG_THEME_VALUE, false);
    }

    public void setNowPlayingThemeChanged(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(NOW_PLAYNG_THEME_VALUE, value);
        editor.apply();
    }

    public boolean getXPosedTrackselectorEnabled() {
        return mPreferences.getBoolean(TOGGLE_XPOSED_TRACKSELECTOR, false);
    }

    public int getPlaylistView() {
        return mPreferences.getInt(TOGGLE_PLAYLIST_VIEW, 0);
    }

    public void setPlaylistView(final int i) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(TOGGLE_PLAYLIST_VIEW, i);
        editor.apply();
    }

    public boolean showAutoPlaylist() {
        return mPreferences.getBoolean(TOGGLE_SHOW_AUTO_PLAYLIST, true);
    }

    public void setToggleShowAutoPlaylist(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_SHOW_AUTO_PLAYLIST, b);
        editor.apply();
    }

    public long getLastAddedCutoff() {
        return mPreferences.getLong(LAST_ADDED_CUTOFF, 0L);
    }

    /**
     * @parm lastAddedMillis timestamp in millis used as a cutoff for last added playlist
     */
    public void setLastAddedCutoff(long lastAddedMillis) {
        mPreferences.edit().putLong(LAST_ADDED_CUTOFF, lastAddedMillis).apply();
    }

    public boolean isGesturesEnabled() {
        return mPreferences.getBoolean(GESTURES, true);
    }

    public void storeLastFolder(String path) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LAST_FOLDER, path);
        editor.apply();
    }

    public String getLastFolder() {
        return mPreferences.getString(LAST_FOLDER, Environment.getExternalStorageDirectory().getPath());
    }

    public boolean fullUnlocked() {
        return mPreferences.getBoolean(FULL_UNLOCKED, true);
    }

    public void setFullUnlocked(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FULL_UNLOCKED, b);
        editor.apply();
    }

    public boolean getSetAlbumartLockscreen() {
        return mPreferences.getBoolean(SHOW_LOCKSCREEN_ALBUMART, true);
    }

    /*
    public void updateService(Bundle extras) {
        if(!MusicPlayer.isPlaybackServiceConnected())return;
        final Intent intent = new Intent(context, MusicService.class);
        intent.setAction(MusicService.UPDATE_PREFERENCES);
        intent.putExtras(extras);
        context.startService(intent);
    }
    */

    public boolean loadArtistAndAlbumImages() {
        if (mPreferences.getBoolean(ARTIST_ALBUM_IMAGE, true)) {
            if (!mPreferences.getBoolean(ARTIST_ALBUM_IMAGE_MOBILE, true)) {
                if (connManager == null)
                    connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = connManager.getActiveNetworkInfo();
                return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
            }
            return true;
        }
        return false;
    }

    public boolean alwaysLoadAlbumImagesFromLastfm() {
        return mPreferences.getBoolean(ALWAYS_LOAD_ALBUM_IMAGES_LASTFM, false);
    }

    public boolean isGridViewMode() {
        return mPreferences.getBoolean(GRID_VIEW_MODE, true);
    }

    public void setGridViewMode(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(GRID_VIEW_MODE, value);
        editor.apply();
    }

    /**
     * Save and get ArrayList in SharedPreference
     */

    private <T> void saveArrayList(ArrayList<T> list, String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    private <T> ArrayList<T> getArrayList(String key) {
        Gson gson = new Gson();
        String json = mPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void savePlaylist(Playlist playlist) {
        ArrayList<Playlist> playlists = getPlaylist();
        if (playlists == null) {
            playlists = new ArrayList<>();
        } else {
            for (int i = 0; i < playlists.size(); i++) {
                Playlist p = playlists.get(i);
                if (p.getLink().equals(playlist.getLink())) {
                    playlists.remove(i);
                }
            }
        }

        playlists.add(playlist);
        saveLastPlaylist(playlist);
        savePlaylist(playlists);
    }

    public void savePlaylist(ArrayList<Playlist> list) {
        saveArrayList(list, PLAYLIST_KEY);
    }

    public ArrayList<Playlist> getPlaylist() {
        Gson gson = new Gson();
        String json = mPreferences.getString(PLAYLIST_KEY, null);
        Type type = new TypeToken<ArrayList<Playlist>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveLastPlaylist(Playlist playlist) {
        SharedPreferences.Editor editor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlist);
        editor.putString(LAST_PLAYLIST, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public Playlist getLastPlaylist() {
        Gson gson = new Gson();
        String json = mPreferences.getString(LAST_PLAYLIST, null);
        Type type = new TypeToken<Playlist>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public boolean checkFavorite(M3UItem channel) {
        ArrayList<M3UItem> favList = getFavoriteChannels();
        if (favList == null || favList.size() == 0) {
            return false;
        }
        for (M3UItem item : favList) {
            if (item.getItemName().equals(channel.getItemName())
                    && item.getItemUrl().equals(channel.getItemUrl())) {
                return true;
            }
        }
        return false;
    }

    public void favorite(M3UItem channel) {
        if (checkFavorite(channel)) {
            removeFavorite(channel);
        } else {
            addFavorite(channel);
        }
    }

    public void addFavorite(M3UItem channel) {
        ArrayList<M3UItem> favList = getFavoriteChannels();
        if (favList == null) {
            favList = new ArrayList<>();
        }

        if (!checkFavorite(channel)) {
            favList.add(channel);
            saveFavoriteChannels(favList);
            Toast.makeText(context, R.string.added_favorite_message, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFavorite(M3UItem channel) {
        ArrayList<M3UItem> favList = getFavoriteChannels();
        if (favList == null || favList.size() == 0) {
            return;
        }

        if (checkFavorite(channel)) {
            for (M3UItem item : favList) {
                if (item.getItemName().equals(channel.getItemName())
                        && item.getItemUrl().equals(channel.getItemUrl())) {
                    favList.remove(item);
                    saveFavoriteChannels(favList);
                    Toast.makeText(context, R.string.removed_favorite_message, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    public void saveFavoriteChannels(ArrayList<M3UItem> list) {
        saveArrayList(list, FAVORITE_CHANNEL_KEY);
    }

    public ArrayList<M3UItem> getFavoriteChannels() {
        Gson gson = new Gson();
        String json = mPreferences.getString(FAVORITE_CHANNEL_KEY, null);
        Type type = new TypeToken<ArrayList<M3UItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public boolean checkHistory(M3UItem channel) {
        ArrayList<M3UItem> historyList = getHistoryChannels();
        if (historyList == null || historyList.size() == 0) {
            return false;
        }
        for (M3UItem item : historyList) {
            if (item.getItemName().equals(channel.getItemName())
                    && item.getItemUrl().equals(channel.getItemUrl())) {
                return true;
            }
        }
        return false;
    }

    public void addHistory(M3UItem channel) {
        ArrayList<M3UItem> historyList = getHistoryChannels();
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        if (checkHistory(channel)) {
            ArrayList<M3UItem> listToRemove = new ArrayList<>();
            for (M3UItem item : historyList) {
                if (item.getItemName().equals(channel.getItemName())
                        && item.getItemUrl().equals(channel.getItemUrl())) {
                    listToRemove.add(item);
                }
            }
            historyList.removeAll(listToRemove);
        }
        historyList.add(0, channel);
        saveHistoryChannels(historyList);
    }

    public void saveHistoryChannels(ArrayList<M3UItem> list) {
        saveArrayList(list, HISTORY_CHANNEL_KEY);
    }

    public ArrayList<M3UItem> getHistoryChannels() {
        Gson gson = new Gson();
        String json = mPreferences.getString(HISTORY_CHANNEL_KEY, null);
        Type type = new TypeToken<ArrayList<M3UItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public boolean hasNoHistoryWatching() {
        ArrayList<M3UItem> historyList = getHistoryChannels();
        return historyList == null || historyList.isEmpty();
    }
}

