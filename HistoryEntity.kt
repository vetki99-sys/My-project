package com.smartcalculator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * كيان جدول سجل العمليات
 * يحفظ تاريخ جميع العمليات الحسابية
 */
@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val calculatorType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
