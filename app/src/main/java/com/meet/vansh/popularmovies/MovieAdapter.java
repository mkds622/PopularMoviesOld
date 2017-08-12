package com.meet.vansh.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mkds622.android.asynclibrary.ASyncLibrary;
import com.mkds622.android.asynclibrary.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<MoviePoster> {
    private static final String LOG_TAG= MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<MoviePoster> moviePosters)
    {
        super(context,0,moviePosters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MoviePoster moviePoster=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.movie_poster_layout,parent,false);
        }
        PhotoView movie=(PhotoView) convertView.findViewById(R.id.MovieButton);
        if(moviePoster.img_url==null)
        {
        movie.setImageResource(R.mipmap.image_not_found);
        }
        else
        //Picasso.with(getContext()).load(moviePoster.img_url.toString()).into(movie);
            ASyncLibrary.with(getContext()).load(moviePoster.img_url.toString()).into(movie);
        return convertView;
    }
}
