package com.smartcalculator.core

import org.mariuszgromada.math.mxparser.Expression
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * محرك الحسابات الرئيسي
 * يدعم العمليات الحسابية الأساسية والعلمية بدقة عالية
 */
@Singleton
class CalculatorEngine @Inject constructor() {

    private val mathContext = MathContext(128, RoundingMode.HALF_UP)
    private val displayPrecision = 10
    private val calculationPrecision = 32

    // أنماط الزوايا
    enum class AngleMode {
        DEGREES, RADIANS
    }

    /**
     * حساب تعبير رياضي
     */
    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEGREES): CalculationResult {
        return try {
            if (expression.isBlank()) {
                return CalculationResult.Error("Empty expression")
            }

            val processedExpression = preprocessExpression(expression, angleMode)
            val result = Expression(processedExpression).calculate()

            if (result.isNaN() || result.isInfinite()) {
                CalculationResult.Error("Invalid calculation")
            } else {
                val bigDecimal = BigDecimal(result).setScale(displayPrecision, RoundingMode.HALF_UP)
                CalculationResult.Success(formatResult(bigDecimal))
            }
        } catch (e: Exception) {
            CalculationResult.Error(e.message ?: "Calculation error")
        }
    }

    /**
     * معالجة التعبير قبل الحساب
     */
    private fun preprocessExpression(expression: String, angleMode: AngleMode): String {
        return expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("^", "^")
            .replace("π", "pi")
            .replace("e", "e")
            .replace("√", "sqrt")
            .replace("sin", if (angleMode == AngleMode.DEGREES) "sind" else "sin")
            .replace("cos", if (angleMode == AngleMode.DEGREES) "cosd" else "cos")
            .replace("tan", if (angleMode == AngleMode.DEGREES) "tand" else "tan")
    }

    /**
     * تنسيق النتيجة للعرض
     */
    private fun formatResult(value: BigDecimal): String {
        val plainString = value.toPlainString()

        // إزالة الأصفار الزائدة بعد الفاصلة
        return if (plainString.contains(".")) {
            val parts = plainString.split(".")
            val integerPart = parts[0]
            val decimalPart = parts[1].trimEnd('0')

            if (decimalPart.isEmpty()) {
                integerPart
            } else {
                "$integerPart.$decimalPart"
            }
        } else {
            plainString
        }
    }

    /**
     * حساب الدالة المثلثية
     */
    fun calculateTrigonometric(
        function: String,
        value: BigDecimal,
        angleMode: AngleMode
    ): BigDecimal {
        val radians = when (angleMode) {
            AngleMode.DEGREES -> value.toDouble() * Math.PI / 180.0
            AngleMode.RADIANS -> value.toDouble()
        }

        val result = when (function.lowercase()) {
            "sin" -> Math.sin(radians)
            "cos" -> Math.cos(radians)
            "tan" -> Math.tan(radians)
            "asin" -> Math.asin(value.toDouble())
            "acos" -> Math.acos(value.toDouble())
            "atan" -> Math.atan(value.toDouble())
            else -> throw IllegalArgumentException("Unknown function: $function")
        }

        return BigDecimal(result).setScale(displayPrecision, RoundingMode.HALF_UP)
    }

    /**
     * حساب اللوغاريتم
     */
    fun calculateLogarithm(value: BigDecimal, base: Double): BigDecimal {
        require(value > BigDecimal.ZERO) { "Logarithm undefined for non-positive numbers" }

        val result = when (base) {
            Math.E -> Math.log(value.toDouble())
            10.0 -> Math.log10(value.toDouble())
            else -> Math.log(value.toDouble()) / Math.log(base)
        }

        return BigDecimal(result).setScale(displayPrecision, RoundingMode.HALF_UP)
    }

    /**
     * حساب العاملي
     */
    fun calculateFactorial(n: Int): BigDecimal {
        require(n >= 0) { "Factorial undefined for negative numbers" }
        if (n > 17000) throw IllegalArgumentException("Number too large for factorial")

        var result = BigDecimal.ONE
        for (i in 2..n) {
            result = result.multiply(BigDecimal(i), mathContext)
        }
        return result
    }

    /**
     * حساب الأس
     */
    fun calculatePower(base: BigDecimal, exponent: BigDecimal): BigDecimal {
        return base.pow(exponent.toInt(), mathContext)
    }

    /**
     * حساب الجذر التربيعي
     */
    fun calculateSqrt(value: BigDecimal): BigDecimal {
        require(value >= BigDecimal.ZERO) { "Square root undefined for negative numbers" }
        return BigDecimal(Math.sqrt(value.toDouble())).setScale(displayPrecision, RoundingMode.HALF_UP)
    }

    /**
     * التحقق من صحة التعبير
     */
    fun isValidExpression(expression: String): Boolean {
        if (expression.isBlank()) return false

        // التحقق من الأقواس المتوازنة
        var openCount = 0
        var closeCount = 0

        for (char in expression) {
            when (char) {
                '(' -> openCount++
                ')' -> closeCount++
            }
        }

        if (openCount != closeCount) return false

        // التحقق من عدم وجود أحرف غير صالحة
        val validPattern = Regex("^[0-9+\\-*/×÷^().√%πe\\s]+$")
        return validPattern.matches(expression)
    }

    /**
     * الحصول على اقتراح للإكمال التلقائي
     */
    fun getAutoCompleteSuggestions(partial: String): List<String> {
        val commonFunctions = listOf(
            "sin(", "cos(", "tan(",
            "log(", "ln(",
            "√(", "sqrt(",
            "(", ")",
            "+", "-", "*", "/", "^", "%"
        )

        return commonFunctions.filter { it.startsWith(partial) }
    }

    /**
     * فئة النتيجة
     */
    sealed class CalculationResult {
        data class Success(val value: String) : CalculationResult()
        data class Error(val message: String) : CalculationResult()
    }
}
