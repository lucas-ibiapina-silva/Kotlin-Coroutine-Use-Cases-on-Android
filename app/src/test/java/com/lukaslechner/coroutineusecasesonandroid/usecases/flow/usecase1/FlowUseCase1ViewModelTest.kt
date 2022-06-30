package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.utils.ReplaceMainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class FlowUseCase1ViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get: Rule
    val replaceMainDispatcherRule = ReplaceMainDispatcherRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @Test
    fun `should receive loading state and emitted items`() = runTest {
        val fakeDataSource = FakeStockPriceDataSource()
        val viewModel = FlowUseCase1ViewModel(fakeDataSource)
        observeViewModel(viewModel)

        assertEquals(
            listOf(
                UiState.Loading,
            ),
            receivedUiStates
        )

        advanceTimeBy(1000)
        runCurrent()

        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(Stock(currentPriceUsd = 1.0f)),
            ),
            receivedUiStates
        )

        advanceTimeBy(1000)
        runCurrent()

        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(Stock(currentPriceUsd = 1.0f)),
                UiState.Success(Stock(currentPriceUsd = 2.0f))
            ),
            receivedUiStates
        )

        advanceTimeBy(1000)
        runCurrent()

        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(Stock(currentPriceUsd = 1.0f)),
                UiState.Success(Stock(currentPriceUsd = 2.0f)),
                UiState.Success(Stock(currentPriceUsd = 3.0f))
            ),
            receivedUiStates
        )
    }

    private fun observeViewModel(viewModel: FlowUseCase1ViewModel) {
        viewModel.currentStockPriceAsLiveData.observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }
        }
    }
}