package com.iseasoft.iseaiptv.api

interface APIListener<T> {
    fun onRequestCompleted(obj: T, json: String)

    fun onError(e: Error)
}
