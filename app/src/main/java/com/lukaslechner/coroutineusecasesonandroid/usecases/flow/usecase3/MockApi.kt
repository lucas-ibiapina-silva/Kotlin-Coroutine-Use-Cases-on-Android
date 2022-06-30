package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import com.google.gson.Gson
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.createFlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.fakeCurrentAlphabetStockPrice
import com.lukaslechner.coroutineusecasesonandroid.utils.MockNetworkInterceptor

fun mockApi() =
    createFlowMockApi(
        MockNetworkInterceptor()
            .mock(
                path = "http://localhost/current-alphabet-stock-price",
                body = { Gson().toJson(fakeCurrentAlphabetStockPrice()) },
                status = 200,
                delayInMs = 2000,
            )
    )