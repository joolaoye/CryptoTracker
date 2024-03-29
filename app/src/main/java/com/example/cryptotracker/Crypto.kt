package com.example.cryptotracker

data class Crypto (
    val id : String,
    val name : String,
    val symbol: String,
    val price: Double,
    val percent_change_24h : Double,
    val imageURL: String
    )
