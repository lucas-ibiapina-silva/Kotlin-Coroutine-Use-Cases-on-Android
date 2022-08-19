package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val stockPriceRepository: StockPriceRepositoryWithMutableSharedFlow) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(StockPriceRepositoryWithMutableSharedFlow::class.java)
            .newInstance(stockPriceRepository)
    }
}