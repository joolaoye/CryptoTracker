package com.example.cryptotracker

import org.json.JSONArray
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

fun format_prices(price : String) : String {
    val array = arrayOf("\u2081", "\u2082", "\u2083", "\u2084","\u2085", "\u2086", "\u2087", "\u2088", "\u2089")
    val priceBigDecimal = BigDecimal(price)
    var formatted_price = "0.0"

    if (priceBigDecimal < BigDecimal("0.00001")) {
        val exponent = priceBigDecimal.scale() - priceBigDecimal.precision()
        val numbersAfterZeroes = (priceBigDecimal * (10.toDouble().pow(priceBigDecimal.scale())).toBigDecimal()).toInt()
        formatted_price = "0.0${array[exponent - 1]}$numbersAfterZeroes"
    }

    else if (priceBigDecimal < BigDecimal("1.0") && price.length > 6) {
         formatted_price = String.format("%.6f", priceBigDecimal)
    }

    else {
        formatted_price = String.format("%.2f", priceBigDecimal)
        formatted_price = NumberFormat.getNumberInstance(Locale.getDefault()).format(BigDecimal(formatted_price))
    }

    return formatted_price

}

fun big_values(value : String) : String {

    if (value == "null") return "\u221E"

    return NumberFormat.getNumberInstance(Locale.getDefault()).format(BigDecimal(value))
}

fun parse_Json(json_array: JSONArray) :  MutableList<Crypto> {
    var cryptos = mutableListOf<Crypto>()

    for (i in 0 until json_array.length()) {
        val cryptoObject = json_array.getJSONObject(i)

        val id = cryptoObject.getString("id")

        val name = cryptoObject.getString("name")

        val symbol = cryptoObject.getString("symbol").uppercase()

        val price = format_prices(cryptoObject.getString("current_price"))

        var temp = cryptoObject.getString("price_change_percentage_24h")
        val percent_change_24h = String.format("%.2f", temp.toDouble()).toDouble()

        val price_change_24h = format_prices(Math.abs(cryptoObject.getString("price_change_24h").toDouble()).toString())

        temp = cryptoObject.getString("total_volume") ?: "null"
        val trading_volume = big_values(cryptoObject.getString("total_volume"))

        val imageUrl = cryptoObject.getString("image")

        val market_cap = big_values(cryptoObject.getString("market_cap"))

        val market_cap_rank = cryptoObject.getString("market_cap_rank").toInt()

        temp = cryptoObject.getString("fully_diluted_valuation") ?: "null"
        val fully_diluted_valuation = big_values(temp)

        val circulating_supply = cryptoObject.getString("circulating_supply")

        temp = cryptoObject.getString("total_supply") ?: "null"
        val total_supply = big_values(temp)

        temp = cryptoObject.getString("max_supply") ?: "null"
        val max_supply = big_values(temp)


        cryptos.add(Crypto(
            id,
            name,
            symbol,
            price,
            percent_change_24h,
            price_change_24h,
            trading_volume,
            imageUrl,
            market_cap,
            market_cap_rank,
            fully_diluted_valuation,
            circulating_supply,
            total_supply,
            max_supply
        ))
    }

    return cryptos
}