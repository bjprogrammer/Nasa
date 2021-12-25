package com.image.nasa.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.image.nasa.BR

class Response : BaseObservable() {
    private lateinit var copyright: String
    private lateinit var date: String
    private lateinit var explanation: String
    private lateinit var title: String

    @SerializedName("media_type")
    private lateinit var type: String

    @SerializedName("hdurl")
    private var url: String?=null

    @SerializedName("url")
    private var videoUrl: String? = null

    @Bindable
    fun getCopyright(): String {
        return copyright
    }

    fun setCopyright(copyright: String) {
        this.copyright = copyright
        notifyPropertyChanged(BR.copyright)
    }

    @Bindable
    fun getDate(): String {
        return date
    }

    fun setDate(date: String) {
        this.date = date
        notifyPropertyChanged(BR.date)
    }

    @Bindable
    fun getExplanation(): String {
        return explanation
    }

    fun setExplanation(explanation: String) {
        this.explanation = explanation
        notifyPropertyChanged(BR.explanation)
    }

    @Bindable
    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
        notifyPropertyChanged(BR.title)
    }

    @Bindable
    fun getType(): String {
        return type
    }

    fun setType(type: String) {
        this.type = type
        notifyPropertyChanged(BR.type)
    }

    @Bindable
    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
        notifyPropertyChanged(BR.url)
    }

    @Bindable
    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String?) {
        this.videoUrl = videoUrl
        notifyPropertyChanged(BR.videoUrl)
    }
}