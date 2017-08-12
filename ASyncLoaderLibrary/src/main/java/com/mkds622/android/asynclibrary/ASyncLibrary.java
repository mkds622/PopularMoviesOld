package com.mkds622.android.asynclibrary;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import java.io.File;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;

/**
 * Image Downloading and Caching manager.
 */

public class ASyncLibrary {

    final Context context;
    final LruCache cache;
    private Downloader downloader;
    private ExecutorService service;

    boolean shutdown;

    private final Listener listener;

    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }

    ASyncLibrary(Context context, LruCache cache, Listener listener, Downloader downloader, ExecutorService service){
        this.context=context;
        this.cache=cache;
        this.listener=listener;
    }

    /** Callbacks. */
    public interface Listener {
        /**
         * Invoked when an Image fails to load. Used for reporting Load Failures.
         */
        void onImageLoadFailed(ASyncLibrary aSyncLibrary, Uri uri, Exception exception);
    }

    static volatile ASyncLibrary singleton=null;
    public static ASyncLibrary with(Context context) {
        if (singleton == null) {
            synchronized (ASyncLibrary.class) {
                if (singleton == null) {
                    if (context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    singleton = new LibraryInstanceBuilder(context).build();
                }
            }
        }
        return singleton;
    }


    /** Fluent API for creating {@link ASyncLibrary} instances. */
    @SuppressWarnings("UnusedDeclaration") // Public API.
    public static class LibraryInstanceBuilder {

        private final Context context;
        private Downloader downloader;
        private ExecutorService service;
        private LruCache cache;
        private Listener listener;
        //private Bitmap.Config defaultBitmapConfig;

        //private boolean indicatorsEnabled;
        //private boolean loggingEnabled;

        /** Start building a new {@link ASyncLibrary} instance. */
        public LibraryInstanceBuilder(@NonNull Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        /** Specify the {@link Downloader} that will be used for downloading images. */
        public LibraryInstanceBuilder downloader(@NonNull Downloader downloader) {
            if (downloader == null) {
                throw new IllegalArgumentException("Downloader must not be null.");
            }
            if (this.downloader != null) {
                throw new IllegalStateException("Downloader already set.");
            }
            this.downloader = downloader;
            return this;
        }

        /**
         * Specify the executor service for loading images in the background.
         * <p>
         * Note: Calling {@link LoaderExecutorService#shutdown() shutdown()} will not shutdown supplied executors.
         */
        public LibraryInstanceBuilder executor(@NonNull LoaderExecutorService executorService) {
            if (executorService == null) {
                throw new IllegalArgumentException("Executor service must not be null.");
            }
            if (this.service != null) {
                throw new IllegalStateException("Executor service already set.");
            }
            this.service = executorService;
            return this;
        }

        /** Specify the memory cache used for the most recent images. */
        public LibraryInstanceBuilder memoryCache(@NonNull LruCache memoryCache) {
            if (memoryCache == null) {
                throw new IllegalArgumentException("Memory cache must not be null.");
            }
            if (this.cache != null) {
                throw new IllegalStateException("Memory cache already set.");
            }
            this.cache = memoryCache;
            return this;
        }

        /** Specify a listener for interesting events. */
        public LibraryInstanceBuilder listener(@NonNull Listener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Listener must not be null.");
            }
            if (this.listener != null) {
                throw new IllegalStateException("Listener already set.");
            }
            this.listener = listener;
            return this;
        }


//        /** Toggle whether to display debug indicators on images. */
//        public LibraryInstanceBuilder indicatorsEnabled(boolean enabled) {
//            this.indicatorsEnabled = enabled;
//            return this;
//        }
//
//        /**
//         * Toggle whether debug logging is enabled.
//         * <p>
//         * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
//         * be used for debugging purposes. Do NOT pass {@code BuildConfig.DEBUG}.
//         */
//        public LibraryInstanceBuilder loggingEnabled(boolean enabled) {
//            this.loggingEnabled = enabled;
//            return this;
//        }

        /** Create the {@link ASyncLibrary} instance. */
        public ASyncLibrary build() {
            Context context = this.context;

            if (downloader == null) {
                downloader = new OkHttpDownloader();
            }
            if (cache == null) {
                ActivityManager am= (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
                int maxSize= am.getMemoryClass()*1024*1024;
                cache = new LruCache(maxSize/7);
            }
            if (service == null) {
                service = new LoaderExecutorService();
            }





            return new ASyncLibrary(context, cache, listener, downloader, service);
        }
    }




    /**
     * Start an image request using the specified URI.
     * <p>
     * Passing {@code null} as a {@code uri} will not trigger any request but will set a placeholder,
     * if one is specified.
     *
     * @see #load(String)
     */
    public RequestCreator load(@Nullable Uri uri) {
        return new RequestCreator(this, uri);
    }

    /**
     * Start an image request using the specified path. This is a convenience method for calling
     * {@link #load(Uri)}.
     * <p>
     * This path may be a remote URL, file resource (prefixed with {@code file:}), content resource
     * (prefixed with {@code content:}), or android resource (prefixed with {@code
     * android.resource:}.
     * <p>
     * Passing {@code null} as a {@code path} will not trigger any request but will set a
     * placeholder, if one is specified.
     *
     * @see #load(Uri)
     * @throws IllegalArgumentException if {@code path} is empty or blank string.
     */
    public RequestCreator load(@Nullable String path) {
        if (path == null) {
            return new RequestCreator(this, null);
        }
        if (path.trim().length() == 0) {
            throw new IllegalArgumentException("Path must not be empty.");
        }
        return load(Uri.parse(path));
    }


    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     *
     * @see #invalidate(String)
     */
    public void invalidate(@Nullable Uri uri) {
        if (uri != null) {
            cache.remove(uri.toString());
        }
    }

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     * {@linkplain RequestCreator#stableKey stable key}.
     *
     * @see #invalidate(Uri)
     */
    public void invalidate(@Nullable String path) {
        if (path != null) {
            invalidate(Uri.parse(path));
        }
    }

    /** Stops this instance from accepting further requests. */
    public void shutdown() {
        if (this == singleton) {
            throw new UnsupportedOperationException("Default singleton instance cannot be shutdown.");
        }
        if (shutdown) {
            return;
        }
        cache.evictAll();
//        cleanupThread.shutdown();
//        stats.shutdown();
//        dispatcher.shutdown();
//        for (DeferredRequestCreator deferredRequestCreator : targetToDeferredRequestCreator.values()) {
//            deferredRequestCreator.cancel();
//        }
//        targetToDeferredRequestCreator.clear();
        shutdown = true;
    }

}
