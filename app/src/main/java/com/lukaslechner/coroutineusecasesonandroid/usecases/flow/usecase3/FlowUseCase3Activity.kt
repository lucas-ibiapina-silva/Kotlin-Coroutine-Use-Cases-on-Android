package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase3Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.helper.initChart
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.helper.style
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database.StockDatabase
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible
import kotlinx.coroutines.launch

class FlowUseCase3Activity : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: FlowUseCase3ViewModel by viewModels {
        ViewModelFactory(
            mockApi(),
            StockDatabase.getInstance(applicationContext).stockDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.chart.initChart(this)

        viewModel.startStockPricePolling()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentGoogleStockPrice.collect { uiState ->
                    render(uiState)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                binding.chart.setGone()
            }
            is UiState.Success -> {
                binding.progressBar.setGone()
                binding.chart.setVisible()
                updateChart(uiState.stockList)
            }
        }
    }

    private fun updateChart(stockList: List<Stock>) {

        val entries: ArrayList<Entry> = ArrayList()
        stockList.forEachIndexed { index, googleStock ->
            entries.add(
                Entry((index + 1).toFloat(), googleStock.currentPriceUsd)
            )
        }

        val data = LineDataSet(entries, "Google Stock Price").apply {
            this.style(this@FlowUseCase3Activity)
        }

        val lineData = LineData(data)
        binding.chart.data = lineData
        binding.chart.invalidate()
    }

    override fun getToolbarTitle() = flowUseCase3Description
}