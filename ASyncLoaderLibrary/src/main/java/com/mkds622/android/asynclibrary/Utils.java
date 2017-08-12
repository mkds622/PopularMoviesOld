package com.mkds622.android.asynclibrary;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;

import java.util.concurrent.ThreadFactory;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Meetu on 10/08/17.
 */

public class Utils {

    static final String THREAD_PREFIX = "ASyncLibrary-";

    static final char KEY_SEPARATOR = '\n';

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        // Target ~15% of the available heap.
        return (int) (1024L * 1024L * memoryClass / 7);
    }

    static int getBitmapBytes(Bitmap bitmap) {
        int result = SDK_INT >= KITKAT ? bitmap.getAllocationByteCount() : bitmap.getByteCount();
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + bitmap);
        }
        return result;
    }



    static class LoaderThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r) {
            return new LoaderThread(r);
        }
    }

    private static class LoaderThread extends Thread {
        public LoaderThread(Runnable r) {
            super(r);
        }

        @Override public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
