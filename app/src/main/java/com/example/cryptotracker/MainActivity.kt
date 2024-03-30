package com.example.cryptotracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    private lateinit var cryptoList : MutableList<Crypto>
    private lateinit var rvCrypto: RecyclerView
    private lateinit var footerLayout : ConstraintLayout
    private lateinit var showMoreButton : Button
    private var listSize = 50
    private var lastItemSeen = 0

    private val apiKey = "CG-7EGf9bH9nooFSJr3s6uPjRnC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar : Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("All Assets")
        toolbar.setTitleTextColor(getResources().getColor(R.color.white))
        setSupportActionBar(toolbar)

        footerLayout = findViewById(R.id.footer)
        footerLayout.visibility = View.GONE

        showMoreButton = findViewById(R.id.showMore)

        showMoreButton.setOnClickListener {
            lastItemSeen = listSize - 2
            listSize += 50
            getCoin(listSize)
            footerLayout.visibility = View.GONE
        }

        rvCrypto = findViewById(R.id.rV)

        rvCrypto.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState : Int) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    footerLayout.visibility = View.VISIBLE

                } else {
                    footerLayout.visibility = View.GONE
                }
            }
        })

        getCoin(listSize)
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun getCoin(listSize : Int) {
        val client = AsyncHttpClient()

        client["https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=$listSize?x_cg_demo_api_key=$apiKey", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("response", "success")

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


                    cryptoList.add(Crypto(
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
                    ))
                }

                val adapter = CryptoAdapter(cryptoList, this@MainActivity)
                rvCrypto.adapter = adapter
                rvCrypto.layoutManager = LinearLayoutManager(this@MainActivity)
                rvCrypto.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))

                rvCrypto.scrollToPosition(lastItemSeen)
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