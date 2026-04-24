package ministudio.app.kinkakaku

import android.app.Application
import ministudio.app.kinkakaku.di.appModule
import ministudio.app.kinkakaku.shared.di.sharedModule
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
