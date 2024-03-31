package com.example.cryptotracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class DetailActivity : AppCompatActivity() {
    private lateinit var crypto_name_view : TextView
    private lateinit var crypto_price_view : TextView
    private lateinit var price_change_view : TextView

    private lateinit var live_chart : LineChart

    private lateinit var market_cap_value_view : TextView
    private lateinit var fully_diluted_val_view: TextView
    private lateinit var trading_vol_24h_value_view : TextView
    private lateinit var volume_value_view : TextView
    private lateinit var supply_value_view : TextView
    private lateinit var popularity_value_view : TextView
    private lateinit var total_supply_value_view : TextView
    private lateinit var max_supply_value_view : TextView

    private var percent_change : Double = 0.0

    private val apiKey = "CG-7EGf9bH9nooFSJr3s6uPjRnC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_view)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        crypto_name_view = findViewById(R.id.crypto_name)
        crypto_price_view = findViewById(R.id.crypto_price)
        price_change_view = findViewById(R.id.price_change)

        live_chart = findViewById(R.id.live_chart)

        market_cap_value_view = findViewById(R.id.market_cap_value)
        trading_vol_24h_value_view = findViewById(R.id.trading_vol_24h_value)
        fully_diluted_val_view = findViewById(R.id.volume_value)
        volume_value_view = findViewById(R.id.volume_value)
        supply_value_view = findViewById(R.id.supply_value)
        popularity_value_view = findViewById(R.id.popularity_value)
        total_supply_value_view = findViewById(R.id.total_supply_value)
        max_supply_value_view = findViewById(R.id.max_supply_value)

        val data = intent.getStringExtra("crypto") ?: "null"

        getCoin(data)

        fetchDataset(data)

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


                val current_crypto = parse_Json(cryptoArray)[0]

                percent_change = current_crypto.percent_change_24h

                updateUI(current_crypto)

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

    private fun fetchDataset(id: String) {
        val client = AsyncHttpClient()

        client["https://api.coingecko.com/api/v3/coins/$id/market_chart?vs_currency=usd&days=1&x_cg_demo_api_key=$apiKey", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val raw_dataset = json.jsonObject.getJSONArray("prices")
                var dataset : MutableList<Entry>

                dataset = mutableListOf()


                for (i in 0 until raw_dataset.length()) {
                    val temp = raw_dataset[i].toString()
                    val items = temp.substring(1, temp.length - 1).split(",")

                    val timestamp = items[0].toLong()
                    val price = items[1].toDouble()

                    dataset.add(Entry(timestamp.toFloat(), price.toFloat()))

                    plotChart(dataset = LineDataSet(dataset, "Sample Data"))
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

    private fun plotChart(dataset : LineDataSet) {
        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER)
        dataset.setCubicIntensity(0.2f)
        dataset.setDrawFilled(true)
        dataset.setDrawCircles(false)
        dataset.setDrawValues(false)
        dataset.setLineWidth(1.8f)
        dataset.setCircleRadius(4f)
        dataset.setCircleColor(Color.RED)
        dataset.setHighLightColor(Color.rgb(50, 53, 62))

        if (percent_change < 0) {
            dataset.setColor(Color.RED)
            dataset.setFillColor(Color.RED)
        }
        else {
            dataset.setColor(Color.GREEN)
            dataset.setFillColor(Color.GREEN)
        }
        dataset.setFillAlpha(30)
        dataset.setDrawHorizontalHighlightIndicator(false)

        val lineData = LineData(dataset)

        val xAxis = live_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        live_chart.axisLeft.isEnabled = false

        val rightAxis = live_chart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setLabelCount(8, true)
        rightAxis.setTextColor(Color.WHITE)
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        rightAxis.setDrawGridLines(true)
        rightAxis.gridColor = Color.rgb(50, 53, 62)
        rightAxis.gridLineWidth = 1f

        // Set data to the chart
        live_chart.data = lineData

        // Customize chart appearance
        live_chart.getDescription().setEnabled(false)
        live_chart.setDrawGridBackground(false)
        live_chart.setBackgroundColor( getResources().getColor(R.color.dark_theme, theme))

        val legend = live_chart.legend
        legend.isEnabled = false

        live_chart.invalidate()
    }

    private fun updateUI(current_crypto: Crypto) {
        crypto_name_view.text = "${current_crypto.name} price"
        crypto_price_view.text = "$${current_crypto.price}"
        price_change_view.text = "$${current_crypto.price_change_24h} (${Math.abs(current_crypto.percent_change_24h)}%)"

        market_cap_value_view.text = "$${current_crypto.market_cap}"
        fully_diluted_val_view.text = "$${current_crypto.fully_diluted_valuation}"
        trading_vol_24h_value_view.text = "$${current_crypto.total_volume}"
        supply_value_view.text = "${current_crypto.circulating_supply}"
        popularity_value_view.text = "#${current_crypto.market_cap_rank}"
        total_supply_value_view.text = "${current_crypto.total_supply}"
        max_supply_value_view.text = "${current_crypto.max_supply}"

        if (percent_change < 0) {
            price_change_view.setTextColor(Color.RED)
        }
        else {
            price_change_view.setTextColor(Color.GREEN)
        }
    }
}
