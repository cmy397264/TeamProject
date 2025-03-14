package com.example.businessreportgenerator.domain.model

import java.util.UUID

data class Asset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AssetType,
    val purchasePrice: Double,
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap()
)

enum class AssetType {
    REAL_ESTATE, STOCK, ETF, BOND, CRYPTO
}