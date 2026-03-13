package edu.nd.pmcburne.hwapp.one.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// Check whether device has active internet connection
// Called before every network request
class ConnectivityObserver(private val context: Context) {

    fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}