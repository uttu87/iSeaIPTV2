package com.iseasoft.iseaiptv.http

import android.util.Log
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

/**
 * Created by sajja on 3/8/2018.
 */

class HttpHandler {

    fun makeServiceCall(reqUrl: String): InputStream? {
        var response: InputStream? = null
        try {
            val url = URL(reqUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // read the response
            response = BufferedInputStream(conn.inputStream)
        } catch (e: MalformedURLException) {
            Log.e(TAG, "MalformedURLException: " + e.message)
        } catch (e: ProtocolException) {
            Log.e(TAG, "ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "IOException: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }

        return response
    }

    companion object {
        private val TAG = HttpHandler::class.java.simpleName
    }
}
