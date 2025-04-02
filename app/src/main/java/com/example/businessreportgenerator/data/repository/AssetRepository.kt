package com.example.businessreportgenerator.data.repository

import com.example.businessreportgenerator.data.local.dao.AssetDao
import com.example.businessreportgenerator.data.local.entity.AssetEntity
import com.example.businessreportgenerator.domain.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssetRepository(private val assetDao: AssetDao) {

    fun getAllAssets(): Flow<List<Asset>> {
        return assetDao.getAllAssets().map { entities ->
            entities.map { it.toAsset() }
        }
    }

    suspend fun insertAsset(asset: Asset) {
        assetDao.insertAsset(AssetEntity.fromAsset(asset))
    }

    suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(AssetEntity.fromAsset(asset))
    }

    suspend fun loadSampleAssets(sampleAssets: List<Asset>) {
        assetDao.deleteAllAssets()
        sampleAssets.forEach { asset ->
            assetDao.insertAsset(AssetEntity.fromAsset(asset))
        }
    }
}