package com.smartcalculator.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

/**
 * فئة مساعدة لإدارة اللغة
 * تدعم التبديل بين اللغات المختلفة بما في ذلك RTL
 */
object LocaleHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "language"

    /**
     * الحصول على اللغة الحالية
     */
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "system") ?: "system"
    }

    /**
     * تعيين اللغة
     */
    fun setLocale(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        if (languageCode == "system") {
            // استخدام لغة النظام
            return
        }

        updateResources(context, languageCode)
    }

    /**
     * تحديث موارد التطبيق
     */
    private fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        configuration.setLocale(locale)
        configuration.layoutDirection = locale

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    /**
     * التحقق من دعم RTL
     */
    fun isRTLLanguage(languageCode: String): Boolean {
        val locale = if (languageCode == "system") {
            Locale.getDefault()
        } else {
            Locale(languageCode)
        }

        return isRTLLocale(locale)
    }

    /**
     * التحقق من لغة RTL
     */
    fun isRTLLocale(locale: Locale): Boolean {
        val direction = Character.getDirectionality(locale.displayName[0].codePointAt(0))
        return direction == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                direction == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
    }

    /**
     * الحصول على قائمة اللغات المدعومة
     */
    fun getSupportedLanguages(): List<LanguageOption> {
        return listOf(
            LanguageOption("system", "System Default", "افتراضي النظام"),
            LanguageOption("en", "English", "الإنجليزية"),
            LanguageOption("ar", "العربية", "العربية"),
            LanguageOption("zh", "中文", "الصينية"),
            LanguageOption("zh-rTW", "繁體中文", "الصينية التقليدية"),
            LanguageOption("ja", "日本語", "اليابانية"),
            LanguageOption("ko", "한국어", "الكورية"),
            LanguageOption("fr", "Français", "الفرنسية"),
            LanguageOption("de", "Deutsch", "الألمانية"),
            LanguageOption("es", "Español", "الإسبانية"),
            LanguageOption("pt", "Português", "البرتغالية"),
            LanguageOption("ru", "Русский", "الروسية"),
            LanguageOption("hi", "हिन्दी", "الهندية"),
            LanguageOption("tr", "Türkçe", "التركية"),
            LanguageOption("vi", "Tiếng Việt", "الفيتنامية"),
            LanguageOption("th", "ไทย", "التايلاندية"),
            LanguageOption("it", "Italiano", "الإيطالية"),
            LanguageOption("nl", "Nederlands", "الهولندية"),
            LanguageOption("pl", "Polski", "البولندية"),
            LanguageOption("uk", "Українська", "الأوكرانية"),
            LanguageOption("cs", "Čeština", "التشيكية"),
            LanguageOption("ro", "Română", "الرومانية"),
            LanguageOption("hu", "Magyar", "المجرية"),
            LanguageOption("el", "Ελληνικά", "اليونانية"),
            LanguageOption("he", "עברית", "العبرية"),
            LanguageOption("fa", "فارسی", "الفارسية"),
            LanguageOption("ur", "اردو", "الأردية"),
            LanguageOption("bn", "বাংলা", "البنغالية"),
            LanguageOption("ms", "Bahasa Melayu", "الماليزية"),
            LanguageOption("id", "Bahasa Indonesia", "الإندونيسية"),
            LanguageOption("fi", "Suomi", "الفنلندية"),
            LanguageOption("da", "Dansk", "الدنماركية"),
            LanguageOption("no", "Norsk", "النرويجية"),
            LanguageOption("sv", "Svenska", "السويدية"),
            LanguageOption("bg", "Български", "البلغارية"),
            LanguageOption("hr", "Hrvatski", "الكرواتية"),
            LanguageOption("sk", "Slovenčina", "السلوفاكية"),
            LanguageOption("sl", "Slovenščina", "السلوفينية"),
            LanguageOption("sr", "Срpski", "الصربية"),
            LanguageOption("lt", "Lietuvių", "الليتوانية"),
            LanguageOption("lv", "Latviešu", "اللاتفية"),
            LanguageOption("et", "Eesti", "الإستونية"),
            LanguageOption("ca", "Català", "الكاتالونية")
        )
    }

    /**
     * فئة خيار اللغة
     */
    data class LanguageOption(
        val code: String,
        val nameEn: String,
        val nameLocalized: String
    ) {
        val displayName: String
            get() = nameEn
    }
}
