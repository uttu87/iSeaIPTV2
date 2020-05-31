package com.iseasoft.iseaiptv.parsers


import com.iseasoft.iseaiptv.models.Catalog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object CatalogParser {

    val ID = "id"
    val NAME = "name"
    val DESCRIPTION = "description"
    val CHANNEL = "channels"

    @Throws(JSONException::class)
    fun createLeagueFromJSONArray(jsonArray: JSONArray?): ArrayList<Catalog> {
        val catalogs = ArrayList<Catalog>()
        if (jsonArray == null || jsonArray.length() == 0) return catalogs
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val catalog = createLeagueFromJSONObject(jsonObject)
            if (catalog.channels!!.size > 0) {
                catalogs.add(catalog)
            }
        }
        return catalogs
    }

    @Throws(JSONException::class)
    fun createLeagueFromJSONObject(jsonObject: JSONObject): Catalog {
        val catalog = Catalog()
        if (jsonObject.has(ID)) {
            catalog.id = jsonObject.getInt(ID)
        }
        if (jsonObject.has(NAME)) {
            catalog.name = jsonObject.getString(NAME)
        }
        if (jsonObject.has(DESCRIPTION)) {
            catalog.description = jsonObject.getString(DESCRIPTION)
        }
        if (jsonObject.has(CHANNEL)) {
            catalog.channels = M3U8Parser.createMatchFromJSONArray(jsonObject.getJSONArray(CHANNEL))
        }
        return catalog
    }
}
