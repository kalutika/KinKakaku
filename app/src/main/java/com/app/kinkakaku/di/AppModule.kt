package com.app.kinkakaku.di

import com.app.kinkakaku.ui.viewmodel.DataViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { DataViewModel(get()) }
}
