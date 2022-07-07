package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao

class ViewModelFactory(
    private val api: FlowMockApi,
    private val database: CryptoCurrencyDao,
    private val networkStatusProvider: NetworkStatusProvider
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            FlowMockApi::class.java,
            CryptoCurrencyDao::class.java,
            NetworkStatusProvider::class.java
        ).newInstance(api, database, networkStatusProvider)
    }
}