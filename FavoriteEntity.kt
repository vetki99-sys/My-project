package com.smartcalculator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * كيان جدول المفضلات
 * يحفظ العمليات المفضلة للمستخدم
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val calculatorType: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
