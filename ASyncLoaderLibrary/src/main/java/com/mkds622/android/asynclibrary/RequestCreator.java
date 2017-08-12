package com.mkds622.android.asynclibrary;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.net.URL;


/**
 * Created by Meetu on 10/08/17.
 */

public class RequestCreator {

    ASyncLibrary aSyncLibrary;
    Uri uri;
    public RequestCreator(ASyncLibrary aSyncLibrary, Uri uri){
        this.aSyncLibrary= aSyncLibrary;
        this.uri=uri;
    }

    public void into(@NonNull PhotoView mPhotoView) {
        try {
            URL url=new URL(uri.toString());
            mPhotoView.setImageURL(url,true,null);
        } catch (Exception e) {
            Log.e("RequestCreator", e.toString());
        }

    }
}
