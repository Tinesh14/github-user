package com.example.githubuser

import android.app.Application
import com.example.data.di.networkModule
import com.example.githubuser.di.okHttpModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    okHttpModule,
                    networkModule
                )
            )
        }
    }
}
