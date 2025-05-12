package com.example.businessreportgenerator.data.domain

data class Asset(
    val id: Long = 0L,
    val name: String,
    val type: AssetType,
    val purchasePrice: Double,
    val purchaseDate: Long? = null,
    val details: Map<String, String> = emptyMap()
)

enum class AssetType {
    REAL_ESTATE, STOCK, ETF, BOND, CRYPTO
}