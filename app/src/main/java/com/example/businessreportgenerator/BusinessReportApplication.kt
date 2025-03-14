package com.example.businessreportgenerator

import android.app.Application
import com.example.businessreportgenerator.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BusinessReportApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BusinessReportApplication)
            modules(appModule)
        }
    }
}