package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database.StockDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database.mapToEntity
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database.mapToUiModelList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowUseCase2ViewModel(
    private val mockApi: FlowMockApi = mockApi(),
    private val database: StockDao
) : BaseViewModel<UiState>() {

    val currentStockPrice = database.stockPrices().map { stockEntityList ->
        stockEntityList.mapToUiModelList()
    }.map { stockList ->
        UiState.Success(stockList)
    }.stateIn(
        initialValue = UiState.Loading,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    init {
        viewModelScope.launch {
            database.clear()
            while (true) {
                val currentStockPrice = mockApi.getCurrentAlphabetStockPrice()
                Timber.d("Fetched new stock price: ${currentStockPrice.currentPriceUsd}$")
                database.insert(currentStockPrice.mapToEntity())
                delay(2000)
            }
        }
    }
}