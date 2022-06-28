package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class FlowUseCase1ViewModel(
    dataSource: GoogleStockPriceDataSource = GoogleStockPriceDataSource()
) : BaseViewModel<UiState>() {

    val currentGoogleStockPriceAsLiveData: LiveData<UiState> = dataSource.latestPrice
        .map { stockList ->
            UiState.Success(stockList)
        }.onEach {
            Timber.d("New value collected when using .asLiveData()")
        }.asLiveData()
}