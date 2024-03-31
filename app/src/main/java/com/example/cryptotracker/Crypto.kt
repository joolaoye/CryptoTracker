package com.example.cryptotracker

data class Crypto (
    val id : String,
    val name : String,
    val symbol: String,
    val price: Double,
    val percent_change_24h : Double,
    val price_change_24h : Double,
    val total_volume : String,
    val imageURL: String,
    val market_cap: String,
    val market_cap_rank: Int,
    val fully_diluted_valuation: String,
    val circulating_supply: String,
    val total_supply: String,
    val max_supply: String
    )
