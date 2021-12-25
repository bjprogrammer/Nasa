package com.image.nasa.network

import retrofit2.HttpException
import java.io.IOException

class NetworkError(val error: Throwable?) : Throwable(error) {
    companion object {
        const val DEFAULT_ERROR_MESSAGE = "Something went wrong!"
        const val NETWORK_ERROR_MESSAGE = "Network Issue! Try after sometime"
    }

    override val message: String
        get() = error!!.message!!

    val appErrorMessage: String
        get() {
            if (error is IOException) return NETWORK_ERROR_MESSAGE
            if (error !is HttpException) return DEFAULT_ERROR_MESSAGE
            val response = (error as HttpException?)!!.response()
            if (response != null) {
                if (response.code() == 500) {
                    return "Server issue! Contact admin"
                } else if (response.code() == 401) {
                    return "Auth token expired. Login again"
                } else if (response.code() == 550) {
                    return "Auth token does not match. Login again"
                } else if (response.code() == 502) {
                    return "Invalid login credentials"
                } else if (response.code() == 404) {
                    return "Record not found"
                }
            }
            return DEFAULT_ERROR_MESSAGE
        }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as NetworkError
        return if (error != null) error == that.error else that.error == null
    }

    override fun hashCode(): Int {
        return error?.hashCode() ?: 0
    }
}