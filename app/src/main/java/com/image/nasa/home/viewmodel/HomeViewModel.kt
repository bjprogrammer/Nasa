package com.image.nasa.home.viewmodel

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.image.nasa.R
import com.image.nasa.home.repository.HomeRepository
import com.image.nasa.model.Media
import com.image.nasa.model.Response
import com.image.nasa.network.NetworkError
import com.image.nasa.utils.MediaType
import com.image.nasa.utils.Resource
import com.image.nasa.utils.Resource.Companion.success
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel : ViewModel() {
    private val homeRepository: HomeRepository = HomeRepository()
    private lateinit var action: String
    private lateinit var url: String
    private var lastSelectedDate = ""
    private var liveData = MutableLiveData<Resource<Response>>()

    var drawableLiveData = MutableLiveData<Int>()
    var actionLiveData = MutableLiveData<Media>()
    var dateLiveData = MutableLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun getDataByDate(date: String?): MutableLiveData<Resource<Response>> {
        liveData = MutableLiveData()
        val responseObservable: Observable<Response>

        if (date != null) {
            lastSelectedDate = date
            responseObservable = homeRepository.getDataByDate(date)
        } else {
            responseObservable = if (lastSelectedDate.isEmpty()) {
                homeRepository.currentDayData
            } else {
                homeRepository.getDataByDate(lastSelectedDate)
            }
        }

        responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(response: Response) {
                        if (response.getType() == MediaType.image.toString()) {
                            action = MediaType.image.toString()
                            drawableLiveData.value = R.drawable.ic_photo
                            url = response.getUrl()!!
                        } else {
                            action = MediaType.video.toString()
                            drawableLiveData.value = R.drawable.ic_film
                            url = response.getVideoUrl()!!
                        }
                        liveData.value = success(response)
                    }

                    override fun onError(e: Throwable) {
                        liveData.value = error(NetworkError(e).appErrorMessage)
                    }

                    override fun onComplete() {}
                })

        return liveData
    }

    fun changeDate() {
        dateLiveData.value = true
    }

    fun performAction() {
        actionLiveData.value = Media(action, url)
    }

    fun observeUserAction(): LiveData<Media> {
            return actionLiveData
    }

    fun observeChangeDate(): LiveData<Boolean> {
        return dateLiveData
    }

    fun clearActions() {
        dateLiveData = MutableLiveData()
        actionLiveData = MutableLiveData()
    }
}