package com.smartcalculator.core

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * المحرك الإحصائي
 * يدعم جميع الحسابات الإحصائية: المتوسط، الوسيط، الانحراف المعياري، التباين، والمدى
 */
@Singleton
class StatisticalEngine @Inject constructor() {

    private val precision = 8
    private val roundingMode = RoundingMode.HALF_UP

    /**
     * حساب المتوسط الحسابي
     */
    fun calculateMean(data: List<Double>): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO
        return BigDecimal(data.average()).setScale(precision, roundingMode)
    }

    /**
     * حساب الوسيط
     */
    fun calculateMedian(data: List<Double>): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO

        val sorted = data.sorted()
        val n = sorted.size

        return if (n % 2 == 0) {
            val mid1 = sorted[n / 2 - 1]
            val mid2 = sorted[n / 2]
            BigDecimal((mid1 + mid2) / 2).setScale(precision, roundingMode)
        } else {
            BigDecimal(sorted[n / 2]).setScale(precision, roundingMode)
        }
    }

    /**
     * حساب المنوال
     */
    fun calculateMode(data: List<Double>): List<BigDecimal> {
        if (data.isEmpty()) return emptyList()

        val frequency = mutableMapOf<Double, Int>()
        for (value in data) {
            frequency[value] = frequency.getOrDefault(value, 0) + 1
        }

        val maxFrequency = frequency.values.maxOrNull() ?: 0
        if (maxFrequency == 1) return emptyList() // لا يوجد تكرار

        return frequency.filter { it.value == maxFrequency }
            .keys
            .sorted()
            .map { BigDecimal(it).setScale(precision, roundingMode) }
    }

    /**
     * حساب المتوسط الهندسي
     */
    fun calculateGeometricMean(data: List<Double>): BigDecimal {
        if (data.isEmpty() || data.any { it <= 0 }) return BigDecimal.ZERO

        val product = data.fold(1.0) { acc, d -> acc * d }
        val nthRoot = data.size.toDouble()
        val result = product.pow(1.0 / nthRoot)

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب المتوسط التوافقي
     */
    fun calculateHarmonicMean(data: List<Double>): BigDecimal {
        if (data.isEmpty() || data.any { it <= 0 }) return BigDecimal.ZERO

        val sumOfReciprocals = data.fold(0.0) { acc, d -> acc + (1.0 / d) }
        val result = data.size / sumOfReciprocals

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب التربيعي المتوسط (RMS)
     */
    fun calculateRootMeanSquare(data: List<Double>): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO

        val sumOfSquares = data.fold(0.0) { acc, d -> acc + d * d }
        val result = sqrt(sumOfSquares / data.size)

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب المدى
     */
    fun calculateRange(data: List<Double>): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO

        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 0.0

        return BigDecimal(max - min).setScale(precision, roundingMode)
    }

    /**
     * حساب التباين (عينة)
     */
    fun calculateVariance(data: List<Double>, sample: Boolean = true): BigDecimal {
        if (data.size < 2) return BigDecimal.ZERO

        val mean = data.average()
        val squaredDiffs = data.map { (it - mean) * (it - mean) }
        val divisor = if (sample) data.size - 1 else data.size

        return BigDecimal(squaredDiffs.average()).setScale(precision, roundingMode)
    }

    /**
     * حساب الانحراف المعياري (عينة)
     */
    fun calculateStandardDeviation(data: List<Double>, sample: Boolean = true): BigDecimal {
        val variance = calculateVariance(data, sample)
        return BigDecimal(sqrt(variance.toDouble())).setScale(precision, roundingMode)
    }

    /**
     * حساب الانحراف المعياري للمجتمع
     */
    fun calculatePopulationStandardDeviation(data: List<Double>): BigDecimal {
        return calculateStandardDeviation(data, sample = false)
    }

    /**
     * حساب معامل الاختلاف (CV)
     */
    fun calculateCoefficientOfVariation(data: List<Double>): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO

        val mean = data.average()
        if (mean == 0.0) return BigDecimal.ZERO

        val stdDev = calculateStandardDeviation(data).toDouble()
        val cv = (stdDev / mean) * 100

        return BigDecimal(cv).setScale(precision, roundingMode)
    }

    /**
     * حساب الرُّبع الأول (Q1)
     */
    fun calculateFirstQuartile(data: List<Double>): BigDecimal {
        return calculatePercentile(data, 25.0)
    }

    /**
     * حساب الرُّبع الثالث (Q3)
     */
    fun calculateThirdQuartile(data: List<Double>): BigDecimal {
        return calculatePercentile(data, 75.0)
    }

    /**
     * حساب الرُّبيع الثاني (الوسيط)
     */
    fun calculateSecondQuartile(data: List<Double>): BigDecimal {
        return calculateMedian(data)
    }

    /**
     * حساب أي رُّبيع أو percent
     */
    fun calculatePercentile(data: List<Double>, percentile: Double): BigDecimal {
        if (data.isEmpty()) return BigDecimal.ZERO

        val sorted = data.sorted()
        val index = (percentile / 100.0) * (sorted.size - 1)

        val lower = sorted[index.toInt()]
        val upper = sorted[minOf(index.toInt() + 1, sorted.size - 1)]
        val weight = index - index.toInt()

        val result = lower + weight * (upper - lower)

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب النطاق الرُّبيعي (IQR)
     */
    fun calculateInterquartileRange(data: List<Double>): BigDecimal {
        val q1 = calculateFirstQuartile(data)
        val q3 = calculateThirdQuartile(data)

        return q3.subtract(q1)
    }

    /**
     * حساب معامل الالتواء (Skewness)
     */
    fun calculateSkewness(data: List<Double>): BigDecimal {
        if (data.size < 3) return BigDecimal.ZERO

        val mean = data.average()
        val n = data.size
        val stdDev = calculateStandardDeviation(data).toDouble()

        if (stdDev == 0.0) return BigDecimal.ZERO

        val sumCubedDiffs = data.fold(0.0) { acc, d ->
            acc + Math.pow((d - mean) / stdDev, 3.0)
        }

        val result = (n.toDouble() / ((n - 1) * (n - 2))) * sumCubedDiffs

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب معامل التفرطح (Kurtosis)
     */
    fun calculateKurtosis(data: List<Double>): BigDecimal {
        if (data.size < 4) return BigDecimal.ZERO

        val mean = data.average()
        val n = data.size
        val stdDev = calculateStandardDeviation(data).toDouble()

        if (stdDev == 0.0) return BigDecimal.ZERO

        val sumFourthDiffs = data.fold(0.0) { acc, d ->
            acc + Math.pow((d - mean) / stdDev, 4.0)
        }

        val result = ((n * (n + 1)) / ((n - 1) * (n - 2) * (n - 3))) * sumFourthDiffs -
                (3 * Math.pow(n - 1, 2.0)) / ((n - 2) * (n - 3))

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب معامل الارتباط (Pearson)
     */
    fun calculateCorrelation(x: List<Double>, y: List<Double>): BigDecimal {
        if (x.size != y.size || x.size < 2) return BigDecimal.ZERO

        val n = x.size
        val meanX = x.average()
        val meanY = y.average()

        var numerator = 0.0
        var denomX = 0.0
        var denomY = 0.0

        for (i in 0 until n) {
            val diffX = x[i] - meanX
            val diffY = y[i] - meanY
            numerator += diffX * diffY
            denomX += diffX * diffX
            denomY += diffY * diffY
        }

        if (denomX == 0.0 || denomY == 0.0) return BigDecimal.ZERO

        val result = numerator / sqrt(denomX * denomY)

        return BigDecimal(result).setScale(precision, roundingMode)
    }

    /**
     * حساب التباين المشترك (Covariance)
     */
    fun calculateCovariance(x: List<Double>, y: List<Double>, sample: Boolean = true): BigDecimal {
        if (x.size != y.size || x.size < 2) return BigDecimal.ZERO

        val meanX = x.average()
        val meanY = y.average()
        val n = if (sample) x.size - 1 else x.size

        val sumProductDiffs = x.zip(y).fold(0.0) { acc, (xi, yi) ->
            acc + (xi - meanX) * (yi - meanY)
        }

        return BigDecimal(sumProductDiffs / n).setScale(precision, roundingMode)
    }

    /**
     * حساب الانحدار الخطي
     */
    fun calculateLinearRegression(x: List<Double>, y: List<Double>): LinearRegressionResult {
        if (x.size != y.size || x.size < 2) {
            return LinearRegressionResult.Error("Insufficient data")
        }

        val n = x.size
        val meanX = x.average()
        val meanY = y.average()

        var slopeNumerator = 0.0
        var slopeDenominator = 0.0

        for (i in 0 until n) {
            slopeNumerator += (x[i] - meanX) * (y[i] - meanY)
            slopeDenominator += (x[i] - meanX) * (x[i] - meanX)
        }

        if (slopeDenominator == 0.0) {
            return LinearRegressionResult.Error("Vertical line")
        }

        val slope = slopeNumerator / slopeDenominator
        val intercept = meanY - slope * meanX

        // حساب R²
        val ssTotal = y.fold(0.0) { acc, yi -> acc + (yi - meanY) * (yi - meanY) }
        val ssResidual = x.zip(y).fold(0.0) { acc, (xi, yi) ->
            val predicted = slope * xi + intercept
            acc + (yi - predicted) * (yi - predicted)
        }
        val rSquared = if (ssTotal != 0.0) 1 - (ssResidual / ssTotal) else 0.0

        return LinearRegressionResult.Success(
            slope = BigDecimal(slope).setScale(precision, roundingMode),
            intercept = BigDecimal(intercept).setScale(precision, roundingMode),
            rSquared = BigDecimal(rSquared).setScale(precision, roundingMode),
            correlation = calculateCorrelation(x, y),
            equation = "y = ${slope}x + ${intercept}"
        )
    }

    /**
     * حساب الانحرافات من المتوسط
     */
    fun calculateDeviations(data: List<Double>): List<Double> {
        if (data.isEmpty()) return emptyList()

        val mean = data.average()
        return data.map { it - mean }
    }

    /**
     * حساب الدرجات المعيارية (Z-scores)
     */
    fun calculateZScores(data: List<Double>): List<Double> {
        if (data.isEmpty()) return emptyList()

        val mean = data.average()
        val stdDev = calculateStandardDeviation(data).toDouble()

        if (stdDev == 0.0) return data.map { 0.0 }

        return data.map { (it - mean) / stdDev }
    }

    /**
     * تحليل البيانات بالكامل وإرجاع ملخص
     */
    fun analyzeData(data: List<Double>): StatisticalSummary {
        if (data.isEmpty()) {
            return StatisticalSummary(
                count = 0,
                error = "Empty data set"
            )
        }

        val mean = calculateMean(data)
        val median = calculateMedian(data)
        val mode = calculateMode(data)
        val range = calculateRange(data)
        val variance = calculateVariance(data)
        val stdDev = calculateStandardDeviation(data)
        val min = BigDecimal(data.minOrNull() ?: 0.0).setScale(precision, roundingMode)
        val max = BigDecimal(data.maxOrNull() ?: 0.0).setScale(precision, roundingMode)
        val sum = BigDecimal(data.sum()).setScale(precision, roundingMode)

        return StatisticalSummary(
            count = data.size,
            sum = sum,
            mean = mean,
            median = median,
            mode = mode,
            min = min,
            max = max,
            range = range,
            variance = variance,
            standardDeviation = stdDev,
            coefficientOfVariation = calculateCoefficientOfVariation(data),
            firstQuartile = calculateFirstQuartile(data),
            thirdQuartile = calculateThirdQuartile(data),
            interquartileRange = calculateInterquartileRange(data),
            skewness = calculateSkewness(data),
            kurtosis = calculateKurtosis(data),
            error = null
        )
    }

    /**
     * ملخص البيانات الإحصائية
     */
    data class StatisticalSummary(
        val count: Int,
        val sum: BigDecimal,
        val mean: BigDecimal,
        val median: BigDecimal,
        val mode: List<BigDecimal>,
        val min: BigDecimal,
        val max: BigDecimal,
        val range: BigDecimal,
        val variance: BigDecimal,
        val standardDeviation: BigDecimal,
        val coefficientOfVariation: BigDecimal,
        val firstQuartile: BigDecimal,
        val thirdQuartile: BigDecimal,
        val interquartileRange: BigDecimal,
        val skewness: BigDecimal,
        val kurtosis: BigDecimal,
        val error: String?
    )

    /**
     * نتيجة الانحدار الخطي
     */
    sealed class LinearRegressionResult {
        data class Success(
            val slope: BigDecimal,
            val intercept: BigDecimal,
            val rSquared: BigDecimal,
            val correlation: BigDecimal,
            val equation: String
        ) : LinearRegressionResult()
        data class Error(val message: String) : LinearRegressionResult()
    }
}
