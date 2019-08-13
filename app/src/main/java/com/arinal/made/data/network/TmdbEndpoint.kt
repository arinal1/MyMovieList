package com.arinal.made.data.network

import com.arinal.made.data.model.DetailModel
import com.arinal.made.data.model.MovieModel
import com.arinal.made.data.model.TvModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbEndpoint {

    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<MovieModel>

    @GET("discover/tv")
    fun getTvShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<TvModel>

    @GET("movie/{id}")
    fun getDetailMovie(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Single<DetailModel>

    @GET("tv/{id}")
    fun getDetailTvShow(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Single<DetailModel>
}