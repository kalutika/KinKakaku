package ministudio.app.kinkakaku.di

import ministudio.app.kinkakaku.ui.viewmodel.DataViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { DataViewModel(get()) }
}
