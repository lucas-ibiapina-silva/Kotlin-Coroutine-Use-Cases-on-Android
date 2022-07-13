package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import com.google.gson.Gson
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.allCryptoCurrenciesWithRandomPrice
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.createFlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.fakeCurrentCurrencyRate
import com.lukaslechner.coroutineusecasesonandroid.utils.MockNetworkInterceptor

fun mockApi() =
    createFlowMockApi(
        MockNetworkInterceptor()
            /*.mock(
                path = "/current-stock-prices",
                body = { request -> Gson().toJson(filterStocks(request)) },
                status = 200,
                delayInMs = 2000,
            )*/
            .mock(
                path = "/current-crypto-currency-prices",
                body = { Gson().toJson(allCryptoCurrenciesWithRandomPrice) },
                status = 200,
                delayInMs = 2000
            )
            .mock(
                path = "/current-currency-rate",
                body = { Gson().toJson(fakeCurrentCurrencyRate()) },
                status = 200,
                delayInMs = 1000,
            )
    )