package com.example.businessreportgenerator.presentation.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 사용자 데이터 모델
data class UserData(
    val name: String = "",
    val age: Int = 0,
    val interests: List<String> = emptyList(),
    val riskTolerance: String = "",
    val reportComplexity: String = "",
    val reportDays: List<Int> = emptyList()
)

// 관심 분야 항목
val interestOptions = listOf(
    "주식 시장", "부동산", "국제 정세", "경제 지표", "산업 동향",
    "기술 동향", "암호화폐", "금융 정책", "원자재 시장", "채권 시장"
)

// 위험 성향 항목
val riskToleranceOptions = listOf(
    "하이리스크 하이리턴", "중고위험", "중립", "중저위험", "로우리스크 로우리턴"
)

// 레포트 난이도 항목
val reportComplexityOptions = listOf(
    "전문가 수준의 레포트를 원함",
    "복잡한 설명도 괜찮음",
    "보통",
    "어려운 용어에 대한 친절한 설명을 원함"
)

// 요일 항목
val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")

@Composable
fun OnboardingScreen(
    onOnboardingComplete: (UserData) -> Unit
) {
    val viewModel : OnboardingViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val currentStep = state.currentStep
    val userData = state.userData

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Scaffold(
            bottomBar = {
                OnboardingBottomBar(
                    currentStep = currentStep,
                    onNextClick = {
                        if (currentStep < 5) {
                            viewModel.increaseCurrentStep()
                        } else {
                            onOnboardingComplete(userData)
                        }
                    },
                    onBackClick = {
                        if (currentStep > 0) {
                            viewModel.decreaseCurrentStep()
                        }
                    },
                    isNextEnabled = viewModel.isStepVaild()
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 진행 표시기
                OnboardingProgressIndicator(
                    totalSteps = 6,
                    currentStep = currentStep
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 현재 단계에 따른 화면 표시
                when (currentStep) {
                    0 -> NameInputStep(
                        name = userData.name,
                        onNameChange = { viewModel.setUserDataName(it) }
                    )
                    1 -> AgeInputStep(
                        age = if (userData.age > 0) userData.age.toString() else "",
                        onAgeChange = { viewModel.setUserDataAge(it.toIntOrNull() ?: 0) }
                    )
                    2 -> InterestsSelectionStep(
                        selectedInterests = userData.interests,
                        onInterestsChange = { viewModel.setUserDataInterests(it) }
                    )
                    3 -> RiskToleranceStep(
                        selectedRiskTolerance = userData.riskTolerance,
                        onRiskToleranceChange = { viewModel.setUserDataRiskTolerance(it) }
                    )
                    4 -> ReportComplexityStep(
                        selectedComplexity = userData.reportComplexity,
                        onComplexityChange = { viewModel.setUserDataReportComplexity(it) }
                    )
                    5 -> ReportDaysStep(
                        selectedDays = userData.reportDays,
                        onDaysChange = { viewModel.setUserDataReportDays(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingProgressIndicator(
    totalSteps: Int,
    currentStep: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0 until totalSteps) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .background(
                        color = if (i <= currentStep) Color(0xFF007AFF) else Color(0xFFDDDDDD),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun OnboardingBottomBar(
    currentStep: Int,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    isNextEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 0) {
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("이전")
                }
            } else {
                Spacer(modifier = Modifier.width(64.dp))
            }

            Button(
                onClick = onNextClick,
                enabled = isNextEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF007AFF).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(if (currentStep == 5) "완료" else "다음")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (currentStep == 5) Icons.Default.CheckCircle else Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputStep(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "이름을 알려주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "맞춤형 보고서를 위해 필요해요",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("이름 또는 닉네임") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007AFF),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = Color(0xFF007AFF)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeInputStep(
    age: String,
    onAgeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "나이를 알려주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "연령대에 맞는 정보를 제공해 드립니다",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = age,
            onValueChange = {
                // 숫자만 입력 가능하도록
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    onAgeChange(it)
                }
            },
            label = { Text("나이") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF007AFF),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = Color(0xFF007AFF)
            )
        )
    }
}

@Composable
fun InterestsSelectionStep(
    selectedInterests: List<String>,
    onInterestsChange: (List<String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "관심 분야를 선택해주세요",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "여러 항목을 선택할 수 있습니다",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 관심 분야 선택 그리드
        for (interest in interestOptions) {
            val isSelected = selectedInterests.contains(interest)
            InterestItem(
                interest = interest,
                isSelected = isSelected,
                onToggle = {
                    val newList = if (isSelected) {
                        selectedInterests.filter { it != interest }
                    } else {
                        selectedInterests + interest
                    }
                    onInterestsChange(newList)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InterestItem(
    interest: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE6F2FF) else Color.White,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 0.dp else 2.dp
        ),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF),
                    uncheckedColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = interest,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskToleranceStep(
    selectedRiskTolerance: String,
    onRiskToleranceChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "위험 수용 성향",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "투자 스타일에 맞는 정보를 제공해 드립니다",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 드롭다운 메뉴
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedRiskTolerance,
                onValueChange = {},
                readOnly = true,
                label = { Text("위험 수용 성향 선택") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    cursorColor = Color(0xFF007AFF)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "드롭다운",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )

            // 투명한 클릭 영역
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .background(Color.Transparent)
                    .padding(8.dp)
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                riskToleranceOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onRiskToleranceChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 선택된 위험 수용 성향에 대한 설명
        if (selectedRiskTolerance.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE6F2FF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = selectedRiskTolerance,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (selectedRiskTolerance) {
                            "하이리스크 하이리턴" -> "높은 위험을 감수하고 높은 수익을 추구하는 투자 스타일"
                            "중고위험" -> "평균 이상의 위험을 감수하고 상당한 수익을 추구하는 투자 스타일"
                            "중립" -> "적절한 위험과 수익의 균형을 추구하는 투자 스타일"
                            "중저위험" -> "약간 낮은 위험을 선호하며 안정적인 수익을 추구하는 투자 스타일"
                            "로우리스크 로우리턴" -> "낮은 위험으로 안정적이지만 상대적으로 낮은 수익을 추구하는 투자 스타일"
                            else -> ""
                        },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportComplexityStep(
    selectedComplexity: String,
    onComplexityChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "레포트 난이도 설정",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "원하시는 설명의 수준을 선택해주세요",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 드롭다운 메뉴
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedComplexity,
                onValueChange = {},
                readOnly = true,
                label = { Text("난이도 선택") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    cursorColor = Color(0xFF007AFF)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "드롭다운",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            )

            // 투명한 클릭 영역
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .background(Color.Transparent)
                    .padding(8.dp)
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                reportComplexityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onComplexityChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 선택된 난이도에 대한 설명
        if (selectedComplexity.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE6F2FF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = selectedComplexity,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (selectedComplexity) {
                            "전문가 수준의 레포트를 원함" -> "금융 전문가 수준의 용어와 심층적인 분석이 포함된 레포트"
                            "복잡한 설명도 괜찮음" -> "전문적인 용어와 상세한 설명이 포함된 레포트"
                            "보통" -> "일반인도 이해할 수 있는 수준의 설명이 포함된 레포트"
                            "어려운 용어에 대한 친절한 설명을 원함" -> "쉬운 용어와 상세한 부가 설명이 포함된 레포트"
                            else -> ""
                        },
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun ReportDaysStep(
    selectedDays: List<Int>,
    onDaysChange: (List<Int>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "레포트 수령 요일",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "레포트를 받고 싶은 요일을 선택해주세요",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 요일 선택 그리드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in 0..6) {
                        DayButton(
                            day = daysOfWeek[i],
                            isSelected = selectedDays.contains(i),
                            onToggle = {
                                val newList = if (selectedDays.contains(i)) {
                                    selectedDays.filter { it != i }
                                } else {
                                    selectedDays + i
                                }
                                onDaysChange(newList)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 선택된 요일 텍스트
        if (selectedDays.isNotEmpty()) {
            val daysText = selectedDays.sorted().map { daysOfWeek[it] }.joinToString(", ")
            Text(
                text = "선택된 요일: $daysText",
                fontSize = 16.sp,
                color = Color(0xFF007AFF),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun DayButton(
    day: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = if (isSelected) Color(0xFF007AFF) else Color.White,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}