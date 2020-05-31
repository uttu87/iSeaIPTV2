package com.iseasoft.iseaiptv.parsers

import android.text.TextUtils
import android.util.Log
import com.iseasoft.iseaiptv.models.M3UItem
import com.iseasoft.iseaiptv.models.M3UPlaylist
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class M3UParser {

    fun convertStreamToString(`is`: InputStream?): String {
        if (`is` == null) {
            return ""
        }
        try {
            return Scanner(`is`).useDelimiter("\\A").next()
        } catch (e: NoSuchElementException) {
            return ""
        }

    }

    @Throws(FileNotFoundException::class)
    fun parseFile(inputStream: InputStream): M3UPlaylist {
        val m3UPlaylist = M3UPlaylist()
        val playlistItems = ArrayList<M3UItem>()
        val stream = convertStreamToString(inputStream)
        val linesArray = stream.split(EXT_INF.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in linesArray.indices) {
            val currLine = linesArray[i]
            if (TextUtils.isEmpty(currLine.trim { it <= ' ' })) {
                continue
            }
            try {
                if (currLine.contains(EXT_M3U)) {
                    //header of file
                    if (currLine.contains(EXT_PLAYLIST_NAME)) {
                        val fileParams = currLine.substring(EXT_M3U.length, currLine.indexOf(EXT_PLAYLIST_NAME))
                        val playListName = currLine.substring(currLine.indexOf(EXT_PLAYLIST_NAME) + EXT_PLAYLIST_NAME.length).replace(":", "")
                        m3UPlaylist.playlistName = playListName
                        m3UPlaylist.playlistParams = fileParams
                    } else {
                        m3UPlaylist.playlistName = "Noname Playlist"
                        m3UPlaylist.playlistParams = "No Params"
                    }
                } else {
                    val playlistItem = M3UItem()
                    val dataArray = currLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (dataArray[0].contains(EXT_LOGO)) {
                        val duration = dataArray[0].substring(0, dataArray[0].indexOf(EXT_LOGO)).replace(":", "").replace("\n", "")
                        var icon = dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO) + EXT_LOGO.length).replace("=", "").replace("\"", "").replace("\n", "")
                        if (icon.contains(EXT_GROUP)) {
                            val strings = icon.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            icon = strings[0]
                        }

                        playlistItem.itemDuration = duration
                        playlistItem.itemIcon = icon
                    } else {
                        val duration = dataArray[0].replace(":", "").replace("\n", "")
                        playlistItem.itemDuration = duration
                        playlistItem.itemIcon = ""
                    }

                    if (dataArray[0].contains(EXT_GROUP)) {
                        var group = dataArray[0].substring(dataArray[0].indexOf(EXT_GROUP) + EXT_GROUP.length)
                                .replace("=", "")
                                .replace("\"", "")
                                .replace("\n", "")
                                .replace(":", "")
                        if (group.contains(EXT_LOGO)) {
                            val strings = group.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            group = strings[0]
                        }
                        playlistItem.itemGroup = group
                    }

                    try {
                        val url = dataArray[1].substring(dataArray[1].indexOf(EXT_URL)).replace("\n", "").replace("\r", "")
                        val name = dataArray[1].substring(0, dataArray[1].indexOf(EXT_URL)).replace("\n", "")
                        playlistItem.itemName = name
                        playlistItem.itemUrl = url
                    } catch (fdfd: Exception) {
                        Log.e("M3UParser", "Error: " + fdfd.fillInStackTrace())
                    }

                    if (!TextUtils.isEmpty(playlistItem.itemUrl)) {
                        playlistItems.add(playlistItem)
                    }
                }
            } catch (e: Exception) {
                Log.e("M3UParser", "Error: " + e.fillInStackTrace())
            }

        }
        m3UPlaylist.playlistItems = playlistItems
        return m3UPlaylist
    }

    companion object {

        private val EXT_M3U = "#EXTM3U"
        private val EXT_INF = "#EXTINF:"
        private val EXT_PLAYLIST_NAME = "#PLAYLIST"
        private val EXT_LOGO = "tvg-logo"
        private val EXT_GROUP = "group-title"
        private val EXT_URL = "http"
        private val IGNORE_CHANNEL = "Jusmin"
        private val ADULT_GROUP = "XXX"
    }
}
