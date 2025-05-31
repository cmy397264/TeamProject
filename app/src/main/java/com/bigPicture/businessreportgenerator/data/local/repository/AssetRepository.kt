package com.bigPicture.businessreportgenerator.data.local.repository

import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import com.bigPicture.businessreportgenerator.data.local.dao.AssetDao
import com.bigPicture.businessreportgenerator.data.local.entity.AssetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssetRepository(private val assetDao: AssetDao) {

    suspend fun insertAsset(asset: Asset) {
        assetDao.insertAsset(asset.toEntity())
    }

    suspend fun updateAsset(asset: Asset) {
        assetDao.updateAsset(asset.toEntity())
    }

    suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(asset.toEntity())
    }

    suspend fun getAssetById(id: Long): Asset? {
        return assetDao.getAssetById(id)?.toDomain()
    }

    fun getAllAssets(): Flow<List<Asset>> {
        return assetDao.getAllAssets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun deleteAllAssets() {
        assetDao.deleteAllAssets()
    }
}

// Asset Domain Model을 AssetEntity로 변환
private fun Asset.toEntity(): AssetEntity {
    return AssetEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        purchasePrice = this.purchasePrice,
        purchaseDate = this.purchaseDate,
        details = this.details,
        ticker = this.ticker,
        currentPrice = this.currentPrice,
        lastUpdated = this.lastUpdated
    )
}

// AssetEntity를 Asset Domain Model로 변환
private fun AssetEntity.toDomain(): Asset {
    return Asset(
        id = this.id,
        name = this.name,
        type = this.type,
        purchasePrice = this.purchasePrice,
        purchaseDate = this.purchaseDate,
        details = this.details,
        ticker = this.ticker,
        currentPrice = this.currentPrice,
        lastUpdated = this.lastUpdated
    )
}