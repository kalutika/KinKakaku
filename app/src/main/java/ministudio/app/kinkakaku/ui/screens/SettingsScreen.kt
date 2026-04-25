package ministudio.app.kinkakaku.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ministudio.app.kinkakaku.billing.BillingManager
import ministudio.app.kinkakaku.R
import ministudio.app.kinkakaku.localization.LanguageManager

private data class LanguageOption(
    val tag: String,
    val labelRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val billingUiState by BillingManager.uiState.collectAsStateWithLifecycle()
    val options = remember {
        listOf(
            LanguageOption(LanguageManager.LANGUAGE_ENGLISH, R.string.language_english),
            LanguageOption(LanguageManager.LANGUAGE_VIETNAMESE, R.string.language_vietnamese),
            LanguageOption(LanguageManager.LANGUAGE_JAPANESE, R.string.language_japanese)
        )
    }

    var selectedTag by remember {
        mutableStateOf(LanguageManager.getSavedLanguageTag(context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_language_section_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.settings_language_section_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

//            val currentLabel = options.firstOrNull { it.tag == selectedTag }?.let {
//                stringResource(it.labelRes)
//            } ?: stringResource(R.string.language_english)
//            Text(
//                text = stringResource(R.string.current_language, currentLabel),
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    LanguageOptionCard(
                        title = stringResource(option.labelRes),
                        selected = selectedTag == option.tag,
                        onClick = {
                            selectedTag = option.tag
                            LanguageManager.setLanguage(context, option.tag)
                        }
                    )
                }
            }

            // Temporarily hide the purchase option until we have a better strategy for handling different purchase states in the UI
//            RemoveAdsCard(
//                isPremium = billingUiState.isPremium,
//                isReady = billingUiState.isReady,
//                isLoading = billingUiState.isLoading,
//                isPurchasing = billingUiState.isPurchasing,
//                productAvailable = billingUiState.productAvailable,
//                error = billingUiState.error,
//                onBuyClick = { BillingManager.buyRemoveAds(context) },
//                onRestoreClick = { BillingManager.restorePurchases() }
//            )
        }
    }
}

@Composable
private fun LanguageOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun RemoveAdsCard(
    isPremium: Boolean,
    isReady: Boolean,
    isLoading: Boolean,
    isPurchasing: Boolean,
    productAvailable: Boolean,
    error: String?,
    onBuyClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_remove_ads_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.settings_remove_ads_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (isPremium) {
                    stringResource(R.string.remove_ads_status_active)
                } else {
                    stringResource(R.string.remove_ads_status_free)
                },
                fontWeight = FontWeight.SemiBold,
                color = if (isPremium) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBuyClick,
                    enabled = isReady && productAvailable && !isPremium && !isPurchasing
                ) {
                    Text(
                        text = if (isPurchasing) {
                            stringResource(R.string.purchase_processing)
                        } else {
                            stringResource(R.string.buy_remove_ads)
                        }
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestoreClick,
                    enabled = isReady && !isLoading && !isPurchasing
                ) {
                    Text(stringResource(R.string.restore_purchase))
                }
            }

            if (!isReady && isLoading) {
                Text(
                    text = stringResource(R.string.purchase_loading),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!productAvailable && !isPremium) {
                Text(
                    text = stringResource(R.string.remove_ads_unavailable),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

