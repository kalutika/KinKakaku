package ministudio.app.kinkakaku.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import ministudio.app.kinkakaku.R
import ministudio.app.kinkakaku.billing.BillingManager
import ministudio.app.kinkakaku.ui.ads.AdMobBanner
import ministudio.app.kinkakaku.shared.model.DataItem
import ministudio.app.kinkakaku.ui.viewmodel.DataViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val GoldAccent = Color(0xFFC8A24A)
private val PositiveChangeColor = Color(0xFF5E8466)
private val NegativeChangeColor = Color(0xFF9A5D5D)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DataGridScreen(
    onSettingsClick: () -> Unit,
    viewModel: DataViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val billingUiState by BillingManager.uiState.collectAsStateWithLifecycle()
    val updatedAt = uiState.data.firstOrNull()?.lastUpdate

    LaunchedEffect(context) {
        BillingManager.initialize(context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!billingUiState.isPremium) {
                    AdMobBanner(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                if (updatedAt != null) {
                    Text(
                        text = stringResource(R.string.updated_at, updatedAt),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        textAlign = TextAlign.End,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (uiState.data.isNotEmpty()) TableHeader()
                if (uiState.data.isNotEmpty()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                uiState.isLoading && uiState.data.isEmpty() -> LoadingContent()
                uiState.error != null -> ErrorContent(error = uiState.error!!, onRetry = viewModel::retry)
                else -> PriceListContent(
                    data = uiState.data,
                    refreshing = uiState.isLoading,
                    onRefresh = viewModel::retry
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading_prices),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.error_prefix) + error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PriceListContent(
    data: List<DataItem>,
    refreshing: Boolean,
    onRefresh: () -> Unit
) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_data_available))
        }
        return
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = data,
                key = { it.id },
                contentType = { "price_row" }
            ) { item ->
                PriceRowCard(item)
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun TableHeader() {
    val leftWidth = 120.dp
    val priceWidth = 94.dp
    val priceGap = 12.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.table_header_product),
            modifier = Modifier.width(leftWidth),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(priceGap))
        Text(
            text = stringResource(R.string.table_header_buy),
            modifier = Modifier.width(priceWidth),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(priceGap))
        Text(
            text = stringResource(R.string.table_header_sell),
            modifier = Modifier.width(priceWidth),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PriceRowCard(item: DataItem) {
    val leftWidth = 120.dp
    val priceWidth = 94.dp
    val priceGap = 12.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(GoldAccent)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.width(leftWidth)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "(${item.category.orEmpty()})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(priceGap))

                PriceColumn(
                    modifier = Modifier.width(priceWidth),
                    value = item.buyPrice,
                    change = item.changeBuy,
                    currency = item.category
                )

                Spacer(modifier = Modifier.width(priceGap))

                PriceColumn(
                    modifier = Modifier.width(priceWidth),
                    value = item.sellPrice,
                    change = item.changeSell,
                    currency = item.category
                )
            }
        }
    }
}

@Composable
private fun PriceColumn(
    modifier: Modifier,
    value: Double?,
    change: Double?,
    currency: String?,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = formatMoney(value, currency),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        ChangeIndicator(change = change, currency = currency)
    }
}

@Composable
private fun ChangeIndicator(
    change: Double?,
    currency: String?
) {
    when {
        change == null -> {
            Text(
                text = "—",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        change == 0.0 -> {
            Text(
                text = formatChange(change, currency),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        change > 0 -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "▲", color = PositiveChangeColor, fontSize = 12.sp)
                Text(
                    text = formatChange(change, currency),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        else -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "▼", color = NegativeChangeColor, fontSize = 12.sp)
                Text(
                    text = formatChange(change, currency),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatMoney(value: Double?, currency: String?): String {
    if (value == null) return "—"
    return if (currency == "USD" && value % 1.0 != 0.0) {
        String.format(Locale.getDefault(), "%,.2f", value)
    } else {
        String.format(Locale.getDefault(), "%,.0f", value)
    }
}

private fun formatChange(value: Double?, currency: String?): String {
    if (value == null) return "—"
    val absValue = kotlin.math.abs(value)
    val formatted = if (currency == "USD" && absValue % 1.0 != 0.0) {
        String.format(Locale.getDefault(), "%,.2f", absValue)
    } else {
        String.format(Locale.getDefault(), "%,.0f", absValue)
    }
    return formatted
}
