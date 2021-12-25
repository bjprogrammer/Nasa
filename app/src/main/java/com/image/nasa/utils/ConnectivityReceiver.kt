package com.image.nasa.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

//Checks for internet connection
class ConnectivityReceiver : BroadcastReceiver() {
    companion object {
        @JvmField
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    override fun onReceive(context: Context, arg1: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork: NetworkInfo? = null
        if (cm != null) {
            activeNetwork = cm.activeNetworkInfo
        }

        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (connectivityReceiverListener != null)
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnected)
    }
}