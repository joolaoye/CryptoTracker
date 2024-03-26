package com.example.cryptotracker

data class Crypto (
    val name : String,
    val symbol: String,
    val price: Double,
    val percent_change_24h : Double,
    val imageURL: String
    )
