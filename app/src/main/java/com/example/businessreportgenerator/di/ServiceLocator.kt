package com.example.businessreportgenerator.di

import android.content.Context
import androidx.room.Room
import com.example.businessreportgenerator.data.local.AppDatabase
import com.example.businessreportgenerator.data.local.repository.ReportRepository

object ServiceLocator {

    // ① DB
    @Volatile private var database: AppDatabase? = null

    private fun provideDatabase(context: Context): AppDatabase =
        database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "bigpicture.db"
            ).build().also { database = it }
        }

    // ② Repository
    @Volatile private var reportRepo: ReportRepository? = null

    fun provideReportRepository(context: Context): ReportRepository =
        reportRepo ?: synchronized(this) {
            reportRepo ?: ReportRepository(
                provideDatabase(context).reportDao()
            ).also { reportRepo = it }
        }
}
