package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database.StockDao

class ViewModelFactory(private val api: FlowMockApi, private val database: StockDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(FlowMockApi::class.java, StockDao::class.java)
            .newInstance(api, database)
    }
}