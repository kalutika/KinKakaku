package com.app.kinkakaku.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class DataItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val price: Double?,
    val category: String?
)
