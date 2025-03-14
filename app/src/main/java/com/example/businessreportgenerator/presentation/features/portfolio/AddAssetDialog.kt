package com.example.businessreportgenerator.presentation.features.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.businessreportgenerator.domain.model.Asset
import com.example.businessreportgenerator.domain.model.AssetType
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit
) {
    val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { it / 2 },
            exit = fadeOut(animationSpec = tween(300)) +
                    slideOutVertically(animationSpec = tween(300)) { it / 2 }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                AddAssetForm(
                    onDismiss = {
                        visibleState.targetState = false
                        onDismiss()
                    },
                    onAddAsset = onAddAsset
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetForm(
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit
) {
    var assetName by remember { mutableStateOf("") }
    var selectedAssetType by remember { mutableStateOf<AssetType?>(null) }
    var showAssetTypeDropdown by remember { mutableStateOf(false) }
    var purchasePriceText by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }

    // 추가 필드 (자산 유형별)
    var address by remember { mutableStateOf("") }              // 부동산
    var marketType by remember { mutableStateOf("") }           // 주식, ETF
    var showMarketTypeDropdown by remember { mutableStateOf(false) }
    var averagePrice by remember { mutableStateOf("") }         // 주식, ETF, 채권, 코인

    // 천 단위 구분 포맷터
    val priceFormatter = remember { DecimalFormat("#,###") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color(0xFF007AFF)
                )
            }

            Text(
                text = "자산 추가",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Button(
                onClick = {
                    // 가격 문자열에서 쉼표 제거 후 숫자로 변환
                    val cleanPriceText = purchasePriceText.replace(",", "")
                    val priceValue = cleanPriceText.toDoubleOrNull() ?: 0.0

                    val cleanAveragePriceText = averagePrice.replace(",", "")
                    val avgPriceValue = cleanAveragePriceText.toDoubleOrNull() ?: 0.0

                    // 날짜 변환
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

                    when (selectedAssetType) {
                        AssetType.REAL_ESTATE -> {
                            details["address"] = address
                        }

                        AssetType.STOCK, AssetType.ETF -> {
                            details["market"] = marketType
                            details["averagePrice"] = avgPriceValue.toString()
                        }

                        AssetType.BOND, AssetType.CRYPTO -> {
                            details["averagePrice"] = avgPriceValue.toString()
                        }

                        else -> { /* nothing to add */
                        }
                    }

                    val asset = Asset(
                        name = assetName,
                        type = selectedAssetType ?: AssetType.STOCK,
                        purchasePrice = priceValue,
                        purchaseDate = dateValue,
                        details = details
                    )

                    onAddAsset(asset)
                },
                enabled = assetName.isNotEmpty() && selectedAssetType != null && purchasePriceText.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF007AFF).copy(alpha = 0.5f),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("추가")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 자산 유형 (가장 상단에 배치)
        Text(
            text = "자산 유형",
            style = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { showAssetTypeDropdown = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (selectedAssetType) {
                        AssetType.REAL_ESTATE -> "부동산"
                        AssetType.STOCK -> "주식"
                        AssetType.ETF -> "ETF"
                        AssetType.BOND -> "채권"
                        AssetType.CRYPTO -> "코인"
                        null -> "자산 유형을 선택하세요"
                    },
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = if (selectedAssetType == null) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "드롭다운",
                    tint = Color.Gray
                )
            }

            DropdownMenu(
                expanded = showAssetTypeDropdown,
                onDismissRequest = { showAssetTypeDropdown = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("부동산", fontSize = 16.sp) },
                    onClick = {
                        selectedAssetType = AssetType.REAL_ESTATE
                        showAssetTypeDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("주식", fontSize = 16.sp) },
                    onClick = {
                        selectedAssetType = AssetType.STOCK
                        showAssetTypeDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("ETF", fontSize = 16.sp) },
                    onClick = {
                        selectedAssetType = AssetType.ETF
                        showAssetTypeDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("채권", fontSize = 16.sp) },
                    onClick = {
                        selectedAssetType = AssetType.BOND
                        showAssetTypeDropdown = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("코인", fontSize = 16.sp) },
                    onClick = {
                        selectedAssetType = AssetType.CRYPTO
                        showAssetTypeDropdown = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 자산 이름
        Text(
            text = "자산 이름",
            style = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = assetName,
            onValueChange = { assetName = it },
            placeholder = { Text("자산의 이름을 입력하세요") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF007AFF),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = Color(0xFF007AFF)
            ),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 자산 유형별 추가 필드
        when (selectedAssetType) {
            AssetType.REAL_ESTATE -> {
                // 부동산 필드
                Text(
                    text = "주소지",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    placeholder = { Text("부동산의 주소를 입력하세요") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 매수가격
                Text(
                    text = "매수가격 (₩)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { input ->
                        // 쉼표 제거 후 숫자만 추출
                        val numericValue = input.replace(Regex("[^0-9]"), "")
                        if (numericValue.isNotEmpty()) {
                            val numberValue = numericValue.toLongOrNull() ?: 0L
                            purchasePriceText = priceFormatter.format(numberValue)
                        } else {
                            purchasePriceText = ""
                        }
                    },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 매수시기
                Text(
                    text = "매수시기 (YYYY-MM-DD)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = purchaseDate,
                    onValueChange = { purchaseDate = it },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    textStyle = TextStyle(fontSize = 16.sp)
                )
            }

            AssetType.STOCK, AssetType.ETF -> {
                // 주식, ETF 필드
                Text(
                    text = "시장",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showMarketTypeDropdown = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (marketType.isEmpty()) "시장을 선택하세요" else marketType,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = if (marketType.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "드롭다운",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = showMarketTypeDropdown,
                        onDismissRequest = { showMarketTypeDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("코스피", fontSize = 16.sp) },
                            onClick = {
                                marketType = "코스피"
                                showMarketTypeDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("코스닥", fontSize = 16.sp) },
                            onClick = {
                                marketType = "코스닥"
                                showMarketTypeDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("나스닥", fontSize = 16.sp) },
                            onClick = {
                                marketType = "나스닥"
                                showMarketTypeDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("NYSE", fontSize = 16.sp) },
                            onClick = {
                                marketType = "NYSE"
                                showMarketTypeDropdown = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 평단가
                Text(
                    text = "평단가 (₩)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = averagePrice,
                    onValueChange = { input ->
                        // 쉼표 제거 후 숫자만 추출
                        val numericValue = input.replace(Regex("[^0-9]"), "")
                        if (numericValue.isNotEmpty()) {
                            val numberValue = numericValue.toLongOrNull() ?: 0L
                            averagePrice = priceFormatter.format(numberValue)
                        } else {
                            averagePrice = ""
                        }
                    },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 매수가격
                Text(
                    text = "매수가격 (₩)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { input ->
                        // 쉼표 제거 후 숫자만 추출
                        val numericValue = input.replace(Regex("[^0-9]"), "")
                        if (numericValue.isNotEmpty()) {
                            val numberValue = numericValue.toLongOrNull() ?: 0L
                            purchasePriceText = priceFormatter.format(numberValue)
                        } else {
                            purchasePriceText = ""
                        }
                    },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp)
                )
            }

            AssetType.BOND, AssetType.CRYPTO -> {
                // 채권, 코인 필드
                Text(
                    text = "평단가 (₩)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = averagePrice,
                    onValueChange = { input ->
                        // 쉼표 제거 후 숫자만 추출
                        val numericValue = input.replace(Regex("[^0-9]"), "")
                        if (numericValue.isNotEmpty()) {
                            val numberValue = numericValue.toLongOrNull() ?: 0L
                            averagePrice = priceFormatter.format(numberValue)
                        } else {
                            averagePrice = ""
                        }
                    },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 매수가격
                Text(
                    text = "매수가격 (₩)",
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = purchasePriceText,
                    onValueChange = { input ->
                        // 쉼표 제거 후 숫자만 추출
                        val numericValue = input.replace(Regex("[^0-9]"), "")
                        if (numericValue.isNotEmpty()) {
                            val numberValue = numericValue.toLongOrNull() ?: 0L
                            purchasePriceText = priceFormatter.format(numberValue)
                        } else {
                            purchasePriceText = ""
                        }
                    },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF007AFF)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp)
                )
            }

            null -> {
                // 자산 유형이 선택되지 않은 경우
                Text(
                    text = "자산 유형을 선택하면 추가 필드가 표시됩니다",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        }
    }}