package com.smartcalculator.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.smartcalculator.core.CalculatorEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// تمديد DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * مستودع الإعدادات
 * يدير إعدادات المستخدم باستخدام DataStore
 */
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    // مفاتيح الإعدادات
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val ANGLE_MODE = stringPreferencesKey("angle_mode")
        val DECIMAL_PLACES = intPreferencesKey("decimal_places")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
        val HIDE_ADS = booleanPreferencesKey("hide_ads")
        val FONT_SIZE = intPreferencesKey("font_size")
        val BUTTON_SIZE = stringPreferencesKey("button_size")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val LAST_USED_CALCULATOR = stringPreferencesKey("last_used_calculator")
    }

    /**
     * الحصول على الوضع (داكن/لايت/تلقائي)
     */
    val themeMode: Flow<ThemeMode> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeName)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }
        }

    /**
     * تعيين الوضع
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    /**
     * الحصول على اللغة
     */
    val language: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.LANGUAGE] ?: "system"
        }

    /**
     * تعيين اللغة
     */
    suspend fun setLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = languageCode
        }
    }

    /**
     * الحصول على وضع الزوايا
     */
    val angleMode: Flow<CalculatorEngine.AngleMode> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val modeName = preferences[PreferencesKeys.ANGLE_MODE] ?: CalculatorEngine.AngleMode.DEGREES.name
            try {
                CalculatorEngine.AngleMode.valueOf(modeName)
            } catch (e: Exception) {
                CalculatorEngine.AngleMode.DEGREES
            }
        }

    /**
     * تعيين وضع الزوايا
     */
    suspend fun setAngleMode(mode: CalculatorEngine.AngleMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANGLE_MODE] = mode.name
        }
    }

    /**
     * الحصول على عدد الأرقام العشرية
     */
    val decimalPlaces: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DECIMAL_PLACES] ?: 8
        }

    /**
     * تعيين عدد الأرقام العشرية
     */
    suspend fun setDecimalPlaces(places: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DECIMAL_PLACES] = places.coerceIn(0, 20)
        }
    }

    /**
     * التحقق من تفعيل الاهتزاز
     */
    val vibrationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
        }

    /**
     * تعيين تفعيل الاهتزاز
     */
    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    /**
     * التحقق من تفعيل الصوت
     */
    val soundEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] ?: false
        }

    /**
     * تعيين تفعيل الصوت
     */
    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    /**
     * التحقق من تفعيل السجل
     */
    val historyEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HISTORY_ENABLED] ?: true
        }

    /**
     * تعيين تفعيل السجل
     */
    suspend fun setHistoryEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HISTORY_ENABLED] = enabled
        }
    }

    /**
     * التحقق من إخفاء الإعلانات
     */
    val hideAds: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.HIDE_ADS] ?: false
        }

    /**
     * تعيين إخفاء الإعلانات
     */
    suspend fun setHideAds(hide: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_ADS] = hide
        }
    }

    /**
     * الحصول على حجم الخط
     */
    val fontSize: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] ?: 24
        }

    /**
     * تعيين حجم الخط
     */
    suspend fun setFontSize(size: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size.coerceIn(16, 48)
        }
    }

    /**
     * الحصول على حجم الأزرار
     */
    val buttonSize: Flow<ButtonSize> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sizeName = preferences[PreferencesKeys.BUTTON_SIZE] ?: ButtonSize.NORMAL.name
            try {
                ButtonSize.valueOf(sizeName)
            } catch (e: Exception) {
                ButtonSize.NORMAL
            }
        }

    /**
     * تعيين حجم الأزرار
     */
    suspend fun setButtonSize(size: ButtonSize) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BUTTON_SIZE] = size.name
        }
    }

    /**
     * التحقق من أول تشغيل
     */
    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] ?: true
        }

    /**
     * تعيين أول تشغيل
     */
    suspend fun setFirstLaunch(isFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] = isFirst
        }
    }

    /**
     * الحصول على آخر آلة حاسبة مستخدمة
     */
    val lastUsedCalculator: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.LAST_USED_CALCULATOR] ?: "basic"
        }

    /**
     * تعيين آخر آلة حاسبة مستخدمة
     */
    suspend fun setLastUsedCalculator(calculatorType: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_USED_CALCULATOR] = calculatorType
        }
    }

    /**
     * إعادة تعيين كل الإعدادات
     */
    suspend fun resetAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // أوضاع الثيم
    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }

    // أحجام الأزرار
    enum class ButtonSize {
        SMALL, NORMAL, LARGE
    }
}
