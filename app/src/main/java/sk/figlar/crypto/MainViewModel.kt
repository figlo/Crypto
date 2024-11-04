package sk.figlar.crypto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    init {
        fetchCryptos()
    }

    private fun fetchCryptos() {
        viewModelScope.launch {
            while (true) {
                val fetchedCryptos = cryptoRepository.getCryptoApiModels()

                _cryptos.value = fetchedCryptos
                    .filter { it.symbol.takeLast(3) == "EUR" }    // filter EUR prices only
                    .filter { it.lastPrice >= 0.003 }                // filter out very cheap cryptos
                    .sortedByDescending { it.lastPrice }             // sort fetched cryptos higher prices first

                delay(1000)
            }
        }
    }
}