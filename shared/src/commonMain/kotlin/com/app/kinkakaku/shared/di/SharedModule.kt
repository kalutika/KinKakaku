package com.app.kinkakaku.shared.di

import com.app.kinkakaku.shared.network.ApiService
import com.app.kinkakaku.shared.network.ApiServiceImpl
import com.app.kinkakaku.shared.repository.DataRepository
import com.app.kinkakaku.shared.repository.DataRepositoryImpl
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
