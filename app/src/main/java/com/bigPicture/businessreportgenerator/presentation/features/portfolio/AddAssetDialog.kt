package com.bigPicture.businessreportgenerator.presentation.features.portfolio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bigPicture.businessreportgenerator.data.domain.Asset
import com.bigPicture.businessreportgenerator.data.domain.AssetType
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.util.Calendar

data class ModernAssetTypeOption(
    val type: AssetType,
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

val AssetTypes = listOf(
    ModernAssetTypeOption(
        type = AssetType.STOCK,
        displayName = "주식",
        icon = Icons.Rounded.AddCircle,
        color = Color(0xFF6366F1),
        description = "개별 기업 주식"
    ),
    ModernAssetTypeOption(
        type = AssetType.ETF,
        displayName = "ETF",
        icon = Icons.Rounded.AccountBox,
        color = Color(0xFF8B5CF6),
        description = "상장지수펀드"
    ),
    ModernAssetTypeOption(
        type = AssetType.CRYPTO,
        displayName = "암호화폐",
        icon = Icons.Rounded.Star,
        color = Color(0xFFF59E0B),
        description = "디지털 자산"
    )
)

@Composable
fun AddAssetDialog(
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit
) {
    val portfolioViewModel: PortfolioViewModel = koinViewModel()
    val portfolioState by portfolioViewModel.state.collectAsState()

    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    Dialog(
        onDismissRequest = {
            visibleState.targetState = false
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .blur(if (visibleState.currentState) 0.dp else 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(animationSpec = tween(400)) +
                        scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialScale = 0.8f
                        ),
                exit = fadeOut(animationSpec = tween(300)) +
                        scaleOut(animationSpec = tween(300))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        ),
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp)
                ) {
                    ModernAddAssetForm(
                        onDismiss = {
                            visibleState.targetState = false
                            onDismiss()
                        },
                        onAddAsset = onAddAsset,
                        portfolioViewModel = portfolioViewModel,
                        portfolioState = portfolioState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAddAssetForm(
    onDismiss: () -> Unit,
    onAddAsset: (Asset) -> Unit,
    portfolioViewModel: PortfolioViewModel,
    portfolioState: ModernPortfolioState
) {
    var assetName by remember { mutableStateOf("") }
    var selectedAssetType by remember { mutableStateOf<ModernAssetTypeOption?>(null) }
    var showAssetTypeDropdown by remember { mutableStateOf(false) }
    var purchasePriceText by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }
    var ticker by remember { mutableStateOf("") }
    var isTickerValid by remember { mutableStateOf<Boolean?>(null) }

    // 추가 필드 (자산 유형별)
    var marketType by remember { mutableStateOf("") }
    var showMarketTypeDropdown by remember { mutableStateOf(false) }
    var averagePrice by remember { mutableStateOf("") }

    val priceFormatter = remember { DecimalFormat("#,###") }
    val coroutineScope = rememberCoroutineScope()

    // 티커 유효성 검사 (디바운스 적용)
    LaunchedEffect(ticker, selectedAssetType?.type) {
        if (selectedAssetType?.type == AssetType.STOCK && ticker.isNotBlank()) {
            delay(500) // 0.5초 디바운스
            isTickerValid = portfolioViewModel.validateTicker(ticker.uppercase())
        } else {
            isTickerValid = null
        }
    }

    // 에러 메시지 클리어
    LaunchedEffect(portfolioState.tickerValidationError) {
        if (portfolioState.tickerValidationError != null) {
            delay(3000)
            portfolioViewModel.clearTickerValidationError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // 현대적인 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "종목 추가",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
            )

            val canAdd = assetName.isNotEmpty() &&
                    selectedAssetType != null &&
                    purchasePriceText.isNotEmpty() &&
                    (selectedAssetType?.type != AssetType.STOCK || isTickerValid == true)

            Button(
                onClick = {
                    val cleanPriceText = purchasePriceText.replace(",", "")
                    val priceValue = cleanPriceText.toDoubleOrNull() ?: 0.0

                    val cleanAveragePriceText = averagePrice.replace(",", "")
                    val avgPriceValue = cleanAveragePriceText.toDoubleOrNull() ?: 0.0

                    val dateValue = try {
                        purchaseDate.takeIf { it.isNotEmpty() }?.let {
                            val (year, month, day) = it.split("-").map { part -> part.toInt() }
                            Calendar.getInstance().apply {
                                set(year, month - 1, day)
                            }.timeInMillis
                        }
                    } catch (e: Exception) {
                        null
                    }

                    val details = mutableMapOf<String, String>()
                    when (selectedAssetType?.type) {
                        AssetType.STOCK, AssetType.ETF -> {
                            details["market"] = marketType
                            details["averagePrice"] = avgPriceValue.toString()
                        }
                        AssetType.CRYPTO -> {
                            details["averagePrice"] = avgPriceValue.toString()
                        }
                        else -> { }
                    }

                    val asset = Asset(
                        name = assetName,
                        type = selectedAssetType?.type ?: AssetType.STOCK,
                        purchasePrice = priceValue,
                        purchaseDate = dateValue,
                        details = details,
                        ticker = if (selectedAssetType?.type == AssetType.STOCK) ticker.uppercase() else null
                    )

                    onAddAsset(asset)
                },
                enabled = canAdd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.4f),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .shadow(
                        elevation = if (canAdd) 4.dp else 0.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    "추가",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 자산 유형 선택 - 현대적 카드 스타일
        Text(
            text = "자산 유형",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssetTypes.forEach { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAssetType = option
                            if (option.type != AssetType.STOCK) {
                                ticker = ""
                                isTickerValid = null
                            }
                        }
                        .shadow(
                            elevation = if (selectedAssetType == option) 8.dp else 2.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAssetType == option) {
                            option.color.copy(alpha = 0.1f)
                        } else {
                            Color.White
                        }
                    ),
                    border = if (selectedAssetType == option) {
                        androidx.compose.foundation.BorderStroke(
                            2.dp,
                            option.color
                        )
                    } else {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFE2E8F0)
                        )
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(option.color.copy(alpha = 0.1f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.displayName,
                                tint = option.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = option.description,
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        if (selectedAssetType == option) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(option.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "선택됨",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 종목명 입력
        Text(
            text = "종목명",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = assetName,
            onValueChange = { assetName = it },
            placeholder = {
                Text(
                    "예: 삼성전자, 애플, 비트코인",
                    color = Color(0xFF94A3B8)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF6366F1),
                containerColor = Color.White
            ),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 주식인 경우 티커 입력 필드 추가
        if (selectedAssetType?.type == AssetType.STOCK) {
            Text(
                text = "티커 심볼",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = ticker,
                onValueChange = {
                    ticker = it.uppercase()
                    isTickerValid = null
                },
                placeholder = {
                    Text(
                        "예: AAPL, NVDA, 005930",
                        color = Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = when {
                        portfolioState.isValidatingTicker -> Color(0xFF6366F1)
                        isTickerValid == true -> Color(0xFF10B981)
                        isTickerValid == false -> Color(0xFFEF4444)
                        else -> Color(0xFF6366F1)
                    },
                    unfocusedBorderColor = when {
                        isTickerValid == true -> Color(0xFF10B981)
                        isTickerValid == false -> Color(0xFFEF4444)
                        else -> Color(0xFFE2E8F0)
                    },
                    cursorColor = Color(0xFF6366F1),
                    containerColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF1E293B)
                ),
                trailingIcon = {
                    when {
                        portfolioState.isValidatingTicker -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF6366F1),
                                strokeWidth = 2.dp
                            )
                        }
                        isTickerValid == true -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "유효한 티커",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        isTickerValid == false -> {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "유효하지 않은 티커",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                supportingText = {
                    when {
                        portfolioState.tickerValidationError != null -> {
                            Text(
                                text = portfolioState.tickerValidationError,
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp
                            )
                        }
                        isTickerValid == true -> {
                            Text(
                                text = "유효한 티커입니다",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp
                            )
                        }
                        ticker.isNotBlank() && !portfolioState.isValidatingTicker && isTickerValid == null -> {
                            Text(
                                text = "티커를 확인하는 중...",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 자산 유형별 추가 필드
        selectedAssetType?.let { assetType ->
            when (assetType.type) {
                AssetType.STOCK, AssetType.ETF -> {
                    // 시장 선택
                    Text(
                        text = "거래소",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMarketTypeDropdown = true }
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (marketType.isEmpty()) "거래소를 선택하세요" else marketType,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = if (marketType.isEmpty()) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                    )
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "드롭다운",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showMarketTypeDropdown,
                            onDismissRequest = { showMarketTypeDropdown = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                        ) {
                            listOf("코스피", "코스닥", "나스닥", "NYSE").forEach { market ->
                                DropdownMenuItem(
                                    text = { Text(market, fontSize = 16.sp) },
                                    onClick = {
                                        marketType = market
                                        showMarketTypeDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 평단가
                    Text(
                        text = "평단가 (₩)",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = averagePrice,
                        onValueChange = { input ->
                            val numericValue = input.replace(Regex("[^0-9]"), "")
                            if (numericValue.isNotEmpty()) {
                                val numberValue = numericValue.toLongOrNull() ?: 0L
                                averagePrice = priceFormatter.format(numberValue)
                            } else {
                                averagePrice = ""
                            }
                        },
                        placeholder = { Text("0", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            cursorColor = Color(0xFF6366F1),
                            containerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                AssetType.CRYPTO -> {
                    // 평단가만
                    Text(
                        text = "평단가 (₩)",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = averagePrice,
                        onValueChange = { input ->
                            val numericValue = input.replace(Regex("[^0-9]"), "")
                            if (numericValue.isNotEmpty()) {
                                val numberValue = numericValue.toLongOrNull() ?: 0L
                                averagePrice = priceFormatter.format(numberValue)
                            } else {
                                averagePrice = ""
                            }
                        },
                        placeholder = { Text("0", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF6366F1),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            cursorColor = Color(0xFF6366F1),
                            containerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                else -> { }
            }
        }

        // 총 투자금액
        Text(
            text = "총 투자금액 (₩)",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = purchasePriceText,
            onValueChange = { input ->
                val numericValue = input.replace(Regex("[^0-9]"), "")
                if (numericValue.isNotEmpty()) {
                    val numberValue = numericValue.toLongOrNull() ?: 0L
                    purchasePriceText = priceFormatter.format(numberValue)
                } else {
                    purchasePriceText = ""
                }
            },
            placeholder = { Text("0", color = Color(0xFF94A3B8)) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF6366F1),
                containerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}