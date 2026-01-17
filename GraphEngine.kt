package com.smartcalculator.core

import org.mariuszgromada.math.mxparser.Expression
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * محرك الرسم البياني
 * يدعم رسم الدوال الرياضية، إيجاد الجذور والتقاطعات
 */
@Singleton
class GraphEngine @Inject constructor() {

    private val precision = 1e-10
    private val defaultStep = 0.01

    /**
     * تقييم الدالة عند نقطة معينة
     */
    fun evaluateFunction(
        expression: String,
        x: Double,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): Double {
        return try {
            val processedExpr = preprocessExpression(expression, angleMode)
            val exprWithX = processedExpr.replace("x", "($x)")
            val result = Expression(exprWithX).calculate()

            if (result.isNaN() || result.isInfinite()) {
                Double.NaN
            } else {
                result
            }
        } catch (e: Exception) {
            Double.NaN
        }
    }

    /**
     * إنشاء نقاط للرسم البياني
     */
    fun generatePlotPoints(
        expression: String,
        startX: Double,
        endX: Double,
        step: Double = defaultStep,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): List<GraphPoint> {
        val points = mutableListOf<GraphPoint>()
        var x = startX

        while (x <= endX) {
            val y = evaluateFunction(expression, x, angleMode)
            if (!y.isNaN() && y.isFinite() && abs(y) < 1e10) {
                points.add(GraphPoint(x, y))
            }
            x += step
        }

        return points
    }

    /**
     * إيجاد الجذور باستخدام طريقة التنصيف
     */
    fun findRoots(
        expression: String,
        startX: Double,
        endX: Double,
        step: Double = defaultStep,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): List<Double> {
        val roots = mutableListOf<Double>()
        var x = startX

        while (x < endX) {
            val y1 = evaluateFunction(expression, x, angleMode)
            val y2 = evaluateFunction(expression, x + step, angleMode)

            if (!y1.isNaN() && !y2.isNaN()) {
                // إذا تغيرت الإشارة، يوجد جذر
                if (y1 * y2 <= 0) {
                    val root = bisectionMethod(expression, x, x + step, angleMode)
                    if (!root.isNaN() && !roots.contains(root)) {
                        roots.add(root)
                    }
                }
            }

            x += step
        }

        return roots.distinct().sorted()
    }

    /**
     * إيجاد نقاط التقاطع بين دالتين
     */
    fun findIntersections(
        expr1: String,
        expr2: String,
        startX: Double,
        endX: Double,
        step: Double = defaultStep,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): List<GraphPoint> {
        val intersections = mutableListOf<GraphPoint>()
        var x = startX

        while (x < endX) {
            val y1 = evaluateFunction(expr1, x, angleMode)
            val y2 = evaluateFunction(expr2, x, angleMode)

            if (!y1.isNaN() && !y2.isNaN()) {
                val diff = abs(y1 - y2)
                if (diff < 0.01) {
                    // التحقق من التقاطع الحقيقي
                    val y1Next = evaluateFunction(expr1, x + step, angleMode)
                    val y2Next = evaluateFunction(expr2, x + step, angleMode)

                    if (!y1Next.isNaN() && !y2Next.isNaN()) {
                        if ((y1 - y2) * (y1Next - y2Next) <= 0) {
                            val avgY = (y1 + y2) / 2
                            intersections.add(GraphPoint(x, avgY))
                        }
                    }
                }
            }

            x += step
        }

        return intersections
    }

    /**
     * حساب المشتقة численно
     */
    fun calculateDerivative(
        expression: String,
        x: Double,
        h: Double = 0.0001,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): Double {
        val f1 = evaluateFunction(expression, x - h, angleMode)
        val f2 = evaluateFunction(expression, x + h, angleMode)

        if (f1.isNaN() || f2.isNaN()) {
            return Double.NaN
        }

        return (f2 - f1) / (2 * h)
    }

    /**
     * حساب التكامل عددياً (طريقة شبه المنحرف)
     */
    def calculateIntegral(
        expression: String,
        startX: Double,
        endX: Double,
        partitions: Int = 1000,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): Double {
        if (startX >= endX) return Double.NaN

        val step = (endX - startX) / partitions
        var integral = 0.0

        for (i in 0 until partitions) {
            val x1 = startX + i * step
            val x2 = startX + (i + 1) * step

            val y1 = evaluateFunction(expression, x1, angleMode)
            val y2 = evaluateFunction(expression, x2, angleMode)

            if (!y1.isNaN() && !y2.isNaN()) {
                integral += (y1 + y2) / 2 * step
            }
        }

        return integral
    }

    /**
     * إيجاد القيمة القصوى في فترة
     */
    fun findExtrema(
        expression: String,
        startX: Double,
        endX: Double,
        step: Double = defaultStep,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): List<ExtremaPoint> {
        val extrema = mutableListOf<ExtremaPoint>()
        var x = startX

        while (x < endX) {
            val y = evaluateFunction(expression, x, angleMode)
            val derivative = calculateDerivative(expression, x, step / 10, angleMode)
            val nextDerivative = calculateDerivative(expression, x + step, step / 10, angleMode)

            if (!derivative.isNaN() && !nextDerivative.isNaN()) {
                // تغيير في إشارة المشتقة
                if (derivative * nextDerivative <= 0) {
                    val refinedX = refineExtrema(expression, x, x + step, angleMode)
                    val refinedY = evaluateFunction(expression, refinedX, angleMode)

                    if (!refinedY.isNaN()) {
                        val type = if (derivative > 0 && nextDerivative < 0) {
                            ExtremaType.MAXIMUM
                        } else {
                            ExtremaType.MINIMUM
                        }

                        if (!extrema.any { abs(it.x - refinedX) < 0.01 }) {
                            extrema.add(ExtremaPoint(refinedX, refinedY, type))
                        }
                    }
                }
            }

            x += step
        }

        return extrema
    }

    /**
     * تحسين تحديد القيم القصوى
     */
    private fun refineExtrema(
        expression: String,
        a: Double,
        b: Double,
        angleMode: CalculatorEngine.AngleMode
    ): Double {
        var left = a
        var right = b

        for (i in 0..50) {
            val mid = (left + right) / 2
            val derivative = calculateDerivative(expression, mid, 1e-6, angleMode)

            if (derivative.abs() < precision) {
                return mid
            }

            if (derivative > 0) {
                left = mid
            } else {
                right = mid
            }
        }

        return (left + right) / 2
    }

    /**
     * حساب القيم عند نقاط معينة
     */
    fun evaluateAtPoints(
        expression: String,
        xValues: List<Double>,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): Map<Double, Double> {
        return xValues.associateWith { x ->
            evaluateFunction(expression, x, angleMode)
        }
    }

    /**
     * تحويل التعبير لتنسيق mXparser
     */
    private fun preprocessExpression(
        expression: String,
        angleMode: CalculatorEngine.AngleMode
    ): String {
        return expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("^", "^")
            .replace("π", "pi")
            .replace("e", "e")
            .replace("√", "sqrt")
            .replace("sin", if (angleMode == CalculatorEngine.AngleMode.DEGREES) "sind" else "sin")
            .replace("cos", if (angleMode == CalculatorEngine.AngleMode.DEGREES) "cosd" else "cos")
            .replace("tan", if (angleMode == CalculatorEngine.AngleMode.DEGREES) "tand" else "tan")
            .replace("log", "log10")
            .replace("ln", "log")
    }

    /**
     * طريقة التنصيف لإيجاد الجذر
     */
    private fun bisectionMethod(
        expression: String,
        a: Double,
        b: Double,
        angleMode: CalculatorEngine.AngleMode
    ): Double {
        var left = a
        var right = b

        for (i in 0..100) {
            val mid = (left + right) / 2
            val y = evaluateFunction(expression, mid, angleMode)

            if (y.abs() < precision || (right - left) / 2 < precision) {
                return mid
            }

            val yLeft = evaluateFunction(expression, left, angleMode)

            if (yLeft * y <= 0) {
                right = mid
            } else {
                left = mid
            }
        }

        return Double.NaN
    }

    /**
     * تقدير الحد من الرسم البياني
     */
    fun estimateBounds(
        expression: String,
        startX: Double,
        endX: Double,
        angleMode: CalculatorEngine.AngleMode = CalculatorEngine.AngleMode.DEGREES
    ): GraphBounds {
        val points = generatePlotPoints(expression, startX, endX, 0.1, angleMode)

        if (points.isEmpty()) {
            return GraphBounds(-10.0, 10.0, -10.0, 10.0)
        }

        val minY = points.minOfOrNull { it.y } ?: -10.0
        val maxY = points.maxOfOrNull { it.y } ?: 10.0

        // إضافة هامش
        val yRange = maxY - minY
        val yPadding = if (yRange > 0) yRange * 0.1 else 10.0

        return GraphBounds(
            minX = startX,
            maxX = endX,
            minY = minY - yPadding,
            maxY = maxY + yPadding
        )
    }

    /**
     * فئة نقطة الرسم البياني
     */
    data class GraphPoint(val x: Double, val y: Double)

    /**
     * فئة حدود الرسم البياني
     */
    data class GraphBounds(
        val minX: Double,
        val maxX: Double,
        val minY: Double,
        val maxY: Double
    )

    /**
     * فئة القيم القصوى
     */
    data class ExtremaPoint(
        val x: Double,
        val y: Double,
        val type: ExtremaType
    )

    /**
     * نوع القيمة القصوى
     */
    enum class ExtremaType {
        MAXIMUM, MINIMUM
    }
}
