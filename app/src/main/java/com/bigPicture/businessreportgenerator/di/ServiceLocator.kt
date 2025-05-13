package com.bigPicture.businessreportgenerator.di

import android.content.Context
import androidx.room.Room
import com.bigPicture.businessreportgenerator.data.local.AppDatabase
import com.bigPicture.businessreportgenerator.data.local.repository.ReportRepository

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
