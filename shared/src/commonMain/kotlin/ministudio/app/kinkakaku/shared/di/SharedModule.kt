package ministudio.app.kinkakaku.shared.di

import ministudio.app.kinkakaku.shared.network.ApiService
import ministudio.app.kinkakaku.shared.network.ApiServiceImpl
import ministudio.app.kinkakaku.shared.repository.DataRepository
import ministudio.app.kinkakaku.shared.repository.DataRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module

val sharedModule = module {
    single<ApiService> { ApiServiceImpl() }
    single<DataRepository> { DataRepositoryImpl(get()) }
}

fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}
