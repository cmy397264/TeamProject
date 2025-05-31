package com.bigPicture.businessreportgenerator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bigPicture.businessreportgenerator.data.local.converter.AssetTypeConverter
import com.bigPicture.businessreportgenerator.data.local.converter.MapConverter
import com.bigPicture.businessreportgenerator.data.local.dao.AssetDao
import com.bigPicture.businessreportgenerator.data.local.dao.ReportDao
import com.bigPicture.businessreportgenerator.data.local.dao.StockDao
import com.bigPicture.businessreportgenerator.data.local.entity.AssetEntity
import com.bigPicture.businessreportgenerator.data.local.entity.ReportEntity
import com.bigPicture.businessreportgenerator.data.local.entity.StockEntity

@Database(
    entities = [AssetEntity::class, ReportEntity::class, StockEntity::class],
    version = 8, // 버전을 7에서 8로 올림 (새 필드 추가로 인해)
    exportSchema = false
)
@TypeConverters(AssetTypeConverter::class, MapConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun reportDao(): ReportDao
    abstract fun stockDao(): StockDao

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
                    .fallbackToDestructiveMigration() // 개발 중에만 사용
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}