package com.glowka.rafal.ai.chat.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkChecker(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    fun isNetworkAvailable(): Boolean {
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
