package com.bigPicture.businessreportgenerator.presentation.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.data.remote.dto.ReportRequest
import com.bigPicture.businessreportgenerator.notification.NotificationHelper
import java.time.DayOfWeek

@Composable
fun OnboardingScreen(
    onComplete: (ReportRequest) -> Unit
) {
    val onBoardingViewModel: OnboardingViewModel = viewModel()
    val state by onBoardingViewModel.state.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFE2E8F0)
                    )
                )
            )
    ) {
        when (state.currentStep) {
            0 -> ModernBasicInfoStep(
                userData = state.userData,
                onNext = { name, age, riskTolerance, reportComplexity ->
                    onBoardingViewModel.setBasicInfo(name, age, riskTolerance, reportComplexity)
                    onBoardingViewModel.nextStep()
                }
            )
            1 -> ModernInterestsStep(
                userData = state.userData,
                onBack = { onBoardingViewModel.prevStep() },
                onComplete = { interests, reportDays ->
                    onBoardingViewModel.setInterestsAndDays(interests, reportDays)

                    val sharedPrefs = context.getSharedPreferences("user_prefs", 0)
                    sharedPrefs.edit {
                        putString("user_name", state.userData.name)
                        putInt("user_age", state.userData.age)
                        putString("risk_tolerance", state.userData.riskTolerance)
                        putString("report_complexity", state.userData.reportComplexity)
                        putStringSet("interests", interests.toSet())
                        putString("report_days", reportDays.joinToString(","))
                        putBoolean("onboarding_completed", true)
                    }

                    val riskTolerance = sharedPrefs.getString("risk_tolerance", null).toString()
                    val reportComplexity = sharedPrefs.getString("report_complexity", null).toString()
                    val interests = sharedPrefs.getStringSet("interests", null)!!.toList()

                    val userReportRequest = ReportRequest(
                        reportType = "economy",
                        stockName = null,
                        riskTolerance = riskTolerance,
                        reportDifficultyLevel = reportComplexity,
                        interestAreas = interests,
                    )

                    onComplete(userReportRequest)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernBasicInfoStep(
    userData: UserData,
    onNext: (String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(userData.name) }
    var ageText by remember { mutableStateOf(if(userData.age > 0) userData.age.toString() else "") }
    var selectedRiskLevel by remember { mutableIntStateOf(getRiskLevelIndex(userData.riskTolerance)) }
    var selectedReportLevel by remember { mutableIntStateOf(getReportLevelIndex(userData.reportComplexity)) }

    val progress by animateFloatAsState(
        targetValue = 0.5f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val riskLevelOptions = listOf(
        "하이리스크 하이리턴",
        "중고위험",
        "중립",
        "중저위험",
        "로우리스크 로우리턴"
    )

    val riskLevelDescriptions = listOf(
        "높은 수익을 위해 위험을 감수할 수 있습니다",
        "평균 이상의 위험을 감수할 수 있습니다",
        "안정성과 수익성의 균형을 추구합니다",
        "안정적인 투자를 선호합니다",
        "원금 보장을 최우선으로 합니다"
    )

    val reportLevelOptions = listOf(
        "전문가 수준",
        "상세한 분석",
        "표준 수준",
        "쉬운 설명"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        // 현대적인 진행 표시
        Column(
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1/2 단계",
                    fontSize = 14.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "50%",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE2E8F0)
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
        ) {
            Column {
                // 헤더 섹션
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "프로필",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "프로필 설정",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "맞춤형 투자 분석을 위해\n기본 정보를 입력해주세요",
                    fontSize = 18.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 기본 정보 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        // 이름 입력
                        Text(
                            text = "이름 또는 닉네임",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = {
                                Text(
                                    "이름을 입력하세요",
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
                                containerColor = Color(0xFFFAFBFC)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 나이 입력
                        Text(
                            text = "나이",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = ageText,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    ageText = it
                                }
                            },
                            placeholder = {
                                Text(
                                    "나이를 입력하세요",
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
                                containerColor = Color(0xFFFAFBFC)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 위험 수용 성향 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1).copy(alpha = 0.1f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = "위험성향",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "투자 성향",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        riskLevelOptions.forEachIndexed { index, option ->
                            ModernRadioOption(
                                text = option,
                                description = riskLevelDescriptions[index],
                                selected = selectedRiskLevel == index,
                                onClick = { selectedRiskLevel = index }
                            )

                            if (index < riskLevelOptions.size - 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 레포트 난이도 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.1f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "분석수준",
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "분석 수준",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        reportLevelOptions.forEachIndexed { index, option ->
                            ModernRadioOption(
                                text = option,
                                description = when(index) {
                                    0 -> "전문적인 금융 용어와 심층 분석"
                                    1 -> "상세한 데이터와 복합적 관점"
                                    2 -> "핵심 정보 위주의 균형잡힌 분석"
                                    3 -> "쉬운 용어로 친절한 설명"
                                    else -> ""
                                },
                                selected = selectedReportLevel == index,
                                onClick = { selectedReportLevel = index }
                            )

                            if (index < reportLevelOptions.size - 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 다음 버튼
                Button(
                    onClick = {
                        onNext(
                            name,
                            ageText.toIntOrNull() ?: 0,
                            if (selectedRiskLevel >= 0) riskLevelOptions[selectedRiskLevel] else "",
                            if (selectedReportLevel >= 0) reportLevelOptions[selectedReportLevel] else ""
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = if (name.isNotBlank() && ageText.isNotBlank() && selectedRiskLevel >= 0 && selectedReportLevel >= 0) 8.dp else 0.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.4f),
                        disabledContentColor = Color.White
                    ),
                    enabled = name.isNotBlank() && ageText.isNotBlank() && selectedRiskLevel >= 0 && selectedReportLevel >= 0
                ) {
                    Text(
                        text = "다음 단계",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernRadioOption(
    text: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(
                color = if (selected) Color(0xFFF0F4FF) else Color.Transparent
            )
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF6366F1),
                unselectedColor = Color(0xFF94A3B8)
            ),
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color(0xFF1E293B) else Color(0xFF374151)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInterestsStep(
    userData: UserData,
    onBack: () -> Unit,
    onComplete: (List<String>, List<Int>) -> Unit
) {
    var selectedInterests by remember { mutableStateOf(userData.interests) }
    var selectedDays by remember { mutableStateOf(userData.reportDays) }

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val interestOptions = listOf(
        "주식 시장", "ETF 투자", "국제 정세", "경제 지표",
        "산업 동향", "기술 동향", "암호화폐", "금융 정책"
    )

    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val notifPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                scheduleWeeklyAlarms(context, selectedDays)
                onComplete(selectedInterests, selectedDays)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        // 진행 표시
        Column(
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2/2 단계",
                    fontSize = 14.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "100%",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE2E8F0)
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
        ) {
            Column {
                // 헤더
                Text(
                    text = "관심 분야 설정",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "맞춤 리포트를 위해\n관심 분야와 알림 설정을 완료해주세요",
                    fontSize = 18.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 관심 분야 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Text(
                            text = "관심 분야 (다중 선택)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        ModernFlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalSpacing = 8.dp,
                            verticalSpacing = 12.dp
                        ) {
                            interestOptions.forEach { interest ->
                                val isSelected = selectedInterests.contains(interest)

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedInterests = if (isSelected) {
                                            selectedInterests - interest
                                        } else {
                                            selectedInterests + interest
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = interest,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF6366F1),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF1F5F9),
                                        labelColor = Color(0xFF475569)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) Color.Transparent else Color(0xFFE2E8F0),
                                        selectedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color(0xFFE2E8F0),
                                        disabledSelectedBorderColor = Color.Transparent,
                                        borderWidth = 1.dp,
                                        selectedBorderWidth = 0.dp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 알림 설정 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        Text(
                            text = "리포트 수령 요일",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            daysOfWeek.forEachIndexed { index, day ->
                                val isSelected = selectedDays.contains(index)

                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) {
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF6366F1),
                                                        Color(0xFF8B5CF6)
                                                    )
                                                )
                                            } else {
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFFF1F5F9),
                                                        Color(0xFFF1F5F9)
                                                    )
                                                )
                                            }
                                        )
                                        .border(
                                            width = if (isSelected) 0.dp else 1.dp,
                                            color = Color(0xFFE2E8F0),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedDays = if (isSelected) {
                                                selectedDays - index
                                            } else {
                                                selectedDays + index
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        color = if (isSelected) Color.White else Color(0xFF475569),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        if (selectedDays.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF6366F1).copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "선택완료",
                                        tint = Color(0xFF6366F1),
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    val daysText = selectedDays.sorted().map { daysOfWeek[it] }.joinToString(", ")
                                    Text(
                                        text = "선택된 요일: $daysText",
                                        fontSize = 14.sp,
                                        color = Color(0xFF4338CA),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6366F1)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            Color(0xFF6366F1)
                        )
                    ) {
                        Text(
                            text = "이전",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val needPermission =
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != PackageManager.PERMISSION_GRANTED

                            if (needPermission) {
                                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                scheduleWeeklyAlarms(context, selectedDays)
                                onComplete(selectedInterests, selectedDays)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(
                                elevation = if (selectedInterests.isNotEmpty() && selectedDays.isNotEmpty()) 8.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF6366F1).copy(alpha = 0.4f),
                            disabledContentColor = Color.White
                        ),
                        enabled = selectedInterests.isNotEmpty() && selectedDays.isNotEmpty()
                    ) {
                        Text(
                            "시작하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// 헬퍼 함수들
private fun scheduleWeeklyAlarms(context: Context, reportDays: List<Int>) {
    val hour = 15
    val minute = 42
    reportDays.forEach { dow ->
        NotificationHelper.schedulePeriodicWeekly(
            context = context,
            id = 2000 + dow,
            title = "주간 투자 리포트",
            body = "선택하신 관심 분야 최신 리포트를 확인하세요.",
            dayOfWeek = DayOfWeek.of(dow + 1),
            hour = hour,
            minute = minute
        )
    }
}

@Composable
fun ModernFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val itemConstraints = constraints.copy(minWidth = 0)

        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(itemConstraints)
            val itemWidthWithSpacing = placeable.width + horizontalSpacing.roundToPx()

            if (currentRow.isNotEmpty() && currentRowWidth + itemWidthWithSpacing > constraints.maxWidth) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }

            currentRow.add(placeable)
            currentRowWidth += itemWidthWithSpacing
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        val height = rows.sumOf { row ->
            (row.maxOfOrNull { it.height } ?: 0) + verticalSpacing.roundToPx()
        } - if (rows.isNotEmpty()) verticalSpacing.roundToPx() else 0

        layout(constraints.maxWidth, height.coerceAtLeast(0)) {
            var y = 0

            rows.forEach { row ->
                var x = 0
                val maxRowHeight = row.maxOfOrNull { it.height } ?: 0

                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width + horizontalSpacing.roundToPx()
                }

                y += maxRowHeight + verticalSpacing.roundToPx()
            }
        }
    }
}

// 기존 호환성을 위한 FlowRow (deprecated)
@Deprecated("Use ModernFlowRow instead")
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    maxItemsInEachRow: Int,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val itemConstraints = constraints.copy(minWidth = 0)

        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(itemConstraints)

            if (currentRow.size >= maxItemsInEachRow || currentRowWidth + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }

            currentRow.add(placeable)
            currentRowWidth += placeable.width
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        val height = rows.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 }

        layout(constraints.maxWidth, height) {
            var y = 0

            rows.forEach { row ->
                var x = 0

                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width
                }

                y += row.maxOfOrNull { it.height } ?: 0
            }
        }
    }
}

// 헬퍼 함수들
private fun getRiskLevelIndex(riskTolerance: String): Int {
    return when(riskTolerance) {
        "하이리스크 하이리턴" -> 0
        "중고위험" -> 1
        "중립" -> 2
        "중저위험" -> 3
        "로우리스크 로우리턴" -> 4
        else -> 2
    }
}

private fun getReportLevelIndex(reportComplexity: String): Int {
    return when(reportComplexity) {
        "전문가 수준의 레포트를 원함" -> 0
        "복잡한 설명도 괜찮음" -> 1
        "보통" -> 2
        "어려운 용어에 대한 친절한 설명을 원함" -> 3
        else -> 2
    }
}