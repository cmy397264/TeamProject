package com.example.businessreportgenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.businessreportgenerator.data.local.converter.ReportTypeConverter

@Entity(tableName = "reports")
@TypeConverters(ReportTypeConverter::class)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val content: String,
    val summary: String,
    val date: Long,
    val type: String
)