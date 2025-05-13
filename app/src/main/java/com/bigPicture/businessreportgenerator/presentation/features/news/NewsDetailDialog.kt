package com.bigPicture.businessreportgenerator.presentation.features.news

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 뉴스 상세 대화상자
 */
@Composable
fun NewsDetailDialog(
    newsItem: NewsItem,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { it / 2 },
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF8F9FA)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 닫기 버튼
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Color.Gray
                            )
                        }

                        // 카테고리
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .background(
                                    color = newsItem.category.getColor().copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = newsItem.category.getDisplayName(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = newsItem.category.getColor()
                            )
                        }

                        // 제목
                        Text(
                            text = newsItem.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // 출처 및 날짜
                        val formatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
                        Text(
                            text = "${newsItem.source} | ${formatter.format(newsItem.publishedAt)}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // 구분선
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        // 내용 (실제로는 더 긴 내용이겠지만 요약으로 대체)
                        Text(
                            text = generateDetailedContent(newsItem),
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * 상세 내용 생성 (더미 데이터)
 */
private fun generateDetailedContent(newsItem: NewsItem): String {
    // 실제로는 API에서 가져온 상세 내용을 사용할 것
    val baseSummary = newsItem.summary

    // 카테고리별 추가 내용
    val additionalContent = when (newsItem.category) {
        NewsCategory.ECONOMY -> """
            이러한 경제 동향은 글로벌 시장에도 영향을 미칠 것으로 예상됩니다. 전문가들은 향후 3개월 내에 경기 회복세가 더욱 뚜렷해질 것으로 전망하고 있습니다.
            
            또한 소비자 물가 상승률이 안정화되면서 가계 소비도 점차 회복될 것으로 예상됩니다. 정부는 이를 뒷받침하기 위한 추가 경기 부양책을 검토 중입니다.
            
            한국은행 관계자는 "현재의 경제 상황을 면밀히 모니터링하며 적절한 통화정책을 시행할 것"이라고 밝혔습니다.
        """.trimIndent()

        NewsCategory.STOCK_MARKET -> """
            주요 증권사들은 이번 실적 개선이 일시적인 현상이 아닌 구조적 회복의 신호라고 평가했습니다. 특히 반도체와 AI 관련 사업 부문이 향후 성장 동력이 될 것으로 전망됩니다.
            
            투자자들은 2분기 실적 발표를 앞두고 주가가 추가 상승할 가능성에 주목하고 있습니다. 외국인 투자자들의 매수세도 점차 강화되는 추세입니다.
            
            업계 관계자는 "글로벌 공급망 문제가 해소되면서 생산 정상화가 이루어지고 있다"며 "하반기에는 더욱 개선된 실적을 기대할 수 있다"고 말했습니다.
        """.trimIndent()

        NewsCategory.REAL_ESTATE -> """
            부동산 시장 전문가들은 이번 거래량 증가가 새로운 상승세의 시작일 수 있다고 조심스럽게 전망했습니다. 다만, 금리 인상과 경기 침체 우려 등 변수도 여전히 존재합니다.
            
            특히 서울 강남권과 재건축 예정 지역을 중심으로 거래가 활발했으며, 중소형 아파트의 가격 상승폭이 상대적으로 컸습니다.
            
            한국부동산원 관계자는 "규제 완화 효과가 점차 시장에 반영되고 있다"며 "하반기에는 더 많은 매수자들이 시장에 진입할 것으로 예상된다"고 밝혔습니다.
        """.trimIndent()

        NewsCategory.CRYPTO -> """
            가상자산 시장 전문가들은 이번 조정을 건전한 시장 형성 과정으로 평가하고 있습니다. 단기적인 변동성에도 불구하고 기관 투자자들의 관심은 여전히 높은 상황입니다.
            
            특히 비트코인 ETF 출시 이후 자금 유입이 꾸준히 이어지고 있으며, 이는 장기적인 가격 상승 요인으로 작용할 것으로 보입니다.
            
            암호화폐 분석가는 "단기적인 조정 이후 연내 10만 달러 돌파도 가능하다"며 "다만 변동성이 큰 만큼 리스크 관리가 중요하다"고 조언했습니다.
        """.trimIndent()

        NewsCategory.INTERNATIONAL -> """
            중국 정부는 경제 활성화를 위해 추가적인 재정 정책을 준비 중이며, 특히 내수 시장 강화에 초점을 맞출 것으로 알려졌습니다.
            
            미중 무역 갈등과 글로벌 공급망 재편 등의 불확실성에도 불구하고, 중국 경제의 기초 체력은 여전히 견고하다는 평가가 나오고 있습니다.
            
            국제 금융기관들은 "중국의 경제 회복세가 예상보다 빠르게 진행되고 있다"며 "글로벌 경제에 긍정적인 영향을 미칠 것"이라고 전망했습니다.
        """.trimIndent()

        NewsCategory.POLICY -> """
            연준 의장은 기준금리 동결 가능성을 시사하면서도 경제 데이터에 따라 정책 방향이 달라질 수 있다고 언급했습니다. 시장은 연준의 금리 인하가 올해 하반기에 시작될 것으로 예상하고 있습니다.
            
            인플레이션이 목표치에 근접함에 따라 금리 인하에 대한 기대감이 높아지고 있으며, 이는 주식 시장에 긍정적인 요인으로 작용할 것으로 보입니다.
            
            경제 전문가는 "연준의 통화정책 전환 시기가 다가오고 있다"며 "다만 고용 시장과 인플레이션 데이터를 주시해야 한다"고 조언했습니다.
        """.trimIndent()
    }

    return "$baseSummary\n\n$additionalContent"
}