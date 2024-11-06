package sk.figlar.crypto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.figlar.crypto.api.CryptoApiModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var cryptoRepository: CryptoRepository
) : ViewModel() {

    private val _cryptos: MutableStateFlow<List<CryptoApiModel>> = MutableStateFlow(emptyList())
    val cryptos get() = _cryptos.asStateFlow()

    suspend fun fetchCryptos() {
        viewModelScope.launch {
            try {
                val fetchedCryptos = cryptoRepository.getCryptoApiModels()

                _cryptos.value = fetchedCryptos
                    .filter { it.symbol.takeLast(3) == "EUR" }    // filter EUR prices only
                    .filter { it.symbol != "EURAEUR" }               // remove this item, not crypto
                    .filter { it.lastPrice >= 0.003 }                // filter out very cheap cryptos
                    .sortedByDescending { it.lastPrice }             // sort fetched cryptos higher prices first

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Error fetching cryptos: $e")
            }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}