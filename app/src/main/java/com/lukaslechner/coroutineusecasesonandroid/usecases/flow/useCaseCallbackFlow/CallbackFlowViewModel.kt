package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseCallbackFlow

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber

class CallbackFlowViewModel(
    context: Context,
    dataSource: StockPriceDataSource = NetworkStockPriceDataSource()
) : ViewModel() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow<NetworkState> {

        val networkStateCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                trySend(NetworkState.Unavailable)
                Timber.d("Network onUnavailable()")
            }

            override fun onAvailable(network: Network) {
                trySend(NetworkState.Available)
                Timber.d("Network onAvailable()")
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.Unavailable)
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

    val networkStateFlow = MutableStateFlow<NetworkState>(NetworkState.Available)
    val networkStateChannel = Channel<NetworkState>(Channel.CONFLATED)

    // TODO: Convert to Channel? Behavior is fine (no toast on configuration changes), so is callbackflow fine here?
    // val networkStateChannelFlow = networkStatus

    val currentStockPriceAsFlow: StateFlow<UiState> = dataSource.latestPrice
        .map { stockList ->
            UiState.Success(stockList)
        }.onEach {
            Timber.d("New value collected")
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    sealed class NetworkState {
        object Available : NetworkState()
        object Unavailable : NetworkState()
    }
}