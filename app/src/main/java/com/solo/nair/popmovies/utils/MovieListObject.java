package com.solo.nair.popmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by abhisheknair on 19/06/16.
 */
public class MovieListObject implements Parcelable{

    @SerializedName("page")
    private int page = 0;
    @SerializedName("results")
    private ArrayList<Result> results = new ArrayList<>();

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @SerializedName("total_results")
    private int totalResults = 0;

    protected MovieListObject(Parcel in) {
        page = in.readInt();
        results = in.createTypedArrayList(Result.CREATOR);
        totalResults = in.readInt();
    }

    public static final Creator<MovieListObject> CREATOR = new Creator<MovieListObject>() {
        @Override
        public MovieListObject createFromParcel(Parcel in) {
            return new MovieListObject(in);
        }

        @Override
        public MovieListObject[] newArray(int size) {
            return new MovieListObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeTypedList(results);
        dest.writeInt(totalResults);
    }


    public static class Result implements Parcelable{
        @SerializedName("poster_path")
        private String posterPath = "";
        @SerializedName("adult")
        private boolean isAdult = false;
        @SerializedName("overview")
        private String plotOverview = "";
        @SerializedName("release_date")
        private String releaseDate = "";
        @SerializedName("id")
        private int id = 0;
        @SerializedName("original_title")
        private String originalTitle = "";
        @SerializedName("original_language")
        private String originalLanguage = "";
        @SerializedName("title")
        private String title = "";
        @SerializedName("backdrop_path")
        private String backdropPath = "";
        @SerializedName("popularity")
        private float popularity = 0.0f;
        @SerializedName("vote_average")
        private float voteAverage = 0.0f;
        @SerializedName("vote_count")
        private int voteCount = 0;

        protected Result(Parcel in) {
            posterPath = in.readString();
            isAdult = in.readByte() != 0;
            plotOverview = in.readString();
            releaseDate = in.readString();
            id = in.readInt();
            originalTitle = in.readString();
            originalLanguage = in.readString();
            title = in.readString();
            backdropPath = in.readString();
            popularity = in.readFloat();
            voteAverage = in.readFloat();
            voteCount = in.readInt();
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public boolean isAdult() {
            return isAdult;
        }

        public void setAdult(boolean adult) {
            isAdult = adult;
        }

        public String getPlotOverview() {
            return plotOverview;
        }

        public void setPlotOverview(String plotOverview) {
            this.plotOverview = plotOverview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public String getOriginalLanguage() {
            return originalLanguage;
        }

        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public float getPopularity() {
            return popularity;
        }

        public void setPopularity(float popularity) {
            this.popularity = popularity;
        }

        public float getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(float voteAverage) {
            this.voteAverage = voteAverage;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(posterPath);
            dest.writeByte((byte) (isAdult ? 1 : 0));
            dest.writeString(plotOverview);
            dest.writeString(releaseDate);
            dest.writeInt(id);
            dest.writeString(originalTitle);
            dest.writeString(originalLanguage);
            dest.writeString(title);
            dest.writeString(backdropPath);
            dest.writeFloat(popularity);
            dest.writeFloat(voteAverage);
            dest.writeInt(voteCount);
        }
    }
}
