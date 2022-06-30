package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.lukaslechner.coroutineusecasesonandroid.R

fun LineChart.initChart(context: Context) {
    val entries: ArrayList<Entry> = ArrayList()
    val data = LineDataSet(entries, "Alphabet Stock Price")
    data.style(context)

    val lineData = LineData(data)

    this.data = lineData
    setDrawGridBackground(false)
    axisRight.isEnabled = false
    xAxis.isEnabled = false
    val description = Description()
    description.isEnabled = false
    this.description = description
    setMaxVisibleValueCount(0)

    axisLeft.apply {
        axisMinimum = 1900f
        axisMaximum = 2100f
        valueFormatter = DollarValueAxisFormatter()
    }
}

fun LineDataSet.style(context: Context) {
    val colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
    color = colorPrimary
    setCircleColor(colorPrimary)
    lineWidth = 2.5f
    circleRadius = 5f
}

class DollarValueAxisFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return "${value.toInt()} $"
    }
}

class EuroValueAxisFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return "${value.toInt()} $"
    }
}