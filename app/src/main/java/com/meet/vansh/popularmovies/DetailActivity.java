package com.meet.vansh.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_movie_details, new Details_Menu())
                   .commit();

        }

    }
}
