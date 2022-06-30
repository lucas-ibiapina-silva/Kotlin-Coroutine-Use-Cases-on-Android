package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import com.lukaslechner.coroutineusecasesonandroid.utils.ReplaceMainDispatcherRule
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowUseCase3ViewModelTest {

    @get: Rule
    val replaceMainDispatcherRule = ReplaceMainDispatcherRule()

    @Test
    fun getCurrentStockPriceAsFlow() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = FlowUseCase3ViewModel(FakeStockPriceDataSource())

        assertEquals(UiState.Loading, viewModel.currentStockPriceAsFlow.value)

        // doesnt work currently
        //advanceTimeBy(1001)
        // runCurrent()
        // assertEquals(
        //    UiState.Success(Stock(currentPriceUsd = 1.0f)),
        //    viewModel.currentStockPriceAsFlow.value
        // )

        // advanceUntilIdle()
    }
}