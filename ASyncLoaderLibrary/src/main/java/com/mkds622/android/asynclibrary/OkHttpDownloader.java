package com.mkds622.android.asynclibrary;

/**
 * Created by Meetu on 10/08/17.
 */


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** A {@link Downloader} which uses OkHttp to download images. */
public final class OkHttpDownloader implements Downloader {
    @VisibleForTesting
    final Call.Factory client;
    private final Cache cache;
    private boolean sharedClient = true;

    /**
     * Create new downloader that uses OkHttp. This will install an image cache into your application
     * cache directory.
     */
    public OkHttpDownloader() {
        this.client=null;
        this.cache=null;
    }
    /**
     * Create a new downloader that uses the specified OkHttp instance. A response cache will not be
     * automatically configured.
     */
    public OkHttpDownloader(OkHttpClient client) {
        this.client = client;
        this.cache = client.cache();
    }

    /** Create a new downloader that uses the specified {@link Call.Factory} instance. */
    public OkHttpDownloader(Call.Factory client) {
        this.client = client;
        this.cache = null;
    }

    @NonNull
    @Override public Response load(@NonNull Request request) throws IOException {
        Response response = client.newCall(request).execute();
        return response;
    }
    public Request RequestBuilder(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return request;
    }
    @Override public void shutdown() {
        if (!sharedClient && cache != null) {
            try {
                cache.close();
            } catch (IOException ignored) {
            }
        }
    }
}

