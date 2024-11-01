package sk.figlar.crypto.api

import retrofit2.http.GET

interface CryptoApi {

    @GET("api/v3/ticker/24hr")
    suspend fun getCryptoApiModels(): List<CryptoApiModel>

}