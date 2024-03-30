package com.example.cryptotracker

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class DetailActivity : AppCompatActivity() {
    private lateinit var crypto_name_view : TextView
    private lateinit var crypto_price_view : TextView
    private lateinit var price_change_view : TextView

    private lateinit var market_cap_value_view : TextView
    private lateinit var trading_vol_24h_value_view : TextView
    private lateinit var volume_value_view : TextView
    private lateinit var supply_value_view : TextView
    private lateinit var popularity_value_view : TextView
    private lateinit var total_supply_value_view : TextView

    private val apiKey = "CG-7EGf9bH9nooFSJr3s6uPjRnC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_view)

        crypto_name_view = findViewById(R.id.crypto_name)
        crypto_price_view = findViewById(R.id.crypto_price)
        price_change_view = findViewById(R.id.price_change)

        market_cap_value_view = findViewById(R.id.market_cap_value)
        trading_vol_24h_value_view = findViewById(R.id.trading_vol_24h_value)
        volume_value_view = findViewById(R.id.volume_value)
        supply_value_view = findViewById(R.id.supply_value)
        popularity_value_view = findViewById(R.id.popularity_value)

        val data = intent.getStringExtra("crypto") ?: "null"

        getCoin(data)
    }

    private fun getCoin(id : String) {
        val client = AsyncHttpClient()

        client["https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=$id&x_cg_demo_api_key=$apiKey", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("response", "${json.jsonArray}")

                val cryptoArray = json.jsonArray


                for (i in 0 until cryptoArray.length()) {
                    val cryptoObject = cryptoArray.getJSONObject(i)

                    val id = cryptoObject.getString("id")

                    val name = cryptoObject.getString("name")

                    val symbol = cryptoObject.getString("symbol").uppercase()

                    val price = cryptoObject.getString("current_price").toDouble()

                    val temp = cryptoObject.getString("price_change_percentage_24h")
                    val percent_change_24h = String.format("%.2f", temp.toDouble()).toDouble()

                    val price_change_24h = cryptoObject.getString("price_change_24h").toDouble()

                    val imageUrl = cryptoObject.getString("image")

                    val market_cap = cryptoObject.getString("market_cap").toDouble()

                    val market_cap_rank = cryptoObject.getString("market_cap_rank").toInt()

                    val fully_diluted_valuation = try {
                        cryptoObject.getString("fully_diluted_valuation").toDouble()
                    } catch (e: NumberFormatException) {
                        Double.POSITIVE_INFINITY
                    }

                    val circulating_supply = cryptoObject.getString("circulating_supply").toDouble()

                    val total_supply = try {
                        cryptoObject.getString("total_supply").toDouble()
                    } catch (e: NumberFormatException) {
                        Double.POSITIVE_INFINITY
                    }

                    val max_supply: Double = try {
                        cryptoObject.getString("max_supply").toDouble()
                    } catch (e: NumberFormatException) {
                        Double.POSITIVE_INFINITY
                    }


                    val current_crypto = Crypto(
                        id,
                        name,
                        symbol,
                        price,
                        percent_change_24h,
                        price_change_24h,
                        imageUrl,
                        market_cap,
                        market_cap_rank,
                        fully_diluted_valuation,
                        circulating_supply,
                        total_supply,
                        max_supply
                    )

                    updateUI(current_crypto)

                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Error", errorResponse)
            }
        }]
    }

    private fun updateUI(current_crypto: Crypto) {
        crypto_name_view.text = "${current_crypto.name} price"
        crypto_price_view.text = "$${current_crypto.price}"
        price_change_view.text = "$${current_crypto.price_change_24h} (${current_crypto.percent_change_24h}%)"

        market_cap_value_view.text = "$${current_crypto.market_cap}"
        supply_value_view.text = "${current_crypto.circulating_supply}"
        supply_value_view.text = "${current_crypto.circulating_supply}"
        popularity_value_view.text = "#${current_crypto.market_cap_rank}"

    }
}
