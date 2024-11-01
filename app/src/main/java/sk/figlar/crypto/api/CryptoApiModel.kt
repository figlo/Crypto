package sk.figlar.crypto.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CryptoApiModel(
    val symbol: String,
    val weightedAvgPrice: Double,
    val lastPrice: Double,
)