package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val oreoFeatures = mockApi.getAndroidVersionFeatures(27)
                val pieFeatures = mockApi.getAndroidVersionFeatures(28)
                val android10Features = mockApi.getAndroidVersionFeatures(29)

                val versionsFetures = listOf(oreoFeatures, pieFeatures, android10Features)
                uiState.value = UiState.Success(versionsFetures)
            } catch (ex: Exception) {
                uiState.value = UiState.Error("Network Request failed!")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading
        val oreoFeaturesDeferred = viewModelScope.async {
             mockApi.getAndroidVersionFeatures(27)
        }
        val pieFeaturesDeferred = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(28)
        }
        val android10FeaturesDeferred = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(29)
        }

        viewModelScope.launch {
            try {
                //more o less good way
//                val oreoFeatures = oreoFeaturesDeferred.await()
//                val pieFeatures = pieFeaturesDeferred.await()
//                val android10Features = android10FeaturesDeferred.await()
                //better way
                val versionsFetures = awaitAll(oreoFeaturesDeferred, pieFeaturesDeferred, android10FeaturesDeferred)
                uiState.value = UiState.Success(versionsFetures)
            } catch (ex: Exception) {
                uiState.value = UiState.Error("Network Request failed!")
            }
        }

    }
}