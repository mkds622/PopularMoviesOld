package com.meet.vansh.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkds622.android.asynclibrary.ASyncLibrary;
import com.mkds622.android.asynclibrary.PhotoView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Details_Menu extends Fragment {
    public Details_Menu(){
    }
    private final String LOG_TAG =Details_Menu.class.getSimpleName();
    private void getMovieDataFromJSON(String jsonMovieDetail,View rootview)throws JSONException {
        URL img_url;
        JSONObject J1= new JSONObject(jsonMovieDetail);
        //setting image thumbnail
        PhotoView I1=(PhotoView)rootview.findViewById(R.id.MoviePosterThumbnail);
        final String baseurl="http://image.tmdb.org/t/p/w780/";
        String img_link=J1.getString("poster_path");
        String builduri=new String();
        if(img_link==null) {
            I1.setImageResource(R.mipmap.image_not_found);
        }
        else {
            builduri = baseurl + img_link;
            try {
                img_url = new URL(builduri.toString());
                //Picasso.with(getContext()).load(img_url.toString()).into(I1);
                ASyncLibrary x=ASyncLibrary.with(getContext());
                x.load(img_url.toString()).into(I1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        //setting Title
        TextView Title=(TextView)rootview.findViewById(R.id.Title);
        Title.setText(J1.getString("title"));
        //setting Synopsis
        TextView Synopsis=(TextView)rootview.findViewById(R.id.Synopsis);
        Synopsis.setText(J1.getString("overview"));
        //setting User Rating
        TextView UserRating=(TextView)rootview.findViewById(R.id.UserRating);
        UserRating.setText(J1.getString("vote_average"));
        //setting Release Date
        TextView ReleaseDate=(TextView)rootview.findViewById(R.id.ReleaseDate);
        ReleaseDate.setText(J1.getString("release_date"));

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonMovieDetailString = null;
                String id=new String();
        try {
            Intent intent = getActivity().getIntent();
            if(intent !=null && intent.hasExtra(Intent.EXTRA_TEXT) )
            {
                id=intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            final String detailurl = "https://api.themoviedb.org/3/movie/"+id+"?";
            final String Api_key_param = "api_key";
            final String apikey = BuildConfig.MovieDB_Api_Key;
            Uri builturi = Uri.parse(detailurl).buildUpon()
                    .appendQueryParameter(Api_key_param, apikey).build();
            URL url = new URL(builturi.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonMovieDetailString = buffer.toString();
            try {
                getMovieDataFromJSON(jsonMovieDetailString,container);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.w(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.w(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return null;
    }


}
