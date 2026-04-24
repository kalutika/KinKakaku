package com.app.kinkakaku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kinkakaku.shared.model.DataItem
import com.app.kinkakaku.ui.viewmodel.DataViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DataGridScreen(
    onSettingsClick: () -> Unit,
    viewModel: DataViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "金 Kakaku", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                if (uiState.data.isNotEmpty()) {
                    TableHeader()
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingContent()
                uiState.error != null -> ErrorContent(error = uiState.error!!, onRetry = viewModel::retry)
                else -> PriceListContent(data = uiState.data)
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
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading prices...")
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
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun PriceListContent(data: List<DataItem>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
        items(data) { item -> PriceRowCard(item) }
        }
    }
}

@Composable
private fun TableHeader() {
    val leftWidth = 170.dp
    val priceWidth = 88.dp

    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sản phẩm",
                modifier = Modifier.width(leftWidth),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "Giá mua",
                modifier = Modifier.width(priceWidth),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "Giá bán",
                modifier = Modifier.width(priceWidth),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PriceRowCard(item: DataItem) {
    val leftWidth = 170.dp
    val priceWidth = 88.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.width(leftWidth)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "(${item.category.orEmpty()})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    item.lastUpdate?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                PriceColumn(
                    modifier = Modifier.width(priceWidth),
                    value = item.buyPrice,
                    change = item.changeBuy,
                    currency = item.category,
                    changeColor = MaterialTheme.colorScheme.error
                )

                PriceColumn(
                    modifier = Modifier.width(priceWidth),
                    value = item.sellPrice,
                    change = item.changeSell,
                    currency = item.category,
                    changeColor = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun PriceColumn(
    modifier: Modifier,
    value: Double?,
    change: Double?,
    currency: String?,
    changeColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = formatMoney(value, currency),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(6.dp))
        ChangeIndicator(change = change, currency = currency, tint = changeColor)
    }
}

@Composable
private fun ChangeIndicator(
    change: Double?,
    currency: String?,
    tint: Color
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
                Text(text = "▲", color = tint, fontSize = 12.sp)
                Text(
                    text = formatChange(change, currency),
                    fontSize = 11.sp,
                    color = tint,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        else -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "▼", color = tint, fontSize = 12.sp)
                Text(
                    text = formatChange(change, currency),
                    fontSize = 11.sp,
                    color = tint,
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
