package com.smartcalculator.data.repository

import com.smartcalculator.data.database.dao.HistoryDao
import com.smartcalculator.data.database.entity.FavoriteEntity
import com.smartcalculator.data.database.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مستودع بيانات السجل
 * يدير عمليات CRUD للسجل والمفضلات
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {

    // ==================== عمليات السجل ====================

    /**
     * الحصول على كل السجل
     */
    fun getAllHistory(): Flow<List<HistoryEntity>> {
        return historyDao.getAllHistory()
    }

    /**
     * الحصول على السجل حسب النوع
     */
    fun getHistoryByType(type: String): Flow<List<HistoryEntity>> {
        return historyDao.getHistoryByType(type)
    }

    /**
     * البحث في السجل
     */
    fun searchHistory(query: String): Flow<List<HistoryEntity>> {
        return historyDao.searchHistory(query)
    }

    /**
     * الحصول على عنصر بالمعرف
     */
    suspend fun getHistoryById(id: Long): HistoryEntity? {
        return historyDao.getHistoryById(id)
    }

    /**
     * إضافة عنصر جديد
     */
    suspend fun addToHistory(
        expression: String,
        result: String,
        calculatorType: String
    ): Long {
        val entity = HistoryEntity(
            expression = expression,
            result = result,
            calculatorType = calculatorType
        )
        return historyDao.insertHistory(entity)
    }

    /**
     * حذف عنصر
     */
    suspend fun deleteHistory(item: HistoryEntity) {
        historyDao.deleteHistory(item)
    }

    /**
     * حذف بالمعرف
     */
    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    /**
     * حذف كل السجل
     */
    suspend fun clearAllHistory() {
        historyDao.clearAllHistory()
    }

    /**
     * حذف السجل حسب النوع
     */
    suspend fun clearHistoryByType(type: String) {
        historyDao.clearHistoryByType(type)
    }

    /**
     * تحديث حالة المفضل
     */
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        historyDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * الحصول على المفضلات
     */
    fun getFavorites(): Flow<List<HistoryEntity>> {
        return historyDao.getFavorites()
    }

    /**
     * عدد العناصر
     */
    suspend fun getHistoryCount(): Int {
        return historyDao.getHistoryCount()
    }

    /**
     * حذف العناصر القديمة
     */
    suspend fun deleteOldHistory(daysOld: Int) {
        val timestamp = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
        historyDao.deleteOldHistory(timestamp)
    }

    // ==================== عمليات المفضلات ====================

    /**
     * الحصول على كل المفضلات
     */
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return historyDao.getAllFavorites()
    }

    /**
     * إضافة للمفضلات
     */
    suspend fun addToFavorites(
        expression: String,
        result: String,
        calculatorType: String,
        note: String = ""
    ): Long {
        val entity = FavoriteEntity(
            expression = expression,
            result = result,
            calculatorType = calculatorType,
            note = note
        )
        return historyDao.insertFavorite(entity)
    }

    /**
     * نقل من السجل إلى المفضلات
     */
    suspend fun moveToFavorites(historyItem: HistoryEntity, note: String = "") {
        historyDao.moveToFavorites(historyItem, note)
    }

    /**
     * حذف من المفضلات
     */
    suspend fun deleteFavorite(item: FavoriteEntity) {
        historyDao.deleteFavorite(item)
    }

    /**
     * حذف من المفضلات بالمعرف
     */
    suspend fun deleteFavoriteById(id: Long) {
        historyDao.deleteFavoriteById(id)
    }

    /**
     * تحديث الملاحظة
     */
    suspend fun updateFavoriteNote(id: Long, note: String) {
        historyDao.updateFavoriteNote(id, note)
    }

    /**
     * البحث في المفضلات
     */
    fun searchFavorites(query: String): Flow<List<FavoriteEntity>> {
        return historyDao.searchFavorites(query)
    }
}
