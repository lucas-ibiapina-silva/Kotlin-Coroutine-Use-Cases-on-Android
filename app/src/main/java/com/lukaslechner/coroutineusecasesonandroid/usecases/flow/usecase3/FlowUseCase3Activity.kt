package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase3Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.GoogleStock
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database.StockDatabase
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

        initChart()

        viewModel.startStockPricePolling()
        /*viewModel.currentGoogleStockPriceAsLiveData.observe(this) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        }*/

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentGoogleStockPriceAsFlow.collect { uiState ->
                    render(uiState)
                }
            }
        }
        viewModel.currentGoogleStockPriceAsFlow
    }

    private fun initChart() {
        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(0f, 2000f))

        val data = LineDataSet(entries, "Google Stock Price")
        data.style()

        val lineData = LineData(data)

        with(binding.googleStockChart) {
            this.data = lineData
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        binding.googleStockChart.axisLeft.apply {
            axisMinimum = 1990f
            axisMaximum = 2100f
        }
    }

    private fun LineDataSet.style() {
        val colorPrimary = ContextCompat.getColor(this@FlowUseCase3Activity, R.color.colorPrimary)
        color = colorPrimary
        setCircleColor(colorPrimary)
        lineWidth = 2.5f
        circleRadius = 1f
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Success -> {
                updateChart(uiState.googleStockList)
            }
        }
    }

    private fun updateChart(stockList: List<GoogleStock>) {

        val entries: ArrayList<Entry> = ArrayList()

        // initial data point
        entries.add(Entry(0f, 2000f))
        stockList.forEachIndexed { index, googleStock ->
            entries.add(
                Entry((index + 1).toFloat(), googleStock.currentPriceUsd)
            )
        }


        val data = LineDataSet(entries, "Google Stock Price").apply {
            style()
        }

        val lineData = LineData(data)
        binding.googleStockChart.data = lineData
        binding.googleStockChart.invalidate()
    }

    override fun getToolbarTitle() = flowUseCase3Description
}