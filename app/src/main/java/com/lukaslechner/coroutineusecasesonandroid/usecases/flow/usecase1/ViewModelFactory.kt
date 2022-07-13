package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi

class ViewModelFactory(private val mockApi: FlowMockApi) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(FlowMockApi::class.java)
            .newInstance(mockApi)
    }
}