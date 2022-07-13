package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.databinding.RecyclerviewItemStockBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Currency
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock

class StockAdapter: RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    var stockList: List<Stock>? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecyclerviewItemStockBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding){
        val stock = stockList?.get(position) ?: return@with
        index.text = stock.rank.toString()
        name.text = stock.name
        val currencySymbol = when (stock.currency) {
            Currency.DOLLAR -> "$"
            Currency.EURO -> "€"
        }
        currentPrice.text = "$currencySymbol${stock.currentPrice}"
    }

    override fun getItemCount(): Int {
        return stockList?.size ?: 0
    }

}