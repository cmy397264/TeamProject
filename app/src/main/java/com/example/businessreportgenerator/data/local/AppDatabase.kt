package com.example.businessreportgenerator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.businessreportgenerator.data.local.converter.AssetTypeConverter
import com.example.businessreportgenerator.data.local.converter.MapConverter
import com.example.businessreportgenerator.data.local.dao.AssetDao
import com.example.businessreportgenerator.data.local.entity.AssetEntity

@Database(entities = [AssetEntity::class], version = 1, exportSchema = false)
@TypeConverters(AssetTypeConverter::class, MapConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portfolio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}