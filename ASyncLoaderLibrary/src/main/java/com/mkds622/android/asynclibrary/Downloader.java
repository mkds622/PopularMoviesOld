package com.mkds622.android.asynclibrary;

/**
 * Created by Meetu on 10/08/17.
 */

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Response;

/** A mechanism to load images from external resources such as cache and/or the internet. */
public interface Downloader {
    /**
     * Download the specified image {@code url} from the internet.
     *
     * @throws IOException if the requested URL cannot successfully be loaded.
     */
    @NonNull
    Response load(@NonNull okhttp3.Request request) throws IOException;

    /**
     * Allows to perform a clean up for this {@link Downloader} including closing the disk cache and
     * other resources.
     */
    void shutdown();
}
