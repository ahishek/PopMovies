package com.solo.nair.popmovies.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.cache.plus.SimpleImageLoader;
import com.solo.nair.popmovies.R;
import com.solo.nair.popmovies.utils.MovieListObject;
import com.solo.nair.popmovies.utils.Utils;

public class MovieInfoActivity extends AppCompatActivity {

    private MovieListObject.Result mMovieObject;
    private ImageView mThumbnailImage;
    private ImageView mBackDropImage;
    private TextView mName;
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mOverview;
    private SimpleImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Bundle args = getIntent().getExtras();
        if (args != null)
            mMovieObject = args.getParcelable(Utils.BUNDLE_MOVIE_OBJECT);
        initToolbar();
        initControls();
        setData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open Share Bottomsheet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setData() {
        if (mMovieObject != null) {
            mName.setText(mMovieObject.getOriginalTitle());
            mReleaseDate.setText(mMovieObject.getReleaseDate());
            mUserRating.setText(getString(R.string.rating_string, String.valueOf(mMovieObject.getVoteAverage())));
            mImageLoader.get(Utils.getPosterUrl(mMovieObject.getPosterPath(), false), mThumbnailImage,
                    0);
            mImageLoader.get(Utils.getPosterUrl(mMovieObject.getBackdropPath(), true), mBackDropImage,
                    0);
            mOverview.setText(mMovieObject.getPlotOverview());
        }
    }

    private void initControls() {
        mImageLoader = new SimpleImageLoader(this);
        mBackDropImage = (ImageView) findViewById(R.id.back_drop_image);
        mThumbnailImage = (ImageView) findViewById(R.id.movie_image_thumbnail_iv);
        mName = (TextView) findViewById(R.id.movie_title_tv);
        mReleaseDate = (TextView) findViewById(R.id.movie_release_date_tv);
        mUserRating = (TextView) findViewById(R.id.movie_rating_tv);
        mOverview = (TextView) findViewById(R.id.movie_overview_tv);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mMovieObject != null)
            toolbar.setTitle(mMovieObject.getOriginalTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
