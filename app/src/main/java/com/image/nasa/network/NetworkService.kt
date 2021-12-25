package com.image.nasa.network

import com.image.nasa.model.Response
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

//ALL API calls endpoints
interface NetworkService {
    @GET("planetary/apod")
    fun getData(@Query("api_key") key: String): Observable<Response>

    @GET("planetary/apod")
    fun getDataByDate(@Query("api_key") key: String, @Query("date") date: String): Observable<Response>
}