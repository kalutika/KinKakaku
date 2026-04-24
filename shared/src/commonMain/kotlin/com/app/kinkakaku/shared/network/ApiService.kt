package com.app.kinkakaku.shared.network

import com.app.kinkakaku.shared.model.DataItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
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
        // For demo purposes, using JSONPlaceholder API
        // Replace with your actual API endpoint
        return httpClient.get("https://jsonplaceholder.typicode.com/posts").body<List<PostResponse>>()
            .map { post ->
                DataItem(
                    id = post.id,
                    title = post.title,
                    description = post.body,
                    imageUrl = "https://picsum.photos/200/200?random=${post.id}",
                    price = (post.id * 10.99),
                    category = "Sample Category"
                )
            }
    }
}

@kotlinx.serialization.Serializable
private data class PostResponse(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)
