package com.solo.nair.popmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
    private View mOverViewLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        if (savedInstanceState != null) {
            mMovieObject = savedInstanceState.getParcelable(Utils.BUNDLE_MOVIE_OBJECT);
        } else {
            Bundle args = getIntent().getExtras();
            if (args != null)
                mMovieObject = args.getParcelable(Utils.BUNDLE_MOVIE_OBJECT);
        }
        initToolbar();
        initControls();
        setData();
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
            if (!TextUtils.isEmpty(mMovieObject.getPlotOverview()))
                mOverview.setText(mMovieObject.getPlotOverview());
            else
                mOverViewLl.setVisibility(View.GONE);
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
        mOverViewLl = findViewById(R.id.overview_ll);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendShareIntent();
                }
            });
    }

    private void sendShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, mMovieObject.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, mMovieObject.getPlotOverview());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (mMovieObject != null)
                toolbar.setTitle(mMovieObject.getOriginalTitle());
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Utils.BUNDLE_MOVIE_OBJECT, mMovieObject);
    }
}
