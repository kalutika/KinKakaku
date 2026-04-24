package com.app.kinkakaku.shared.network

import com.app.kinkakaku.shared.model.DataItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface ApiService {
    suspend fun getDataItems(): List<DataItem>
}

class ApiServiceImpl : ApiService {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    override suspend fun getDataItems(): List<DataItem> {
        println("Gold API: Fetching data from vang.today...")
        val response = httpClient.get("https://www.vang.today/api/prices")
            .body<GoldPriceResponse>()

        println("Gold API: Response received, items count: ${response.prices.size}")

        return response.prices.entries.mapIndexed { index, (key, priceData) ->
            DataItem(
                id = index + 1,
                title = priceData.name,
                description = key,
                imageUrl = null,
                price = priceData.buy,
                category = priceData.currency,
                buyPrice = priceData.buy,
                sellPrice = priceData.sell,
                changeBuy = priceData.changeBuy,
                changeSell = priceData.changeSell,
                lastUpdate = "${response.date} ${response.time}"
            )
        }.also {
            println("Gold API: Successfully mapped ${it.size} items")
        }
    }
}

@Serializable
data class GoldPriceResponse(
    val success: Boolean,
    val timestamp: Long,
    val time: String,
    val date: String,
    val count: Int,
    val prices: Map<String, PriceData>
)

@Serializable
data class PriceData(
    val name: String,
    val buy: Double,
    val sell: Double,
    @SerialName("change_buy") val changeBuy: Double,
    @SerialName("change_sell") val changeSell: Double,
    val currency: String
)

