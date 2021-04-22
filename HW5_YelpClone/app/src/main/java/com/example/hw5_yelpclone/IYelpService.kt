package com.example.hw5_yelpclone

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface IYelpService {

    @GET("businesses/search")
    fun getSearchItems(@Header("Authorization") authHeader: String,
                       @Query("term")content:String,
                       @Query("location")location:String) : Call<SearchItem>

}