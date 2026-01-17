package com.smartcalculator.core

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * المحرك المالي
 * يدعم جميع الحسابات المالية: القروض، الفوائد، الاستثمارات، الضرائب، الخصومات
 */
@Singleton
class FinancialEngine @Inject constructor() {

    private val precision = 2
    private val roundingMode = RoundingMode.HALF_UP

    /**
     * حساب القسط الشهري للقرض
     * PMT = P * (r(1+r)^n) / ((1+r)^n - 1)
     */
    fun calculateLoanPayment(
        principal: BigDecimal,
        annualInterestRate: BigDecimal,
        years: Int
    ): LoanResult {
        val monthlyRate = annualInterestRate.divide(BigDecimal(1200), precision + 4, roundingMode)
        val numberOfPayments = years * 12

        val onePlusR = BigDecimal.ONE.add(monthlyRate)
        val power = onePlusR.pow(numberOfPayments)

        val numerator = monthlyRate.multiply(power)
        val denominator = power.subtract(BigDecimal.ONE)

        val monthlyPayment = principal
            .multiply(numerator)
            .divide(denominator, precision, roundingMode)

        val totalPayment = monthlyPayment.multiply(BigDecimal(numberOfPayments))
        val totalInterest = totalPayment.subtract(principal)

        return LoanResult(
            monthlyPayment = monthlyPayment,
            totalPayment = totalPayment,
            totalInterest = totalInterest,
            numberOfPayments = numberOfPayments,
            principal = principal
        )
    }

    /**
     * جدول سداد القرض (الاستهلاك)
     */
    fun generateAmortizationSchedule(
        principal: BigDecimal,
        annualInterestRate: BigDecimal,
        years: Int
    ): List<AmortizationEntry> {
        val monthlyRate = annualInterestRate.divide(BigDecimal(1200), precision + 4, roundingMode)
        val numberOfPayments = years * 12
        val monthlyPayment = calculateLoanPayment(principal, annualInterestRate, years).monthlyPayment

        val schedule = mutableListOf<AmortizationEntry>()
        var remainingBalance = principal

        for (month in 1..numberOfPayments) {
            val interestPayment = remainingBalance.multiply(monthlyRate).setScale(precision, roundingMode)
            val principalPayment = monthlyPayment.subtract(interestPayment)
            remainingBalance = remainingBalance.subtract(principalPayment)

            if (remainingBalance < BigDecimal.ZERO) {
                remainingBalance = BigDecimal.ZERO
            }

            schedule.add(
                AmortizationEntry(
                    month = month,
                    payment = monthlyPayment,
                    principalPayment = principalPayment,
                    interestPayment = interestPayment,
                    remainingBalance = remainingBalance
                )
            )
        }

        return schedule
    }

    /**
     * حساب الفائدة المركبة
     * A = P(1 + r/n)^(nt)
     */
    fun calculateCompoundInterest(
        principal: BigDecimal,
        annualRate: BigDecimal,
        years: Int,
        compoundsPerYear: Int = 12
    ): CompoundInterestResult {
        val ratePerPeriod = annualRate.divide(BigDecimal(100 * compoundsPerYear), precision + 4, roundingMode)
        val totalPeriods = years * compoundsPerYear

        val base = BigDecimal.ONE.add(ratePerPeriod)
        val finalAmount = principal.multiply(base.pow(totalPeriods)).setScale(precision, roundingMode)
        val totalInterest = finalAmount.subtract(principal)

        return CompoundInterestResult(
            finalAmount = finalAmount,
            totalInterest = totalInterest,
            principal = principal,
            rate = annualRate,
            years = years,
            compoundsPerYear = compoundsPerYear
        )
    }

    /**
     * حساب القيمة المستقبلية
     */
    fun calculateFutureValue(
        presentValue: BigDecimal,
        annualRate: BigDecimal,
        years: Int,
        compoundsPerYear: Int = 12
    ): BigDecimal {
        val ratePerPeriod = annualRate.divide(BigDecimal(100 * compoundsPerYear), precision + 4, roundingMode)
        val totalPeriods = years * compoundsPerYear

        val base = BigDecimal.ONE.add(ratePerPeriod)
        return presentValue.multiply(base.pow(totalPeriods)).setScale(precision, roundingMode)
    }

    /**
     * حساب القيمة الحالية
     */
    fun calculatePresentValue(
        futureValue: BigDecimal,
        annualRate: BigDecimal,
        years: Int,
        compoundsPerYear: Int = 12
    ): BigDecimal {
        val ratePerPeriod = annualInterestRate.divide(BigDecimal(100 * compoundsPerYear), precision + 4, roundingMode)
        val totalPeriods = years * compoundsPerYear

        val base = BigDecimal.ONE.add(ratePerPeriod)
        return futureValue.divide(base.pow(totalPeriods), precision, roundingMode)
    }

    /**
     * حساب العائد على الاستثمار (ROI)
     */
    fun calculateROI(
        investment: BigDecimal,
        revenue: BigDecimal
    ): ROIResult {
        val profit = revenue.subtract(investment)
        val roi = profit
            .divide(investment, precision + 4, roundingMode)
            .multiply(BigDecimal(100))

        val roiPercentage = roi.setScale(2, roundingMode)

        return ROIResult(
            profit = profit,
            roiPercentage = roiPercentage,
            investment = investment,
            revenue = revenue,
            isProfitable = profit > BigDecimal.ZERO
        )
    }

    /**
     * حساب الخصم
     */
    fun calculateDiscount(
        originalPrice: BigDecimal,
        discountPercentage: BigDecimal
    ): DiscountResult {
        val discountAmount = originalPrice
            .multiply(discountPercentage)
            .divide(BigDecimal(100), precision, roundingMode)

        val finalPrice = originalPrice.subtract(discountAmount)

        return DiscountResult(
            originalPrice = originalPrice,
            discountAmount = discountAmount,
            finalPrice = finalPrice,
            discountPercentage = discountPercentage
        )
    }

    /**
     * حساب الضريبة
     */
    fun calculateTax(
        amount: BigDecimal,
        taxRate: BigDecimal
    ): TaxResult {
        val taxAmount = amount
            .multiply(taxRate)
            .divide(BigDecimal(100), precision, roundingMode)

        val totalWithTax = amount.add(taxAmount)

        return TaxResult(
            amount = amount,
            taxAmount = taxAmount,
            totalWithTax = totalWithTax,
            taxRate = taxRate
        )
    }

    /**
     * حساب هامش الربح
     */
    fun calculateProfitMargin(
        cost: BigDecimal,
        sellingPrice: BigDecimal
    ): ProfitMarginResult {
        val profit = sellingPrice.subtract(cost)
        val profitMargin = profit
            .divide(sellingPrice, precision + 4, roundingMode)
            .multiply(BigDecimal(100))

        return ProfitMarginResult(
            cost = cost,
            sellingPrice = sellingPrice,
            profit = profit,
            profitMargin = profitMargin.setScale(2, roundingMode)
        )
    }

    /**
     * حساب معدل العائد الداخلي (IRR) - تقريب
     */
    fun calculateIRR(
        cashFlows: List<BigDecimal>,
        guess: BigDecimal = BigDecimal("0.1")
    ): BigDecimal {
        // استخدام طريقة نيوتن-رافسون للتقريب
        var rate = guess

        for (iteration in 0..100) {
            val npv = calculateNPV(cashFlows, rate)
            val derivative = calculateNPVDerivative(cashFlows, rate)

            if (derivative == BigDecimal.ZERO) {
                break
            }

            val newRate = rate.subtract(npv.divide(derivative, precision + 6, roundingMode))

            if ((newRate.subtract(rate).abs() < BigDecimal("0.0001"))) {
                return newRate.multiply(BigDecimal(100)).setScale(2, roundingMode)
            }

            rate = newRate

            if (rate.abs() > BigDecimal("10")) {
                break
            }
        }

        return rate.multiply(BigDecimal(100)).setScale(2, roundingMode)
    }

    /**
     * حساب صافي القيمة الحالية (NPV)
     */
    private fun calculateNPV(cashFlows: List<BigDecimal>, rate: BigDecimal): BigDecimal {
        var npv = BigDecimal.ZERO
        val onePlusRate = BigDecimal.ONE.add(rate)

        for ((index, cashFlow) in cashFlows.withIndex()) {
            npv = npv.add(cashFlow.divide(onePlusRate.pow(index), precision + 4, roundingMode))
        }

        return npv
    }

    /**
     * حساب مشتقة NPV
     */
    private fun calculateNPVDerivative(cashFlows: List<BigDecimal>, rate: BigDecimal): BigDecimal {
        var derivative = BigDecimal.ZERO
        val onePlusRate = BigDecimal.ONE.add(rate)

        for ((index, cashFlow) in cashFlows.withIndex()) {
            if (index > 0) {
                val exponent = BigDecimal(index)
                val numerator = cashFlow.multiply(exponent).negate()
                derivative = derivative.add(numerator.divide(onePlusRate.pow(index + 1), precision + 4, roundingMode))
            }
        }

        return derivative
    }

    // فئات النتائج
    data class LoanResult(
        val monthlyPayment: BigDecimal,
        val totalPayment: BigDecimal,
        val totalInterest: BigDecimal,
        val numberOfPayments: Int,
        val principal: BigDecimal
    )

    data class AmortizationEntry(
        val month: Int,
        val payment: BigDecimal,
        val principalPayment: BigDecimal,
        val interestPayment: BigDecimal,
        val remainingBalance: BigDecimal
    )

    data class CompoundInterestResult(
        val finalAmount: BigDecimal,
        val totalInterest: BigDecimal,
        val principal: BigDecimal,
        val rate: BigDecimal,
        val years: Int,
        val compoundsPerYear: Int
    )

    data class ROIResult(
        val profit: BigDecimal,
        val roiPercentage: BigDecimal,
        val investment: BigDecimal,
        val revenue: BigDecimal,
        val isProfitable: Boolean
    )

    data class DiscountResult(
        val originalPrice: BigDecimal,
        val discountAmount: BigDecimal,
        val finalPrice: BigDecimal,
        val discountPercentage: BigDecimal
    )

    data class TaxResult(
        val amount: BigDecimal,
        val taxAmount: BigDecimal,
        val totalWithTax: BigDecimal,
        val taxRate: BigDecimal
    )

    data class ProfitMarginResult(
        val cost: BigDecimal,
        val sellingPrice: BigDecimal,
        val profit: BigDecimal,
        val profitMargin: BigDecimal
    )
}
