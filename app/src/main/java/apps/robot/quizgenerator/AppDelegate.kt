package apps.robot.quizgenerator

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppDelegate : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initKoinDi()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    private fun initKoinDi() {
        startKoin {
            androidContext(this@AppDelegate)
            modules(appModule())
        }
    }
}

