package com.smartcalculator.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartcalculator.data.database.dao.HistoryDao
import com.smartcalculator.data.database.entity.FavoriteEntity
import com.smartcalculator.data.database.entity.HistoryEntity

/**
 * قاعدة بيانات التطبيق
 * تحتوي على جدولي السجل والمفضلات
 */
@Database(
    entities = [
        HistoryEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DATABASE_NAME = "smart_calculator_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * الحصول على DAO مباشرة
         */
        fun getHistoryDao(context: Context): HistoryDao {
            return getInstance(context).historyDao()
        }
    }
}
