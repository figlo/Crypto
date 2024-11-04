package sk.figlar.crypto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.figlar.crypto.api.CryptoApiModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var cryptoRepository: CryptoRepository
): ViewModel() {

    private val _cryptos: MutableStateFlow<List<CryptoApiModel>> = MutableStateFlow(emptyList())
    val cryptos get() = _cryptos.asStateFlow()

    init {
        viewModelScope.launch {
            _cryptos.value = cryptoRepository.getCryptoApiModels()
        }
    }
}