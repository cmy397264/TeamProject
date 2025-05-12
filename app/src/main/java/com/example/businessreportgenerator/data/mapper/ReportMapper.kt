import com.example.businessreportgenerator.data.domain.Report
import com.example.businessreportgenerator.data.local.entity.ReportEntity
import java.util.Date

fun ReportEntity.toDomain() = Report(
    id          = id ?: 0L,
    title       = title,
    description = summary,
    createdAt   = Date(date),
    type        = type                                // 그대로 enum
)

fun Report.toEntity() = ReportEntity(
    id      = if (id == 0L) null else id,
    title    = title,
    content  = description,
    summary  = description,
    date     = createdAt.time,
    type     = type                                   // 그대로 enum
)
