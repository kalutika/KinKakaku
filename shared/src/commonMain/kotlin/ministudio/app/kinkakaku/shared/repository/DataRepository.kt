package ministudio.app.kinkakaku.shared.repository

import ministudio.app.kinkakaku.shared.model.DataItem
import ministudio.app.kinkakaku.shared.network.ApiService

interface DataRepository {
    suspend fun getDataItems(): Result<List<DataItem>>
}

class DataRepositoryImpl(
    private val apiService: ApiService
) : DataRepository {

    override suspend fun getDataItems(): Result<List<DataItem>> {
        return try {
            val items = apiService.getDataItems()
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
