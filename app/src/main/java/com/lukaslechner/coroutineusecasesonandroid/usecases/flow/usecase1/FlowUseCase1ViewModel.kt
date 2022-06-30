package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

class FlowUseCase1ViewModel(
    dataSource: StockPriceDataSource = StockPriceDataSource()
) : BaseViewModel<UiState>() {

    val currentGoogleStockPriceAsLiveData: LiveData<UiState> = dataSource.latestPrice
        .map{ stock ->
            UiState.Success(stock) as UiState
        }.onEach {
            Timber.d("New value collected")
        }.onStart {
            emit(UiState.Loading)
        }.asLiveData()

}