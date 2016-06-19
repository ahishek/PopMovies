package com.solo.nair.popmovies.utils;

/**
 * Created by abhisheknair on 19/06/16.
 */
public class Utils {

    public static final String BASE_API_URL = "https://api.themoviedb.org/3";
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p";
    public static final String API_KEY = "742b576db40a344bce1f824c41a771e8";

    public static final String MOVIES_LIST_ENDPOINT = "/discover/movie";
    public static final String IMAGE_SIZE_MOBILE = "/w185";
    public static final String IMAGE_SIZE_LARGE = "/w500";

    public static final String BUNDLE_MOVIE_OBJECT = "movie_object";

    public static class SortOrder {
        public static final int MOST_POPULAR = 1;
        public static final int USER_RATING = 2;
    }

    public static String getPosterUrl(String posterPath, boolean size) {
        if (size)
            return BASE_IMAGE_URL + IMAGE_SIZE_LARGE + posterPath;
        else
            return BASE_IMAGE_URL + IMAGE_SIZE_MOBILE + posterPath;
    }
}
