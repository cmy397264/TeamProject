package com.bigPicture.businessreportgenerator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bigPicture.businessreportgenerator.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Long): AssetEntity?

    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("DELETE FROM assets")
    suspend fun deleteAllAssets()

    // 티커로 자산 찾기 (추가 유틸리티 메서드)
    @Query("SELECT * FROM assets WHERE ticker = :ticker")
    suspend fun getAssetByTicker(ticker: String): AssetEntity?

    // 특정 타입의 자산들만 가져오기
    @Query("SELECT * FROM assets WHERE type = :type")
    fun getAssetsByType(type: String): Flow<List<AssetEntity>>
}