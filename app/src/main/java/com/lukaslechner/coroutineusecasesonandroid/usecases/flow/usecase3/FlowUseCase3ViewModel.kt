package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database.StockDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database.mapToEntity
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database.mapToUiModelList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowUseCase3ViewModel(
    private val mockApi: FlowMockApi = mockApi(),
    private val database: StockDao
) : BaseViewModel<UiState>() {

    val currentGoogleStockPrice = MutableLiveData<UiState>(UiState.Loading)

    val currentGoogleStockPriceAsLiveData = database.googleStockPrices()
        .map { stockEntityList ->
            stockEntityList.mapToUiModelList()
        }
        .map { stockList ->
            UiState.Success(stockList)
        }.onEach {
            Timber.d("New value collected when using .asLiveData()")
        }.asLiveData()

    val currentGoogleStockPriceAsFlow = database.googleStockPrices()
        .map { stockEntityList ->
            stockEntityList.mapToUiModelList()
        }
        .map { stockList ->
            UiState.Success(stockList)
        }.onEach {
            Timber.d("New value collected when using pure flow")
        }


    init {
        viewModelScope.launch {
            database.googleStockPrices().map { stockEntityList ->
                stockEntityList.mapToUiModelList()
            }.collect { stockList ->
                Timber.d("New value collected")
                currentGoogleStockPrice.value = UiState.Success(stockList)
            }
        }
    }

    fun startStockPricePolling() {
        viewModelScope.launch {
            database.clear()
            while (true) {
                val currentGoogleStockPrice = mockApi.getCurrentGoogleStockPrice()
                Timber.d("Fetched new stock price: ${currentGoogleStockPrice.currentPriceUsd}$")
                database.insert(currentGoogleStockPrice.mapToEntity())
                delay(2000)
            }
        }
    }
}