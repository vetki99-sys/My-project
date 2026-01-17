package com.smartcalculator.data.database.dao

import androidx.room.*
import com.smartcalculator.data.database.entity.FavoriteEntity
import com.smartcalculator.data.database.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * واجهة الوصول إلى بيانات السجل
 * تدعم جميع عمليات CRUD للسجل والمفضلات
 */
@Dao
interface HistoryDao {

    // ==================== عمليات السجل ====================

    /**
     * الحصول على كل السجل مرتباً حسب الوقت (الأحدث أولاً)
     */
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    /**
     * الحصول على السجل حسب النوع
     */
    @Query("SELECT * FROM history WHERE calculatorType = :type ORDER BY timestamp DESC")
    fun getHistoryByType(type: String): Flow<List<HistoryEntity>>

    /**
     * الحصول على عنصر سجل بواسطة المعرف
     */
    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Long): HistoryEntity?

    /**
     * البحث في السجل
     */
    @Query("SELECT * FROM history WHERE expression LIKE '%' || :query || '%' OR result LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    /**
     * إضافة عنصر جديد للسجل
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity): Long

    /**
     * حذف عنصر من السجل
     */
    @Delete
    suspend fun deleteHistory(item: HistoryEntity)

    /**
     * حذف عنصر بالمعرف
     */
    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    /**
     * حذف كل السجل
     */
    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    /**
     * حذف السجل حسب النوع
     */
    @Query("DELETE FROM history WHERE calculatorType = :type")
    suspend fun clearHistoryByType(type: String)

    /**
     * تحديث حالة المفضل
     */
    @Query("UPDATE history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    /**
     * الحصول على المفضلات فقط
     */
    @Query("SELECT * FROM history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<HistoryEntity>>

    /**
     * عدد العناصر في السجل
     */
    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int

    /**
     * حذف العناصر الأقدم من تاريخ معين
     */
    @Query("DELETE FROM history WHERE timestamp < :timestamp")
    suspend fun deleteOldHistory(timestamp: Long)

    // ==================== عمليات المفضلات ====================

    /**
     * الحصول على كل المفضلات
     */
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    /**
     * إضافة للمفضلات
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteEntity): Long

    /**
     * حذف من المفضلات
     */
    @Delete
    suspend fun deleteFavorite(item: FavoriteEntity)

    /**
     * حذف بالمعرف
     */
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Long)

    /**
     * تحديث الملاحظة
     */
    @Query("UPDATE favorites SET note = :note WHERE id = :id")
    suspend fun updateFavoriteNote(id: Long, note: String)

    /**
     * البحث في المفضلات
     */
    @Query("SELECT * FROM favorites WHERE expression LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchFavorites(query: String): Flow<List<FavoriteEntity>>

    /**
     * نقل من السجل إلى المفضلات
     */
    @Transaction
    suspend fun moveToFavorites(historyItem: HistoryEntity, note: String = "") {
        val favorite = FavoriteEntity(
            expression = historyItem.expression,
            result = historyItem.result,
            calculatorType = historyItem.calculatorType,
            note = note
        )
        insertFavorite(favorite)
        deleteHistory(historyItem)
    }
}
