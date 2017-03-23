package com.meet.vansh.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Main_Menu extends Fragment {


    public Main_Menu() {
        // Required empty public constructor
    }
    MovieAdapter movieAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View movieView=inflater.inflate(R.layout.main_menu,container,false);
        List<MoviePoster> movieList=new ArrayList<MoviePoster>();
        movieAdapter=new MovieAdapter(getActivity(),movieList);
        GridView movie_poster_grid=(GridView)movieView.findViewById(R.id.movie_poster_grid);
        movie_poster_grid.setAdapter(movieAdapter);
        movie_poster_grid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                    MoviePoster selected=movieAdapter.getItem(position);
                    Intent I1=new Intent(getActivity(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,String.valueOf(selected.movie_id));
                    startActivity(I1);
            }

        });


        return movieView;
    }


    private void updateMovieList()
    {
        final ExtractMovieTask movieTask=new ExtractMovieTask();
        RadioGroup radioGroup=(RadioGroup)getActivity().findViewById(R.id.RadioSort);
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.popular_radio:
                movieTask.execute("popular");
                break;

            case R.id.toprated_radio:
                movieTask.execute("top_rated");
                break;
        };
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId)
                                                  {
                                                      switch(checkedId) {
                                                          case R.id.popular_radio:
                                                              updateMovieList();
                                                              break;

                                                          case R.id.toprated_radio:
                                                              updateMovieList();
                                                              break;
                                                      }
                                                  }
                                              }
        );

    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateMovieList();
    }
    public class ExtractMovieTask extends AsyncTask<String,Void,MoviePoster[]> {
        private final String LOG_TAG =ExtractMovieTask.class.getSimpleName();
            private MoviePoster[] getMovieDataFromJSON(String jsonMovieString)throws JSONException {
                final String results="results";
                final String id="id";
                final String poster_path="poster_path";

                        JSONObject J1 = new JSONObject(jsonMovieString);
                        JSONArray J2 = J1.getJSONArray(results);
                        MoviePoster[] RESULT = new MoviePoster[J2.length()];
                        for (int i = 0; i < RESULT.length; ++i) {
                            J1 = J2.getJSONObject(i);

                            Long movie_id = Long.valueOf(J1.getString(id));
                            String img_link = J1.getString(poster_path);
                            img_link.replace('\\', '/');
                            MoviePoster temp = new MoviePoster(img_link, movie_id);
                            RESULT[i] = temp;

                        }

                        return RESULT;
            }

        protected MoviePoster[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonMovieString = null;
            try {
                final String base_url = "https://api.themoviedb.org/3/movie/"+params[0]+"?";
                final String Api_key_param = "api_key";
                final String apikey = BuildConfig.MovieDB_Api_Key;
                Uri builturi = Uri.parse(base_url).buildUpon()
                        .appendQueryParameter(Api_key_param, apikey).build();
                Log.e(LOG_TAG,builturi.toString());
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
                jsonMovieString = buffer.toString();

            } catch (IOException e) {
                Log.w(LOG_TAG, "Error ", e);
                return null;
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
            try {
                return getMovieDataFromJSON(jsonMovieString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
            @Override
            protected  void onPostExecute(MoviePoster[] result){
                if(result!=null){
                    movieAdapter.clear();
                    for(MoviePoster M1 : result) {
                        movieAdapter.add(M1);
                    }
                }
            }
    }

}
