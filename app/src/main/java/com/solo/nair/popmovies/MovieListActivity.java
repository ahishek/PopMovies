package com.solo.nair.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.solo.nair.popmovies.activities.MovieInfoActivity;
import com.solo.nair.popmovies.adapters.EndlessRecyclerViewScrollListener;
import com.solo.nair.popmovies.adapters.MovieListAdapter;
import com.solo.nair.popmovies.network.JsonParamRequest;
import com.solo.nair.popmovies.utils.MovieListObject;
import com.solo.nair.popmovies.utils.Utils;

import xyz.hanks.library.SmallBang;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener{

    public final int NUMBER_OF_GRIDS = 3;

    private RecyclerView mRecyclerView;
    private MovieListAdapter mAdapter;
    private SimpleImageLoader mImageLoader;
    private View mLoadingView;
    private int mSortByValue;
    private final String MOST_POPULAR = "popularity.desc";
    private final String USER_RATING = "vote_average.desc";
    private Spinner mSortBySpinner;
    private int pageCount;
    private View mFooterLoadingView;
    private SmallBang mSmallBang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initToolbar();
        initControls();
        fetchMovieListFromApi(false);
    }

    private void initControls() {
        mSmallBang = SmallBang.attach2Window(this);
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
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.movieDB_title));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void fetchMovieListFromApi(final boolean isLoadMore) {
        if (!isLoadMore)
            mLoadingView.setVisibility(View.VISIBLE);
//        else
//            mFooterLoadingView.setVisibility(View.VISIBLE);
        JsonParamRequest<MovieListObject> request = new JsonParamRequest<MovieListObject>(Request.Method.GET,
                Utils.BASE_API_URL + Utils.MOVIES_LIST_ENDPOINT, MovieListObject.class,
                getParams(isLoadMore), new Response.Listener<MovieListObject>() {

            @Override
            public void onResponse(MovieListObject response) {
                if (!isLoadMore) {
                    mLoadingView.setVisibility(View.GONE);
                    pageCount = response.getPage();
                }
//                else
//                    mFooterLoadingView.setVisibility(View.VISIBLE);
                setRecylerViewData(response, isLoadMore);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
}
