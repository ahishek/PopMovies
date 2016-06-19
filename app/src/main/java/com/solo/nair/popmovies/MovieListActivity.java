package com.solo.nair.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
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
import com.solo.nair.popmovies.adapters.MovieListAdapter;
import com.solo.nair.popmovies.network.JsonParamRequest;
import com.solo.nair.popmovies.utils.MovieListObject;
import com.solo.nair.popmovies.utils.Utils;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.OnItemClickListener, AdapterView.OnItemClickListener{

    private RecyclerView mRecylerView;
    private MovieListAdapter mAdapter;
    private SimpleImageLoader mImageLoader;
    private View mLoadingView;
    private int mSortByValue;
    private final String MOST_POPULAR = "popularity.desc";
    private final String USER_RATING = "vote_average.desc";
    private Spinner mSortBySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initToolbar();
        initControls();
        fetchMovieListFromApi();
    }

    private void initControls() {
        mSortBySpinner = (Spinner) findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortBySpinner.setAdapter(adapter);
        mSortBySpinner.setOnItemClickListener(this);
        mImageLoader = new SimpleImageLoader(this);
        mRecylerView = (RecyclerView) findViewById(R.id.movies_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRecylerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MovieListAdapter(this, mImageLoader);
        mAdapter.setOnItemClickListener(this);
        mRecylerView.setAdapter(mAdapter);
        mSortByValue = Utils.SortOrder.MOST_POPULAR;
        mLoadingView = findViewById(R.id.loading_view);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fetchMovieListFromApi() {
        mLoadingView.setVisibility(View.VISIBLE);
        JsonParamRequest<MovieListObject> request = new JsonParamRequest<MovieListObject>(Request.Method.GET,
                Utils.BASE_API_URL + Utils.MOVIES_LIST_ENDPOINT, MovieListObject.class,
                getParams(), new Response.Listener<MovieListObject>() {

            @Override
            public void onResponse(MovieListObject response) {
                mLoadingView.setVisibility(View.GONE);
                setRecylerViewData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void setRecylerViewData(MovieListObject response) {
        mAdapter.setData(response.getResults(), false);
    }

    public ArrayMap<String, String> getParams() {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("api_key", Utils.API_KEY);
        switch (mSortByValue) {
            case Utils.SortOrder.MOST_POPULAR:
                params.put("sort_by", MOST_POPULAR);
                break;

            case Utils.SortOrder.USER_RATING:
                params.put("sort_by", USER_RATING);
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String itemText = String.valueOf(parent.getItemAtPosition(position));
        String[] array = getResources().getStringArray(R.array.sort_by);
        if (array.length > 2) {
            if (itemText.equals(array[0]))
                mSortByValue = Utils.SortOrder.MOST_POPULAR;
            else if (itemText.equals(array[1]))
                mSortByValue = Utils.SortOrder.USER_RATING;
        }
        fetchMovieListFromApi();
    }
}
