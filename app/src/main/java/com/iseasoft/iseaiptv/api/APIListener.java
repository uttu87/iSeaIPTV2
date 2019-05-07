package com.iseasoft.iseaiptv.api;

public interface APIListener<T> {
    void onRequestCompleted(T obj, String json);

    void onError(Error e);
}
