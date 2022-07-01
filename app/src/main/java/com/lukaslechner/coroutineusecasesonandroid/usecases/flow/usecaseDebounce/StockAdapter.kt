package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.databinding.RecyclerviewItemStockBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.StockListing

class StockAdapter(private val stockList: List<StockListing>): RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecyclerviewItemStockBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.companyName.text = stockList[position].name
        holder.binding.currentPrice.text = stockList[position].symbol
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

}