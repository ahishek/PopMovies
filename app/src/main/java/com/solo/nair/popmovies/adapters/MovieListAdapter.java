package com.solo.nair.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.cache.plus.SimpleImageLoader;
import com.solo.nair.popmovies.R;
import com.solo.nair.popmovies.utils.MovieListObject;
import com.solo.nair.popmovies.utils.Utils;

import java.util.ArrayList;

/**
 * Created by abhisheknair on 19/06/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.PosterViewHolder> {

    private SimpleImageLoader mImageLoader;
    private Context mContext;
    private ArrayList<MovieListObject.Result> mMoviesList;
    private OnItemClickListener mItemClickListener;

    public MovieListAdapter(Context context, SimpleImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
        this.mContext = context;
        this.mMoviesList = new ArrayList<>();
    }

    public void setData(ArrayList<MovieListObject.Result> list, boolean fromLoadMore) {
        if (mMoviesList != null) {
            if (!fromLoadMore)
                mMoviesList.clear();
            mMoviesList.addAll(list);
            if (!fromLoadMore)
                notifyDataSetChanged();
            else
                notifyItemRangeInserted(getItemCount(), list.size());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, MovieListObject.Result movieObject);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_list, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        MovieListObject.Result movieObject = mMoviesList.get(position);
        holder.setMovieData(movieObject);
    }

    @Override
    public int getItemCount() {
        return (mMoviesList == null)? 0 : mMoviesList.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView title;
        private MovieListObject.Result movieObject;

        public PosterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.movie_photo);
            title = (TextView) itemView.findViewById(R.id.movie_name);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, movieObject);
            }
        }

        public void setMovieData(MovieListObject.Result result) {
            movieObject = result;
            mImageLoader.get(getPosterUrl(result.getPosterPath()), imageView,
                    0);
            title.setText(result.getOriginalTitle());
        }

        private String getPosterUrl(String posterPath) {
            return Utils.BASE_IMAGE_URL + Utils.IMAGE_SIZE_MOBILE + posterPath;
        }
    }
}
