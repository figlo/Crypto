package sk.figlar.crypto

import sk.figlar.crypto.api.CryptoApi
import sk.figlar.crypto.api.CryptoApiModel
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val cryptoApi: CryptoApi,
) {
    suspend fun getCryptoApiModels(): List<CryptoApiModel> {
        return cryptoApi.getCryptoApiModels()
    }
}