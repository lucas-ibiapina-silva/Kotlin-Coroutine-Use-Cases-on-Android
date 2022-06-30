package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseViewModelWithChannels

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber

class ViewModelWithChannels(
    context: Context,
    dataSource: StockPriceDataSource = NetworkStockPriceDataSource()
) : BaseViewModel<UiState>() {

    val networkStateFlow = MutableStateFlow<NetworkState>(NetworkState.Available)
    val networkStateChannel = Channel<NetworkState>(Channel.CONFLATED)
    val networkStateChannelFlow = networkStateChannel.receiveAsFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onUnavailable() {
            networkStateFlow.value = NetworkState.Unavailable
            networkStateChannel.trySend(NetworkState.Unavailable)
            Timber.d("Network onUnavailable()")
        }

        override fun onAvailable(network: Network) {
            networkStateFlow.value = NetworkState.Available
            networkStateChannel.trySend(NetworkState.Available)
            Timber.d("Network onAvailable()")
        }

        override fun onLost(network: Network) {
            networkStateFlow.value = NetworkState.Unavailable
            networkStateChannel.trySend(NetworkState.Unavailable)
            Timber.d("Network onLost()")
        }
    }

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    init {
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)
    }

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