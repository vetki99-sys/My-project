package com.smartcalculator.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.smartcalculator.R
import com.smartcalculator.data.repository.PreferencesRepository
import com.smartcalculator.databinding.ActivityMainBinding
import com.smartcalculator.ui.adapters.CalculatorPagerAdapter
import com.smartcalculator.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * النشاط الرئيسي للتطبيق
 * يحتوي على واجهة التبويبات لجميع أنواع الآلات الحاسبة
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: CalculatorPagerAdapter

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تطبيق الثيم واللغة
        applySettings()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        setupTabs()
    }

    /**
     * تطبيق الإعدادات (الثيم واللغة)
     */
    private fun applySettings() {
        // تطبيق الثيم
        lifecycleScope.launch {
            preferencesRepository.themeMode.collect { mode ->
                val nightMode = when (mode) {
                    PreferencesRepository.ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    PreferencesRepository.ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    PreferencesRepository.ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }

        // تطبيق اللغة
        lifecycleScope.launch {
            val language = preferencesRepository.language.first()
            LocaleHelper.setLocale(this@MainActivity, language)
        }
    }

    /**
     * إعداد شريط الأدوات
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    /**
     * إعداد ViewPager للتبويبات
     */
    private fun setupViewPager() {
        pagerAdapter = CalculatorPagerAdapter(this)
        binding.viewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 3
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // حفظ آخر آلة حاسبة مستخدمة
                    val calculatorType = pagerAdapter.getCalculatorType(position)
                    lifecycleScope.launch {
                        preferencesRepository.setLastUsedCalculator(calculatorType)
                    }
                }
            })
        }
    }

    /**
     * إعداد التبويبات
     */
    private fun setupTabs() {
        val tabTitles = listOf(
            getString(R.string.tab_basic),
            getString(R.string.tab_scientific),
            getString(R.string.tab_graphing),
            getString(R.string.tab_financial),
            getString(R.string.tab_engineering),
            getString(R.string.tab_programming),
            getString(R.string.tab_statistical),
            getString(R.string.tab_time)
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles.getOrNull(position) ?: ""
            tab.setIcon(getTabIcon(position))
        }.attach()

        // تخصيص مظهر التبويبات
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * الحصول على أيقونة التبويب
     */
    private fun getTabIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_calculator
            1 -> R.drawable.ic_scientific
            2 -> R.drawable.ic_graph
            3 -> R.drawable.ic_financial
            4 -> R.drawable.ic_engineering
            5 -> R.drawable.ic_programming
            6 -> R.drawable.ic_statistical
            7 -> R.drawable.ic_time
            else -> R.drawable.ic_calculator
        }
    }

    /**
     * استئناف النشاط
     */
    override fun onResume() {
        super.onResume()
        // التحقق من الإعدادات عند الاستئناف
        applySettings()
    }
}
