package com.smartcalculator.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.smartcalculator.ui.fragments.*

/**
 * محول ViewPager للآلات الحاسبة
 * يدير التبديل بين أنواع الآلات الحاسبة المختلفة
 */
class CalculatorPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val CALCULATOR_BASIC = "basic"
        const val CALCULATOR_SCIENTIFIC = "scientific"
        const val CALCULATOR_GRAPHING = "graphing"
        const val CALCULATOR_FINANCIAL = "financial"
        const val CALCULATOR_ENGINEERING = "engineering"
        const val CALCULATOR_PROGRAMMING = "programming"
        const val CALCULATOR_STATISTICAL = "statistical"
        const val CALCULATOR_TIME = "time"

        private val calculatorTypes = listOf(
            CALCULATOR_BASIC,
            CALCULATOR_SCIENTIFIC,
            CALCULATOR_GRAPHING,
            CALCULATOR_FINANCIAL,
            CALCULATOR_ENGINEERING,
            CALCULATOR_PROGRAMMING,
            CALCULATOR_STATISTICAL,
            CALCULATOR_TIME
        )
    }

    override fun getItemCount(): Int = calculatorTypes.size

    override fun createFragment(position: Int): Fragment {
        return when (calculatorTypes[position]) {
            CALCULATOR_BASIC -> BasicFragment.newInstance()
            CALCULATOR_SCIENTIFIC -> ScientificFragment.newInstance()
            CALCULATOR_GRAPHING -> GraphingFragment.newInstance()
            CALCULATOR_FINANCIAL -> FinancialFragment.newInstance()
            CALCULATOR_ENGINEERING -> EngineeringFragment.newInstance()
            CALCULATOR_PROGRAMMING -> ProgrammingFragment.newInstance()
            CALCULATOR_STATISTICAL -> StatisticalFragment.newInstance()
            CALCULATOR_TIME -> TimeFragment.newInstance()
            else -> BasicFragment.newInstance()
        }
    }

    /**
     * الحصول على نوع الآلة الحاسبة بالموقع
     */
    fun getCalculatorType(position: Int): String {
        return calculatorTypes.getOrElse(position) { CALCULATOR_BASIC }
    }

    /**
     * الحصول على موقع نوع الآلة الحاسبة
     */
    fun getPosition(calculatorType: String): Int {
        return calculatorTypes.indexOf(calculatorType)
    }
}
