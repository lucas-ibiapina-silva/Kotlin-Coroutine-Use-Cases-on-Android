package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseViewModelWithChannels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context, private val dataSource: StockPriceDataSource) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java, StockPriceDataSource::class.java)
            .newInstance(context, dataSource)
    }
}