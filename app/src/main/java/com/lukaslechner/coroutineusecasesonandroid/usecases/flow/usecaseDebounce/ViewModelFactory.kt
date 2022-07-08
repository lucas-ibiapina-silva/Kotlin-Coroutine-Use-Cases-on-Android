package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao
import kotlinx.coroutines.CoroutineDispatcher

class ViewModelFactory(
    private val api: FlowMockApi,
    private val database: CryptoCurrencyDao,
    private val networkStatusProvider: NetworkStatusProvider,
    private val dispatcher: CoroutineDispatcher
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            FlowMockApi::class.java,
            CryptoCurrencyDao::class.java,
            NetworkStatusProvider::class.java,
            CoroutineDispatcher::class.java
        ).newInstance(api, database, networkStatusProvider, dispatcher)
    }
}