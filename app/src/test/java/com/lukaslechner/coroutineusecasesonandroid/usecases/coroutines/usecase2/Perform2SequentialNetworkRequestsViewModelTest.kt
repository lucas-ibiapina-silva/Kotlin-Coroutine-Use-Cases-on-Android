package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1.FakeErrorApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1.PerformSingleNetworkRequestViewModel

import com.lukaslechner.coroutineusecasesonandroid.usecases.utils.MainCoroutineScopeRule
import org.junit.Assert
import org.junit.Rule

import org.junit.rules.TestRule

class Perform2SequentialNetworkRequestsViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @org.junit.Test
    fun `should return Success when both network requests are successful`() {

        // Arrange
        val fakeApi = FakeApiSuccess()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(
            fakeApi
        )

        observeViewModel(viewModel)

        // Act
        viewModel.perform2SequentialNetworkRequest()


        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockVersionFeaturesAndroid10)
            ),
            receivedUiStates
        )
    }

    @org.junit.Test
    fun `Should return Error when first network request fails`() {
        // Arrange
        val fakeApi = FakeVersionErrorApi()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(
            fakeApi
        )

        observeViewModel(viewModel)

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Network request failed!")
            ),
            receivedUiStates
        )
    }

    private fun observeViewModel(viewModel: Perform2SequentialNetworkRequestsViewModel) {
        viewModel.uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }
        }
    }
}