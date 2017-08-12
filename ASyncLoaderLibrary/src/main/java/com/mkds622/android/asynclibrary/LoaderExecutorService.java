package com.mkds622.android.asynclibrary;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Meetu on 09/08/17.
 */

public class LoaderExecutorService extends ThreadPoolExecutor {

    private static final int DEFAULT_NO_OF_THREADS=3;

    LoaderExecutorService(){
        super(DEFAULT_NO_OF_THREADS,DEFAULT_NO_OF_THREADS,1, TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(),new Utils.LoaderThreadFactory());
    }

    void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(DEFAULT_NO_OF_THREADS);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(DEFAULT_NO_OF_THREADS);
                }
                break;
            default:
                setThreadCount(DEFAULT_NO_OF_THREADS);
        }
    }

    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

//    @Override
//    public Future<?> submit(Runnable task) {
//        PicassoFutureTask ftask = new PicassoFutureTask((BitmapHunter) task);
//        execute(ftask);
//        return ftask;
//    }
//
//    private static final class PicassoFutureTask extends FutureTask<BitmapHunter>
//            implements Comparable<PicassoFutureTask> {
//        private final BitmapHunter hunter;
//
//        public PicassoFutureTask(BitmapHunter hunter) {
//            super(hunter, null);
//            this.hunter = hunter;
//        }
//
//        @Override
//        public int compareTo(PicassoFutureTask other) {
//            ASyncLibrary.Priority p1 = hunter.getPriority();
//            ASyncLibrary.Priority p2 = other.hunter.getPriority();
//
//            // High-priority requests are "lesser" so they are sorted to the front.
//            // Equal priorities are sorted by sequence number to provide FIFO ordering.
//            return (p1 == p2 ? hunter.sequence - other.hunter.sequence : p2.ordinal() - p1.ordinal());
//        }
//    }
}
