package com.arinal.made.data.network

import com.arinal.made.utils.Constants.tmdbBaseUrl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(tmdbBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getTmdb(): TmdbEndpoint = retrofit.newBuilder().build().create(TmdbEndpoint::class.java)
}