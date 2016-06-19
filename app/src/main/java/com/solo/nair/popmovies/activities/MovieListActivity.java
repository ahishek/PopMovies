package com.solo.nair.popmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.cache.plus.SimpleImageLoader;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.solo.nair.popmovies.R;
import com.solo.nair.popmovies.adapters.EndlessRecyclerViewScrollListener;
import com.solo.nair.popmovies.adapters.MovieListAdapter;
import com.solo.nair.popmovies.network.JsonParamRequest;
import com.solo.nair.popmovies.utils.MovieListObject;
import com.solo.nair.popmovies.utils.Utils;

import java.util.ArrayList;

import xyz.hanks.library.SmallBang;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.OnItemClickListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    public final int NUMBER_OF_GRIDS = 3;

    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private SimpleImageLoader mImageLoader;
    private View mLoadingView;
    private View mErrorView;
    private int mSortByValue;
    private final String MOST_POPULAR = "popularity.desc";
    private final String USER_RATING = "vote_average.desc";
    private Spinner mSortBySpinner;
    private int pageCount;
    private SmallBang mSmallBang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initToolbar();
        initControls();
        if (savedInstanceState != null) {
            ArrayList<MovieListObject.Result> data = savedInstanceState.getParcelableArrayList(Utils.BUNDLE_DATA);
            mAdapter.setData(data, true);
        } else {
            fetchMovieListFromApi(false);
        }
    }

    private void initControls() {
        mSmallBang = SmallBang.attach2Window(this);
        pageCount = 1;
        mSortBySpinner = (Spinner) findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortBySpinner.setAdapter(adapter);
        mSortBySpinner.setOnItemSelectedListener(this);
        mImageLoader = new SimpleImageLoader(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.movies_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUMBER_OF_GRIDS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchMovieListFromApi(true);
            }
        });
        mAdapter = new MovieListAdapter(this, mImageLoader, mSmallBang);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);
        mSortByValue = Utils.SortOrder.MOST_POPULAR;
        mLoadingView = findViewById(R.id.loading_view);
        mErrorView = findViewById(R.id.error_container);
        AppCompatButton retryBtn = (AppCompatButton) mErrorView.findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.movieDB_title));
            toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back_arrow));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void fetchMovieListFromApi(final boolean isLoadMore) {
        if (!isLoadMore)
            mLoadingView.setVisibility(View.VISIBLE);
        JsonParamRequest<MovieListObject> request = new JsonParamRequest<MovieListObject>(Request.Method.GET,
                Utils.BASE_API_URL + Utils.MOVIES_LIST_ENDPOINT, MovieListObject.class,
                getParams(isLoadMore), new Response.Listener<MovieListObject>() {

            @Override
            public void onResponse(MovieListObject response) {
                if (!isLoadMore)
                    mLoadingView.setVisibility(View.GONE);
                setRecylerViewData(response, isLoadMore);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mErrorView.setVisibility(View.VISIBLE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void setRecylerViewData(MovieListObject response, boolean isLoadMore) {
        mAdapter.setData(response.getResults(), isLoadMore);
    }

    public ArrayMap<String, String> getParams(boolean isLoadMore) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("api_key", Utils.API_KEY);
        switch (mSortByValue) {
            case Utils.SortOrder.MOST_POPULAR:
                params.put("sort_by", MOST_POPULAR);
                break;

            case Utils.SortOrder.USER_RATING:
                params.put("sort_by", USER_RATING);
        }
        if (isLoadMore)
            params.put("page", String.valueOf(pageCount++));
        return params;
    }

    @Override
    public void onItemClick(View view, MovieListObject.Result movieObject) {
        Intent intent = new Intent(this, MovieInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Utils.BUNDLE_MOVIE_OBJECT, movieObject);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemText = String.valueOf(parent.getItemAtPosition(position));
        String[] array = getResources().getStringArray(R.array.sort_by);
        if (array.length > 1) {
            if (itemText.equals(array[0]))
                mSortByValue = Utils.SortOrder.MOST_POPULAR;
            else if (itemText.equals(array[1]))
                mSortByValue = Utils.SortOrder.USER_RATING;
        }
        fetchMovieListFromApi(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                mImageLoader.stopProcessingQueue();
            } else {
                mImageLoader.startProcessingQueue();
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Utils.BUNDLE_DATA, mAdapter.getData());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retry:
                fetchMovieListFromApi(false);
                break;
        }
    }
}
