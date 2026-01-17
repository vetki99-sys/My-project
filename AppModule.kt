package com.smartcalculator.di

import android.content.Context
import com.smartcalculator.core.CalculatorEngine
import com.smartcalculator.core.FinancialEngine
import com.smartcalculator.core.GraphEngine
import com.smartcalculator.core.EngineeringEngine
import com.smartcalculator.core.ProgrammingEngine
import com.smartcalculator.core.StatisticalEngine
import com.smartcalculator.core.TimeEngine
import com.smartcalculator.data.database.AppDatabase
import com.smartcalculator.data.database.dao.HistoryDao
import com.smartcalculator.data.repository.HistoryRepository
import com.smartcalculator.data.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * وحدة حقن التبعيات
 * توفر جميع التبعيات اللازمة للتطبيق
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ==================== المحركات ====================

    @Provides
    @Singleton
    fun provideCalculatorEngine(): CalculatorEngine {
        return CalculatorEngine()
    }

    @Provides
    @Singleton
    fun provideFinancialEngine(): FinancialEngine {
        return FinancialEngine()
    }

    @Provides
    @Singleton
    fun provideGraphEngine(): GraphEngine {
        return GraphEngine()
    }

    @Provides
    @Singleton
    fun provideEngineeringEngine(): EngineeringEngine {
        return EngineeringEngine()
    }

    @Provides
    @Singleton
    fun provideProgrammingEngine(): ProgrammingEngine {
        return ProgrammingEngine()
    }

    @Provides
    @Singleton
    fun provideStatisticalEngine(): StatisticalEngine {
        return StatisticalEngine()
    }

    @Provides
    @Singleton
    fun provideTimeEngine(): TimeEngine {
        return TimeEngine()
    }

    // ==================== قاعدة البيانات ====================

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }

    // ==================== المستودعات ====================

    @Provides
    @Singleton
    fun provideHistoryRepository(historyDao: HistoryDao): HistoryRepository {
        return HistoryRepository(historyDao)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }
}
