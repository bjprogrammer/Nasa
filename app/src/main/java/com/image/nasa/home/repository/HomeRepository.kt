package com.image.nasa.home.repository

import com.image.nasa.model.Response
import com.image.nasa.network.NetworkAPI.client
import com.image.nasa.network.NetworkService
import com.image.nasa.utils.Constants
import io.reactivex.Observable

class HomeRepository {
    private val networkService: NetworkService = client!!.create(NetworkService::class.java)

    val currentDayData: Observable<Response>
        get() = networkService.getData(Constants.API_KEY)

    fun getDataByDate(date: String): Observable<Response> {
        return networkService.getDataByDate(Constants.API_KEY, date)
    }
}