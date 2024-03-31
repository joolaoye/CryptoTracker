package com.example.cryptotracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class SearchActivity : AppCompatActivity() {
    private lateinit var cryptoList : MutableList<Crypto>
    private lateinit var rvCrypto: RecyclerView

    private val apiKey = "CG-7EGf9bH9nooFSJr3s6uPjRnC"
    private lateinit var queryView : EditText
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        cryptoList = mutableListOf()

        rvCrypto = findViewById(R.id.rV1)

        queryView = findViewById(R.id.query)

        queryView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Get the text from the EditText
                query = queryView.text.toString().lowercase()

                getCoin(query)

                return@setOnEditorActionListener true
            }
            false
        }


        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getCoin(id : String) {
        val client = AsyncHttpClient()

        client["https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=$id&x_cg_demo_api_key=$apiKey", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("response", "${json.jsonArray}")

                val cryptoArray = json.jsonArray

                cryptoList = mutableListOf()

                for (i in 0 until cryptoArray.length()) {
                    val cryptoObject = cryptoArray.getJSONObject(i)

                    val id = cryptoObject.getString("id")

                    val name = cryptoObject.getString("name")

                    val symbol = cryptoObject.getString("symbol").uppercase()

                    val price = cryptoObject.getString("current_price").toDouble()

                    val temp = cryptoObject.getString("price_change_percentage_24h")
                    val percent_change_24h = String.format("%.2f", temp.toDouble()).toDouble()

                    val price_change_24h = cryptoObject.getString("price_change_24h").toDouble()

                    val trading_volume = cryptoObject.getString("total_volume")

                    val imageUrl = cryptoObject.getString("image")

                    val market_cap = cryptoObject.getString("market_cap")

                    val market_cap_rank = cryptoObject.getString("market_cap_rank").toInt()

                    val fully_diluted_valuation = cryptoObject.getString("fully_diluted_valuation") ?: "null"

                    val circulating_supply = cryptoObject.getString("circulating_supply")

                    val total_supply = cryptoObject.getString("total_supply") ?: "null"

                    val max_supply = cryptoObject.getString("max_supply") ?: "null"


                    cryptoList.add(Crypto(
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

                Log.d("test", cryptoList.toString())

                val adapter = CryptoAdapter(cryptoList, this@SearchActivity)
                rvCrypto.adapter = adapter
                rvCrypto.layoutManager = LinearLayoutManager(this@SearchActivity)
                rvCrypto.addItemDecoration(DividerItemDecoration(this@SearchActivity, LinearLayoutManager.VERTICAL))
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
}