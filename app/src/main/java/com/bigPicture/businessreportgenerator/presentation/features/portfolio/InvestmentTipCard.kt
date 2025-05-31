import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class InvestmentTip(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val gradient: Brush
)

val InvestmentTips = listOf(
    InvestmentTip(
        title = "분산 투자의 힘",
        description = "여러 종목에 분산 투자하여 리스크를 줄이고 안정적인 수익을 추구하세요.",
        icon = Icons.Rounded.Star,
        iconColor = Color.White,
        gradient = Brush.linearGradient(
            listOf(
                Color(0xFF667eea),
                Color(0xFF764ba2)
            )
        )
    ),
    InvestmentTip(
        title = "장기 투자 전략",
        description = "시간의 힘을 활용한 장기 투자로 복리 효과를 누려보세요.",
        icon = Icons.Rounded.Edit,
        iconColor = Color.White,
        gradient = Brush.linearGradient(
            listOf(
                Color(0xFF667eea),
                Color(0xFF764ba2)
            )
        )
    )
)