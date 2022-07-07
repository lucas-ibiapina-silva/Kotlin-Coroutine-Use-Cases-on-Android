package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class NetworkStatusProvider(val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow {

        val networkStateCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
                Timber.d("Network onUnavailable()")
            }

            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
                Timber.d("Network onAvailable()")
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Unavailable)
                Timber.d("Network onLost()")
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkStateCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStateCallback)
        }
    }

    sealed class NetworkStatus {
        object Available : NetworkStatus()
        object Unavailable : NetworkStatus()
    }
}