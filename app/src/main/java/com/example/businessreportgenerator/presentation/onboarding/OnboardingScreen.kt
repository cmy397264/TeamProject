package com.example.businessreportgenerator.presentation.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.businessreportgenerator.data.remote.model.ReportRequest
import com.example.businessreportgenerator.notification.NotificationHelper
import com.example.businessreportgenerator.presentation.features.analyst.AnalystViewmodel
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek


@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val analystViewmodel : AnalystViewmodel = koinViewModel()
    val onBoardingViewModel: OnboardingViewModel = viewModel()
    val state by onBoardingViewModel.state.collectAsState()
    val context = LocalContext.current

    when (state.currentStep) {
        0 -> BasicInfoStep(
            userData = state.userData,
            onNext = { name, age, riskTolerance, reportComplexity ->
                onBoardingViewModel.setBasicInfo(name, age, riskTolerance, reportComplexity)
                onBoardingViewModel.nextStep()
            }
        )
        1 -> InterestsStep(
            userData = state.userData,
            onBack = { onBoardingViewModel.prevStep() },
            onComplete = { interests, reportDays ->
                onBoardingViewModel.setInterestsAndDays(interests, reportDays)

                // 사용자 데이터 저장
                val sharedPrefs = context.getSharedPreferences("user_prefs", 0)
                sharedPrefs.edit {
                    // 기본 정보 저장
                    putString("user_name", state.userData.name)
                    putInt("user_age", state.userData.age)
                    putString("risk_tolerance", state.userData.riskTolerance)
                    putString("report_complexity", state.userData.reportComplexity)

                    // 관심 분야 저장
                    putStringSet("interests", interests.toSet())

                    // 레포트 수령 요일 저장
                    putString("report_days", reportDays.joinToString(","))

                    // 온보딩 완료 플래그 설정
                    putBoolean("onboarding_completed", true)
                }

                val riskTolerance = sharedPrefs.getString("risk_tolerance", null).toString()
                val reportComplexity = sharedPrefs.getString("report_complexity", null).toString()
                val interests = sharedPrefs.getStringSet("interests", null)!!.toList()

                val userReportReQuest = ReportRequest(
                    reportType = "economy",
                    stockName = null,
                    riskTolerance = riskTolerance,
                    reportDifficultyLevel = reportComplexity,
                    interestAreas = interests,
                )

                analystViewmodel.requestReport(userReportReQuest)

                // 온보딩 완료 콜백 호출
                onComplete()
            }
        )
    }
}

/**
 * 기본 정보 입력 단계
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BasicInfoStep(
    userData: UserData,
    onNext: (String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(userData.name) }
    var ageText by remember { mutableStateOf(if(userData.age > 0) userData.age.toString() else "") }
    var selectedRiskLevel by remember { mutableIntStateOf(getRiskLevelIndex(userData.riskTolerance)) }
    var selectedReportLevel by remember { mutableIntStateOf(getReportLevelIndex(userData.reportComplexity)) }

    val riskLevelOptions = listOf(
        "하이리스크 하이리턴",
        "중고위험",
        "중립",
        "중저위험",
        "로우리스크 로우리턴"
    )

    val riskLevelDescriptions = listOf(
        "수익을 위해 높은 위험을 감수할 수 있습니다.",
        "평균 이상의 위험을 감수할 수 있습니다.",
        "적절한 위험과 수익의 균형을 선호합니다.",
        "안정적인 투자를 선호하며 적은 위험을 감수합니다.",
        "안전한 투자만을 원하고 위험을 회피합니다."
    )

    val reportLevelOptions = listOf(
        "전문가 수준의 레포트를 원함",
        "복잡한 설명도 괜찮음",
        "보통",
        "어려운 용어에 대한 친절한 설명을 원함"
    )

    val reportLevelDescriptions = listOf(
        "금융 업계에서 사용되는 전문 용어와 심층 분석을 포함합니다.",
        "복잡한 금융 개념과 분석이 포함된 상세한 내용을 제공합니다.",
        "일반적인 투자자가 이해할 수 있는 수준의 내용으로 구성됩니다.",
        "금융 초보자도 이해할 수 있도록 모든 용어에 설명이 포함됩니다."
    )

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // 진행 상태 표시
            LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFF007AFF),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Text(
                text = "1/2 기본 정보 설정",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 제목 및 안내 텍스트
            Text(
                text = "기본 정보 설정",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "맞춤형 증권 리포트 서비스를 위해 필요한 정보를 입력해주세요.",
                fontSize = 16.sp,
                color = Color.Gray,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 기본 정보 입력 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // 이름 입력
                    Text(
                        text = "이름 또는 닉네임",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("이름을 입력하세요") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    // 나이 입력
                    Text(
                        text = "나이",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = {
                            // 숫자만 입력 가능하도록
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                ageText = it
                            }
                        },
                        placeholder = { Text("나이를 입력하세요") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color.LightGray
                        ),
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // 위험 수용 성향
                    Text(
                        text = "위험 수용 성향",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        riskLevelOptions.forEachIndexed { index, option ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedRiskLevel = index },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedRiskLevel == index,
                                        onClick = { selectedRiskLevel = index },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF007AFF)
                                        )
                                    )

                                    Text(
                                        text = option,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                // 선택된 경우에만 설명 표시
                                AnimatedVisibility(
                                    visible = selectedRiskLevel == index,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Text(
                                        text = riskLevelDescriptions[index],
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier
                                            .padding(start = 48.dp, top = 4.dp, bottom = 8.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // 레포트 난이도
                    Text(
                        text = "레포트 난이도 설정",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        reportLevelOptions.forEachIndexed { index, option ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedReportLevel = index },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedReportLevel == index,
                                        onClick = { selectedReportLevel = index },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF007AFF)
                                        )
                                    )

                                    Text(
                                        text = option,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                // 선택된 경우에만 설명 표시
                                AnimatedVisibility(
                                    visible = selectedReportLevel == index,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Text(
                                        text = reportLevelDescriptions[index],
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier
                                            .padding(start = 48.dp, top = 4.dp, bottom = 8.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

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
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White
                ),
                enabled = name.isNotBlank() && ageText.isNotBlank() && selectedRiskLevel >= 0 && selectedReportLevel >= 0
            ) {
                Text(
                    text = "다음",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsStep(
    userData: UserData,
    onBack: () -> Unit,
    onComplete: (List<String>, List<Int>) -> Unit
) {
    var selectedInterests by remember { mutableStateOf(userData.interests) }
    var selectedDays by remember { mutableStateOf(userData.reportDays) }

    val interestOptions = listOf(
        "주식 시장", "부동산", "국제 정세", "경제 지표",
        "산업 동향", "기술 동향", "암호화폐", "금융 정책",
        "원자재 시장", "채권 시장"
    )

    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

    val scrollState = rememberScrollState()


    val context = LocalContext.current
    // 런타임 런처 선언
    val notifPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                scheduleWeeklyAlarms(context, selectedDays)
                onComplete(selectedInterests, selectedDays)
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // 진행 상태 표시
            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFF007AFF),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Text(
                text = "2/2 관심 분야 설정",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 제목 및 안내 텍스트
            Text(
                text = "관심 분야 설정",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Text(
                text = "관심 있는 분야와 레포트를 받고 싶은 요일을 선택해주세요.",
                fontSize = 16.sp,
                color = Color.Gray,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 관심 분야 선택 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "관심 분야 (다중 선택 가능)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 관심 분야 그리드
                    FlowRow(
                        maxItemsInEachRow = 2,
                        modifier = Modifier.fillMaxWidth()
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
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier.padding(4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF007AFF).copy(alpha = 0.1f),
                                    selectedLabelColor = Color(0xFF007AFF)
                                )
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // 레포트 수령 요일
                    Text(
                        text = "레포트 수령 요일 (다중 선택 가능)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        daysOfWeek.forEachIndexed { index, day ->
                            val isSelected = selectedDays.contains(index)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            color = if (isSelected) Color(0xFF007AFF) else Color.White,
                                            shape = RoundedCornerShape(percent = 50)
                                        )
                                        .clip(CircleShape)
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
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // 선택된 요일 표시
                    if (selectedDays.isNotEmpty()) {
                        val daysText = selectedDays.sorted().map { daysOfWeek[it] }.joinToString(", ")
                        Text(
                            text = "선택된 요일: $daysText",
                            fontSize = 14.sp,
                            color = Color(0xFF007AFF),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 버튼 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 이전 버튼
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF007AFF)
                    )
                ) {
                    Text(
                        text = "이전",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
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
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        contentColor   = Color.White
                    ),
                    enabled = selectedInterests.isNotEmpty() && selectedDays.isNotEmpty()
                ) { Text("완료", fontSize = 18.sp, fontWeight = FontWeight.Medium) }
            }
        }
    }
}

// 헬퍼 함수
private fun scheduleWeeklyAlarms(context: Context, reportDays: List<Int>) {
    val hour = 15 // 시간 정하기
    val minute = 42
    reportDays.forEach { dow ->
        NotificationHelper.schedulePeriodicWeekly(
            context   = context,
            id        = 2000 + dow,
            title     = "주간 투자 리포트",
            body      = "선택하신 관심 분야 최신 리포트를 확인하세요.",
            dayOfWeek = DayOfWeek.of(dow + 1),   // 1 = 월 … 7 = 일
            hour      = hour,
            minute    = minute
        )
    }
}

// FlowRow 컴포넌트 (그리드 레이아웃)
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

// 리스크 레벨 인덱스 가져오기
private fun getRiskLevelIndex(riskTolerance: String): Int {
    return when(riskTolerance) {
        "하이리스크 하이리턴" -> 0
        "중고위험" -> 1
        "중립" -> 2
        "중저위험" -> 3
        "로우리스크 로우리턴" -> 4
        else -> 2 // 기본값은 중립
    }
}

// 리포트 난이도 인덱스 가져오기
private fun getReportLevelIndex(reportComplexity: String): Int {
    return when(reportComplexity) {
        "전문가 수준의 레포트를 원함" -> 0
        "복잡한 설명도 괜찮음" -> 1
        "보통" -> 2
        "어려운 용어에 대한 친절한 설명을 원함" -> 3
        else -> 2 // 기본값은 보통
    }
}