package com.example.cryptotracker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.math.BigDecimal

class CryptoAdapter(var cryptos : List<Crypto>, private val context: Context) : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    inner class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val row : CardView
        val nameView: TextView
        val symbolView: TextView
        val priceView: TextView
        val percentChangeView: TextView
        val logoView : ImageView

        init {
            row = itemView.findViewById(R.id.row)

            row.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                val position = adapterPosition
                val item = cryptos[position]
                intent.putExtra("crypto", item.id)
                context.startActivity(intent)
            }

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
        holder.percentChangeView.text = if (percentChange > BigDecimal("0.00001").toDouble()) {
            "+${percentChange}%"
        }
        else {
            "${percentChange}%"
        }
        val textColor = if ( cryptos[position].percent_change_24h < BigDecimal("0.00001").toDouble()) {
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