package com.bigPicture.businessreportgenerator.data.local.converter

import androidx.room.TypeConverter
import com.bigPicture.businessreportgenerator.data.domain.ReportType

class ReportTypeConverter {

    @TypeConverter
    fun fromReportType(type: ReportType): String = type.name   // enum→String

    @TypeConverter
    fun toReportType(value: String): ReportType = ReportType.valueOf(value) // String→enum
}
