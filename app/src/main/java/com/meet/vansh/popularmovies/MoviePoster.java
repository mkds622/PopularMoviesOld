package com.meet.vansh.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vansh on 8/21/2016.
 */
public class MoviePoster implements Parcelable{
    private final String LOG_TAG =MoviePoster.class.getSimpleName();
    URL img_url;
    String img_link;
    long movie_id;
    static final String baseurl="http://image.tmdb.org/t/p/w185/";

    public MoviePoster(String img_link,long movie_id)throws NullPointerException{
        this.img_link=img_link;
        this.movie_id=movie_id;
        String builduri;
        if(img_link==null) {
            builduri=null;
            img_url=null;
        }
        else {
            builduri = baseurl + img_link;
            try {
                img_url = new URL(builduri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    private MoviePoster(Parcel in){
        this.img_link=in.readString();
        this.movie_id=in.readLong();
        Uri builduri= Uri.parse(baseurl).buildUpon().appendPath(img_link).build();
        try {
            img_url=new URL(builduri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.w(LOG_TAG,"URL:"+img_url);

    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return img_url + "--" + movie_id; }

    @Override
    public void writeToParcel(Parcel parcel,int i){
        parcel.writeString(img_link);
        parcel.writeLong(movie_id);
    }
    public final Parcelable.Creator<MoviePoster> CREATOR=new Parcelable.Creator<MoviePoster>(){
        @Override
        public MoviePoster createFromParcel(Parcel parcel){
            return new MoviePoster(parcel);
        }
        @Override
        public MoviePoster[] newArray(int i){
            return new MoviePoster[i];
        }
    };

}
