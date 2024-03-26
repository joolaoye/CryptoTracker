package com.example.cryptotracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CryptoAdapter(var cryptos : List<Crypto>) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    inner class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView
        val symbolView: TextView
        val priceView: TextView
        val percentChangeView: TextView
        val logoView : ImageView

        init {
            nameView = itemView.findViewById(R.id.name)
            symbolView = itemView.findViewById(R.id.symbol)
            priceView = itemView.findViewById(R.id.price)
            percentChangeView = itemView.findViewById(R.id.percentChange)
            logoView = itemView.findViewById(R.id.logo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return CryptoViewHolder(view)
    }

    override fun getItemCount() = cryptos.size

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        holder.nameView.text = cryptos[position].name
        holder.symbolView.text = cryptos[position].symbol
        holder.priceView.text = "$${cryptos[position].price.toString()}"
        val percentChange = cryptos[position].percent_change_24h
        holder.percentChangeView.text = if (percentChange > 0) {
            "+${percentChange.toString()}%"
        }
        else {
            "${percentChange.toString()}%"
        }
        val textColor = if ( cryptos[position].percent_change_24h < 0) {
            R.color.red
        }
        else {
            R.color.green
        }
        holder.percentChangeView.setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))

        Glide.with(holder.itemView)
            .load(cryptos[position].imageURL)
            .centerCrop()
            .into(holder.logoView)
    }
}