// presentation/features/portfolio/AddAssetDialog.kt
package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit
) {
    var assetName by remember { mutableStateOf("") }
    var assetType by remember { mutableStateOf(AssetType.STOCK) }
    var purchasePrice by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }

    // 추가 필드 (자산 유형별)
    var address by remember { mutableStateOf("") }  // 부동산
    var marketType by remember { mutableStateOf("") }  // 주식, ETF
    var averagePrice by remember { mutableStateOf("") }  // 주식, ETF, 채권, 코인

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("종목 추가하기") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 자산 이름
                OutlinedTextField(
                    value = assetName,
                    onValueChange = { assetName = it },
                    label = { Text("종목명") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 자산 유형 선택
                ExposedDropdownMenuBox(
                    expanded = false, // 상태 관리 추가 필요
                    onExpandedChange = { /* 상태 업데이트 */ },
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = when(assetType) {
                            AssetType.REAL_ESTATE -> "부동산"
                            AssetType.STOCK -> "주식"
                            AssetType.ETF -> "ETF"
                            AssetType.BOND -> "채권"
                            AssetType.CRYPTO -> "코인"
                        },
                        onValueChange = { },
                        label = { Text("자산 유형") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = false, // 상태와 연결 필요
                        onDismissRequest = { /* 상태 업데이트 */ },
                    ) {
                        DropdownMenuItem(
                            text = { Text("부동산") },
                            onClick = { assetType = AssetType.REAL_ESTATE }
                        )
                        DropdownMenuItem(
                            text = { Text("주식") },
                            onClick = { assetType = AssetType.STOCK }
                        )
                        DropdownMenuItem(
                            text = { Text("ETF") },
                            onClick = { assetType = AssetType.ETF }
                        )
                        DropdownMenuItem(
                            text = { Text("채권") },
                            onClick = { assetType = AssetType.BOND }
                        )
                        DropdownMenuItem(
                            text = { Text("코인") },
                            onClick = { assetType = AssetType.CRYPTO }
                        )
                    }
                }

                // 매수가격
                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("매수가격") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 자산 유형별 추가 필드
                when (assetType) {
                    AssetType.REAL_ESTATE -> {
                        // 부동산 필드
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("주소지") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = purchaseDate,
                            onValueChange = { purchaseDate = it },
                            label = { Text("매수시기 (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    AssetType.STOCK, AssetType.ETF -> {
                        // 주식, ETF 필드
                        ExposedDropdownMenuBox(
                            expanded = false, // 상태 필요
                            onExpandedChange = { /* 상태 업데이트 */ },
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = marketType,
                                onValueChange = { },
                                label = { Text("시장") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = false, // 상태 필요
                                onDismissRequest = { /* 상태 업데이트 */ },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("코스피") },
                                    onClick = { marketType = "코스피" }
                                )
                                DropdownMenuItem(
                                    text = { Text("코스닥") },
                                    onClick = { marketType = "코스닥" }
                                )
                                DropdownMenuItem(
                                    text = { Text("나스닥") },
                                    onClick = { marketType = "나스닥" }
                                )
                                DropdownMenuItem(
                                    text = { Text("NYSE") },
                                    onClick = { marketType = "NYSE" }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = averagePrice,
                            onValueChange = { averagePrice = it },
                            label = { Text("평단가") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    AssetType.BOND, AssetType.CRYPTO -> {
                        // 채권, 코인 필드
                        OutlinedTextField(
                            value = averagePrice,
                            onValueChange = { averagePrice = it },
                            label = { Text("평단가") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = purchasePrice.toDoubleOrNull() ?: 0.0
                    val dateValue = try {
                        purchaseDate.takeIf { it.isNotEmpty() }?.let {
                            // 간단한 날짜 변환 (YYYY-MM-DD 형식 가정)
                            val (year, month, day) = it.split("-").map { part -> part.toInt() }
                            Calendar.getInstance().apply {
                                set(year, month - 1, day)
                            }.timeInMillis
                        }
                    } catch (e: Exception) {
                        null
                    }

                    // 추가 세부 정보
                    val details = mutableMapOf<String, String>()

                    when (assetType) {
                        AssetType.REAL_ESTATE -> {
                            details["address"] = address
                        }
                        AssetType.STOCK, AssetType.ETF -> {
                            details["market"] = marketType
                            details["averagePrice"] = averagePrice
                        }
                        AssetType.BOND, AssetType.CRYPTO -> {
                            details["averagePrice"] = averagePrice
                        }
                    }

                    val asset = Asset(
                        name = assetName,
                        type = assetType,
                        purchasePrice = priceValue,
                        purchaseDate = dateValue,
                        details = details
                    )

                    onAddAsset(asset)
                }
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}