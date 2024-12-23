package sk.figlar.crypto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import sk.figlar.crypto.ui.theme.AppTheme
import sk.figlar.crypto.utils.formatPerc
import sk.figlar.crypto.utils.formatPrice
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    topBar = {
                        AppToolbar(
                            toolbarTitle = stringResource(R.string.toolbar_title)
                        )
                    }
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val lifecycle = LocalLifecycleOwner.current
    val cryptos by viewModel.cryptos.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while(true) {
                viewModel.fetchCryptos()
                delay(4000)
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(start = 4.dp, top = 64.dp, end = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.symbol),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(2f)
                .wrapContentSize()
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.avg_price_24h_eur),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(3f)
                .wrapContentSize()
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.last_price_eur),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(3f)
                .wrapContentSize()
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.change),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .weight(2f)
                .wrapContentSize()
                .align(Alignment.CenterVertically),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
    ) {
        items(cryptos) { crypto ->
            val weightedAvgPrice = crypto.weightedAvgPrice
            val lastPrice = crypto.lastPrice

            // compute price change (%)
            val priceChangePerc = (lastPrice - weightedAvgPrice) * 100 / weightedAvgPrice

            // compute row background color
            val colorChange = minOf(255, (abs(priceChangePerc) * 70).toInt())
            val backgroundColor = when {
                lastPrice > weightedAvgPrice -> android.graphics.Color.rgb(255 - colorChange, 255, 255 - colorChange)
                lastPrice < weightedAvgPrice -> android.graphics.Color.rgb(255, 255 - colorChange, 255 - colorChange)
                else                         -> android.graphics.Color.rgb(255, 255, 255)
            }

            // set font color to black even in dark mode
            val fontColor = Color.Black

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillParentMaxWidth()
                    .background(Color(backgroundColor))
                    .padding(4.dp)
            ) {
                Text(
                    text = crypto.symbol.dropLast(3),
                    modifier = Modifier.weight(2f),
                    color = fontColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )
                Text(
                    text = formatPrice(crypto.weightedAvgPrice),
                    modifier = Modifier.weight(3f),
                    color = fontColor,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = formatPrice(crypto.lastPrice),
                    modifier = Modifier.weight(3f),
                    color = fontColor,
                    textAlign = TextAlign.Right,
                )
                Text(
                    text = formatPerc(priceChangePerc),
                    modifier = Modifier.weight(2f),
                    color = fontColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(toolbarTitle: String) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        title = {
            Text(toolbarTitle)
        }
    )
}