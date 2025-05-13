package com.bigPicture.businessreportgenerator.data.local.repository

import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.local.dao.AssetDao
import com.bigPicture.businessreportgenerator.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssetRepository(private val assetDao: AssetDao) : AssetRepositoryInterface {

    override fun getAllAssets(): Flow<List<Asset>> {
        return assetDao.getAllAssets().map { entities ->
            entities.map { it.toAsset() }
        }
    }

    override suspend fun insertAsset(asset: Asset) {
        val newId = assetDao.insertAsset(AssetEntity.fromAsset(asset))
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