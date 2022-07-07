package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.databinding.RecyclerviewItemCryptoCurrencyBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.CryptoCurrency
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.PriceTrend
import com.lukaslechner.coroutineusecasesonandroid.utils.setInvisible

class CryptoCurrencyAdapter(private val cryptoCurrencyList: List<CryptoCurrency>): RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecyclerviewItemCryptoCurrencyBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_crypto_currency, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding){
        val cryptoCurrency = cryptoCurrencyList[position]
        index.text = cryptoCurrencyList.indexOf(cryptoCurrency).toString()
        name.text = cryptoCurrency.name
        marketCap.text = cryptoCurrency.marketCap.toString()
        currentPrice.text = "$${cryptoCurrency.currentPriceUsd}"
        when (cryptoCurrency.priceTrend) {
            PriceTrend.UP -> priceTrendIcon.setImageResource(R.drawable.ic_baseline_trending_up_24)
            PriceTrend.DOWN -> priceTrendIcon.setImageResource(R.drawable.ic_baseline_trending_down_24)
            else -> priceTrendIcon.setInvisible()
        }
    }

    override fun getItemCount(): Int {
        return cryptoCurrencyList.size
    }

}