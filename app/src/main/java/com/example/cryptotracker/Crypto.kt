package com.example.cryptotracker

data class Crypto (
    val id : String,
    val name : String,
    val symbol: String,
    val price: Double,
    val percent_change_24h : Double,
    val price_change_24h : Double,
    val imageURL: String,
    val market_cap: Double,
    val market_cap_rank: Int,
    val fully_diluted_valuation: Double,
    val circulating_supply: Double,
    val total_supply: Double,
    val max_supply: Double
    )
