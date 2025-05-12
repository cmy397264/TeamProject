package com.example.businessreportgenerator.data.local.repository

import com.example.businessreportgenerator.data.domain.Asset
import com.example.businessreportgenerator.data.local.dao.AssetDao
import com.example.businessreportgenerator.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssetRepository(private val assetDao: AssetDao) : AssetRepositoryInterface {

    override fun getAllAssets(): Flow<List<Asset>> {
        return assetDao.getAllAssets().map { entities ->
            entities.map { it.toAsset() }
        }
    }

    override suspend fun insertAsset(asset: Asset) {
        assetDao.insertAsset(AssetEntity.Companion.fromAsset(asset))
    }

    override suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(AssetEntity.Companion.fromAsset(asset))
    }

    override suspend fun loadSampleAssets(sampleAssets: List<Asset>) {
        assetDao.deleteAllAssets()
        sampleAssets.forEach { asset ->
            assetDao.insertAsset(AssetEntity.Companion.fromAsset(asset))
        }
    }
}