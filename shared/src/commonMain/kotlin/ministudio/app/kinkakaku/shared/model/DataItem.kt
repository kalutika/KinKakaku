package ministudio.app.kinkakaku.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class DataItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val price: Double?,
    val category: String?,
    val buyPrice: Double? = null,
    val sellPrice: Double? = null,
    val changeBuy: Double? = null,
    val changeSell: Double? = null,
    val weight: String? = null,
    val lastUpdate: String? = null
)
