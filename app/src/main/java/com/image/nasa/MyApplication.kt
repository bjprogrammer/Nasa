package com.image.nasa

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        context = this
    }
}