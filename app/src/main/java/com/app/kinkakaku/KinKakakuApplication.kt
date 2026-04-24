package com.app.kinkakaku

import android.app.Application
import com.app.kinkakaku.di.appModule
import com.app.kinkakaku.shared.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KinKakakuApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@KinKakakuApplication)
            modules(sharedModule, appModule)
        }
    }
}
