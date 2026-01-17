package com.smartcalculator.core

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * المحرك الهندسي
 * يدعم حسابات الأشكال الهندسية، الأحجام، المحيطات، المساحات، وتحويلات الوحدات
 */
@Singleton
class EngineeringEngine @Inject constructor() {

    private val precision = 8
    private val roundingMode = RoundingMode.HALF_UP

    /**
     * حساب مساحة الشكل
     */
    fun calculateArea(shape: Shape, dimensions: Map<String, Double>): Double {
        return when (shape) {
            Shape.CIRCLE -> {
                val radius = dimensions["radius"] ?: 0.0
                PI * radius * radius
            }
            Shape.RECTANGLE -> {
                val length = dimensions["length"] ?: 0.0
                val width = dimensions["width"] ?: 0.0
                length * width
            }
            Shape.SQUARE -> {
                val side = dimensions["side"] ?: 0.0
                side * side
            }
            Shape.TRIANGLE -> {
                val base = dimensions["base"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                0.5 * base * height
            }
            Shape.TRIANGLE_SSS -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                val c = dimensions["c"] ?: 0.0
                val s = (a + b + c) / 2
                sqrt(s * (s - a) * (s - b) * (s - c))
            }
            Shape.PARALLELOGRAM -> {
                val base = dimensions["base"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                base * height
            }
            Shape.TRAPEZOID -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                0.5 * (a + b) * height
            }
            Shape.ELLIPSE -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                PI * a * b
            }
            Shape.SECTOR -> {
                val radius = dimensions["radius"] ?: 0.0
                val angle = dimensions["angle"] ?: 0.0
                0.5 * radius * radius * Math.toRadians(angle)
            }
            Shape.POLYGON -> {
                val sides = dimensions["sides"]?.toInt() ?: 0
                val sideLength = dimensions["sideLength"] ?: 0.0
                if (sides < 3) 0.0
                else (sides * sideLength * sideLength) / (4 * tan(PI / sides))
            }
        }
    }

    /**
     * حساب حجم الشكل
     */
    fun calculateVolume(shape: Shape3D, dimensions: Map<String, Double>): Double {
        return when (shape) {
            Shape3D.CUBE -> {
                val side = dimensions["side"] ?: 0.0
                side * side * side
            }
            Shape3D.SPHERE -> {
                val radius = dimensions["radius"] ?: 0.0
                (4.0 / 3.0) * PI * radius * radius * radius
            }
            Shape3D.CYLINDER -> {
                val radius = dimensions["radius"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                PI * radius * radius * height
            }
            Shape3D.CONE -> {
                val radius = dimensions["radius"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                (1.0 / 3.0) * PI * radius * radius * height
            }
            Shape3D.PYRAMID -> {
                val baseArea = dimensions["baseArea"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                (1.0 / 3.0) * baseArea * height
            }
            Shape3D.RECTANGULAR_PRISM -> {
                val length = dimensions["length"] ?: 0.0
                val width = dimensions["width"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                length * width * height
            }
            Shape3D.TORUS -> {
                val majorRadius = dimensions["majorRadius"] ?: 0.0
                val minorRadius = dimensions["minorRadius"] ?: 0.0
                2 * PI * PI * majorRadius * minorRadius * minorRadius
            }
            Shape3D.ELLIPSOID -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                val c = dimensions["c"] ?: 0.0
                (4.0 / 3.0) * PI * a * b * c
            }
        }
    }

    /**
     * حساب المحيط
     */
    fun calculatePerimeter(shape: Shape, dimensions: Map<String, Double>): Double {
        return when (shape) {
            Shape.CIRCLE -> {
                val radius = dimensions["radius"] ?: 0.0
                2 * PI * radius
            }
            Shape.RECTANGLE -> {
                val length = dimensions["length"] ?: 0.0
                val width = dimensions["width"] ?: 0.0
                2 * (length + width)
            }
            Shape.SQUARE -> {
                val side = dimensions["side"] ?: 0.0
                4 * side
            }
            Shape.TRIANGLE, Shape.TRIANGLE_SSS -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                val c = dimensions["c"] ?: dimensions["base"] ?: 0.0
                a + b + c
            }
            Shape.PARALLELOGRAM -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                2 * (a + b)
            }
            Shape.TRAPEZOID -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                val c = dimensions["c"] ?: 0.0
                val d = dimensions["d"] ?: 0.0
                a + b + c + d
            }
            Shape.ELLIPSE -> {
                val a = dimensions["a"] ?: 0.0
                val b = dimensions["b"] ?: 0.0
                PI * (3 * (a + b) - sqrt((3 * a + b) * (a + 3 * b)))
            }
            Shape.POLYGON -> {
                val sides = dimensions["sides"]?.toInt() ?: 0
                val sideLength = dimensions["sideLength"] ?: 0.0
                sides * sideLength
            }
        }
    }

    /**
     * حساب مساحة السطح
     */
    fun calculateSurfaceArea(shape: Shape3D, dimensions: Map<String, Double>): Double {
        return when (shape) {
            Shape3D.CUBE -> {
                val side = dimensions["side"] ?: 0.0
                6 * side * side
            }
            Shape3D.SPHERE -> {
                val radius = dimensions["radius"] ?: 0.0
                4 * PI * radius * radius
            }
            Shape3D.CYLINDER -> {
                val radius = dimensions["radius"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                2 * PI * radius * (height + radius)
            }
            Shape3D.CONE -> {
                val radius = dimensions["radius"] ?: 0.0
                val slantHeight = dimensions["slantHeight"] ?: sqrt(
                    (dimensions["height"] ?: 0.0) * (dimensions["height"] ?: 0.0) + radius * radius
                )
                PI * radius * (radius + slantHeight)
            }
            Shape3D.PYRAMID -> {
                val basePerimeter = dimensions["basePerimeter"] ?: 0.0
                val slantHeight = dimensions["slantHeight"] ?: 0.0
                val baseArea = dimensions["baseArea"] ?: 0.0
                0.5 * basePerimeter * slantHeight + baseArea
            }
            Shape3D.RECTANGULAR_PRISM -> {
                val length = dimensions["length"] ?: 0.0
                val width = dimensions["width"] ?: 0.0
                val height = dimensions["height"] ?: 0.0
                2 * (length * width + length * height + width * height)
            }
        }
    }

    /**
     * حل المثلث - قانون الجيب
     */
    fun solveTriangleSSA(
        sideA: Double,
        sideB: Double,
        angleA: Double,
        angleMode: CalculatorEngine.AngleMode
    ): TriangleSolution {
        val angleARad = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toRadians(angleA)
        } else angleA

        // قانون الجيب: a/sin(A) = b/sin(B)
        val sinB = sideB * sin(angleARad) / sideA

        if (sinB > 1) {
            return TriangleSolution(error = "No solution: side too short")
        }

        val angleBRad = asin(sinB)
        val angleCRad = PI - angleARad - angleBRad
        val angleB = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toDegrees(angleBRad)
        } else angleBRad

        val angleC = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toDegrees(angleCRad)
        } else angleCRad

        val sideC = sideA * sin(angleCRad) / sin(angleARad)

        return TriangleSolution(
            angleA = angleA,
            angleB = angleB,
            angleC = angleC,
            sideA = sideA,
            sideB = sideB,
            sideC = sideC
        )
    }

    /**
     * حل المثلث - قانون جيب التمام
     */
    fun solveTriangleSSS(
        sideA: Double,
        sideB: Double,
        sideC: Double,
        angleMode: CalculatorEngine.AngleMode
    ): TriangleSolution {
        val angleA = acos((sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC))
        val angleB = acos((sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC))
        val angleCRad = PI - angleA - angleB
        val angleC = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toDegrees(angleCRad)
        } else angleCRad

        val angleADeg = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toDegrees(angleA)
        } else angleA

        val angleBDeg = if (angleMode == CalculatorEngine.AngleMode.DEGREES) {
            Math.toDegrees(angleB)
        } else angleB

        return TriangleSolution(
            angleA = angleADeg,
            angleB = angleBDeg,
            angleC = angleC,
            sideA = sideA,
            sideB = sideB,
            sideC = sideC
        )
    }

    /**
     * تحويل الطول بين الوحدات
     */
    fun convertLength(value: Double, from: LengthUnit, to: LengthUnit): Double {
        val meters = when (from) {
            LengthUnit.METER -> value
            LengthUnit.KILOMETER -> value * 1000
            LengthUnit.CENTIMETER -> value / 100
            LengthUnit.MILLIMETER -> value / 1000
            LengthUnit.MICROMETER -> value / 1_000_000
            LengthUnit.NANOMETER -> value / 1_000_000_000
            LengthUnit.MILE -> value * 1609.344
            LengthUnit.YARD -> value * 0.9144
            LengthUnit.FOOT -> value * 0.3048
            LengthUnit.INCH -> value * 0.0254
            LengthUnit.LIGHT_YEAR -> value * 9.461e15
        }

        return when (to) {
            LengthUnit.METER -> meters
            LengthUnit.KILOMETER -> meters / 1000
            LengthUnit.CENTIMETER -> meters * 100
            LengthUnit.MILLIMETER -> meters * 1000
            LengthUnit.MICROMETER -> meters * 1_000_000
            LengthUnit.NANOMETER -> meters * 1_000_000_000
            LengthUnit.MILE -> meters / 1609.344
            LengthUnit.YARD -> meters / 0.9144
            LengthUnit.FOOT -> meters / 0.3048
            LengthUnit.INCH -> meters / 0.0254
            LengthUnit.LIGHT_YEAR -> meters / 9.461e15
        }
    }

    /**
     * تحويل الكتلة بين الوحدات
     */
    fun convertMass(value: Double, from: MassUnit, to: MassUnit): Double {
        val kilograms = when (from) {
            MassUnit.KILOGRAM -> value
            MassUnit.GRAM -> value / 1000
            MassUnit.MILLIGRAM -> value / 1_000_000
            MassUnit.METRIC_TON -> value * 1000
            MassUnit.POUND -> value * 0.453592
            MassUnit.OUNCE -> value * 0.0283495
            MassUnit.STONE -> value * 6.35029
        }

        return when (to) {
            MassUnit.KILOGRAM -> kilograms
            MassUnit.GRAM -> kilograms * 1000
            MassUnit.MILLIGRAM -> kilograms * 1_000_000
            MassUnit.METRIC_TON -> kilograms / 1000
            MassUnit.POUND -> kilograms / 0.453592
            MassUnit.OUNCE -> kilograms / 0.0283495
            MassUnit.STONE -> kilograms / 6.35029
        }
    }

    /**
     * تحويل درجة الحرارة
     */
    fun convertTemperature(value: Double, from: TemperatureUnit, to: TemperatureUnit): Double {
        val celsius = when (from) {
            TemperatureUnit.CELSIUS -> value
            TemperatureUnit.FAHRENHEIT -> (value - 32) * 5 / 9
            TemperatureUnit.KELVIN -> value - 273.15
            TemperatureUnit.RANKINE -> (value - 491.67) * 5 / 9
        }

        return when (to) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> celsius * 9 / 5 + 32
            TemperatureUnit.KELVIN -> celsius + 273.15
            TemperatureUnit.RANKINE -> (celsius + 273.15) * 9 / 5
        }
    }

    /**
     * تحويل الضغط
     */
    fun convertPressure(value: Double, from: PressureUnit, to: PressureUnit): Double {
        val pascals = when (from) {
            PressureUnit.PASCAL -> value
            PressureUnit.KILOPASCAL -> value * 1000
            PressureUnit.MEGAPASCAL -> value * 1_000_000
            PressureUnit.BAR -> value * 100000
            PressureUnit.ATMOSPHERE -> value * 101325
            PressureUnit.PSI -> value * 6894.76
            PressureUnit.TORR -> value * 133.322
        }

        return when (to) {
            PressureUnit.PASCAL -> pascals
            PressureUnit.KILOPASCAL -> pascals / 1000
            PressureUnit.MEGAPASCAL -> pascals / 1_000_000
            PressureUnit.BAR -> pascals / 100000
            PressureUnit.ATMOSPHERE -> pascals / 101325
            PressureUnit.PSI -> pascals / 6894.76
            PressureUnit.TORR -> pascals / 133.322
        }
    }

    /**
     * فئات الأشكال والأبعاد
     */
    enum class Shape {
        CIRCLE, RECTANGLE, SQUARE, TRIANGLE, TRIANGLE_SSS,
        PARALLELOGRAM, TRAPEZOID, ELLIPSE, SECTOR, POLYGON
    }

    enum class Shape3D {
        CUBE, SPHERE, CYLINDER, CONE, PYRAMID,
        RECTANGULAR_PRISM, TORUS, ELLIPSOID
    }

    enum class LengthUnit {
        METER, KILOMETER, CENTIMETER, MILLIMETER, MICROMETER, NANOMETER,
        MILE, YARD, FOOT, INCH, LIGHT_YEAR
    }

    enum class MassUnit {
        KILOGRAM, GRAM, MILLIGRAM, METRIC_TON, POUND, OUNCE, STONE
    }

    enum class TemperatureUnit {
        CELSIUS, FAHRENHEIT, KELVIN, RANKINE
    }

    enum class PressureUnit {
        PASCAL, KILOPASCAL, MEGAPASCAL, BAR, ATMOSPHERE, PSI, TORR
    }

    data class TriangleSolution(
        val angleA: Double = 0.0,
        val angleB: Double = 0.0,
        val angleC: Double = 0.0,
        val sideA: Double = 0.0,
        val sideB: Double = 0.0,
        val sideC: Double = 0.0,
        val perimeter: Double = sideA + sideB + sideC,
        val area: Double = 0.5 * sideA * sideB * sin(Math.toRadians(angleC)),
        val error: String? = null
    )
}
