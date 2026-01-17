package com.smartcalculator.core

import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * المحرك البرمجي
 * يدعم التحويل بين الأنظمة العددية والعمليات على البتات
 */
@Singleton
class ProgrammingEngine @Inject constructor() {

    /**
     * التحويل بين الأنظمة العددية
     */
    fun convertNumber(
        value: String,
        fromBase: NumericBase,
        toBase: NumericBase
    ): ConversionResult {
        return try {
            // التحقق من صحة القيمة
            if (!isValidNumber(value, fromBase)) {
                return ConversionResult.Error("Invalid number for base ${fromBase.displayName}")
            }

            // تحويل إلى عشري أولاً
            val decimalValue = toDecimal(value, fromBase)

            // تحويل من العشري إلى القاعدة المطلوبة
            val result = fromDecimal(decimalValue, toBase)

            ConversionResult.Success(
                value = result,
                fromBase = fromBase,
                toBase = toBase,
                decimalValue = decimalValue.toString()
            )
        } catch (e: Exception) {
            ConversionResult.Error(e.message ?: "Conversion error")
        }
    }

    /**
     * تحويل إلى عشري
     */
    private fun toDecimal(value: String, fromBase: NumericBase): Long {
        return when (fromBase) {
            NumericBase.BINARY -> value.toLong(2)
            NumericBase.OCTAL -> value.toLong(8)
            NumericBase.DECIMAL -> value.toLong(10)
            NumericBase.HEXADECIMAL -> value.toLong(16)
        }
    }

    /**
     * تحويل من عشري
     */
    private fun fromDecimal(value: Long, toBase: NumericBase): String {
        return when (toBase) {
            NumericBase.BINARY -> value.toString(2)
            NumericBase.OCTAL -> value.toString(8)
            NumericBase.DECIMAL -> value.toString(10)
            NumericBase.HEXADECIMAL -> value.toString(16).uppercase()
        }
    }

    /**
     * تحويل إلى جميع الأنظمة دفعة واحدة
     */
    fun convertToAllBases(value: String, base: NumericBase): AllBasesResult {
        if (!isValidNumber(value, base)) {
            return AllBasesResult.Error("Invalid number")
        }

        val decimal = toDecimal(value, base)

        return AllBasesResult.Success(
            binary = decimal.toString(2),
            octal = decimal.toString(8),
            decimal = decimal.toString(10),
            hexadecimal = decimal.toString(16).uppercase()
        )
    }

    /**
     * العمليات المنطقية الثنائية
     */
    fun binaryOperation(
        a: String,
        b: String,
        operation: BitwiseOperation
    ): BitwiseResult {
        return try {
            val aLong = a.toLong(2)
            val bLong = b.toLong(2)

            val result = when (operation) {
                BitwiseOperation.AND -> aLong and bLong
                BitwiseOperation.OR -> aLong or bLong
                BitwiseOperation.XOR -> aLong xor bLong
                BitwiseOperation.NAND -> aLong.inv() and bLong
                BitwiseOperation.NOR -> aLong.inv() or bLong
                BitwiseOperation.XNOR -> aLong.xor(bLong).inv()
            }

            BitwiseResult.Success(
                result = result.toString(2),
                operation = operation,
                a = a,
                b = b,
                decimalResult = result
            )
        } catch (e: Exception) {
            BitwiseResult.Error(e.message ?: "Operation error")
        }
    }

    /**
     * العمليات الأحادية (NOT, الإزاحات)
     */
    fun unaryOperation(
        value: String,
        operation: UnaryOperation,
        bitWidth: Int = 32
    ): UnaryResult {
        return try {
            var valueLong = value.toLong(2)

            // تطبيق الإزاحة مع الحفاظ على البتات
            val mask = (1L shl bitWidth) - 1
            valueLong = valueLong and mask

            val result = when (operation) {
                UnaryOperation.NOT -> valueLong.inv() and mask
                UnaryOperation.LEFT_SHIFT -> (valueLong shl 1) and mask
                UnaryOperation.RIGHT_SHIFT_LOGICAL -> (valueLong ushr 1) and mask
                UnaryOperation.RIGHT_SHIFT_ARITHMETIC -> {
                    if (valueLong and (1L shl (bitWidth - 1)) != 0L) {
                        ((valueLong shr 1) or (1L shl (bitWidth - 1))) and mask
                    } else {
                        valueLong shr 1
                    }
                }
                UnaryOperation.ROTATE_LEFT -> {
                    val msb = (valueLong and (1L shl (bitWidth - 1))) != 0
                    var rotated = (valueLong shl 1) and mask
                    if (msb) rotated = rotated or 1L
                    rotated
                }
                UnaryOperation.ROTATE_RIGHT -> {
                    val lsb = valueLong and 1L
                    var rotated = valueLong shr 1
                    if (lsb != 0L) rotated = rotated or (1L shl (bitWidth - 1))
                    rotated
                }
            }

            UnaryResult.Success(
                result = result.toString(2).padStart(bitWidth, '0'),
                operation = operation,
                original = value,
                decimalResult = result,
                bitWidth = bitWidth
            )
        } catch (e: Exception) {
            UnaryResult.Error(e.message ?: "Operation error")
        }
    }

    /**
     * العمليات الحسابية في أنظمة مختلفة
     */
    fun arithmeticOperation(
        a: String,
        b: String,
        base: NumericBase,
        operation: ArithmeticOperation
    ): ArithmeticResult {
        return try {
            val aLong = when (base) {
                NumericBase.BINARY -> a.toLong(2)
                NumericBase.OCTAL -> a.toLong(8)
                NumericBase.DECIMAL -> a.toLong(10)
                NumericBase.HEXADECIMAL -> a.toLong(16)
            }

            val bLong = when (base) {
                NumericBase.BINARY -> b.toLong(2)
                NumericBase.OCTAL -> b.toLong(8)
                NumericBase.DECIMAL -> b.toLong(10)
                NumericBase.HEXADECIMAL -> b.toLong(16)
            }

            val result = when (operation) {
                ArithmeticOperation.ADD -> aLong + bLong
                ArithmeticOperation.SUBTRACT -> aLong - bLong
                ArithmeticOperation.MULTIPLY -> aLong * bLong
                ArithmeticOperation.DIVIDE -> if (bLong != 0L) aLong / bLong else throw Exception("Division by zero")
                ArithmeticOperation.MODULUS -> if (bLong != 0L) aLong % bLong else throw Exception("Modulo by zero")
            }

            ArithmeticResult.Success(
                result = when (base) {
                    NumericBase.BINARY -> result.toString(2)
                    NumericBase.OCTAL -> result.toString(8)
                    NumericBase.DECIMAL -> result.toString(10)
                    NumericBase.HEXADECIMAL -> result.toString(16).uppercase()
                },
                operation = operation,
                a = a,
                b = b,
                decimalResult = result
            )
        } catch (e: Exception) {
            ArithmeticResult.Error(e.message ?: "Operation error")
        }
    }

    /**
     * التحقق من صحة الرقم في قاعدة معينة
     */
    fun isValidNumber(value: String, base: NumericBase): Boolean {
        return try {
            when (base) {
                NumericBase.BINARY -> value.all { it == '0' || it == '1' }
                NumericBase.OCTAL -> value.all { it in '0'..'7' }
                NumericBase.DECIMAL -> value.all { it in '0'..'9' }
                NumericBase.HEXADECIMAL -> value.all {
                    it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f'
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * حساب الأعداد الصحيحة من خيارات بت محددة
     */
    fun calculateFromBits(setBits: List<Int>): Long {
        var result = 0L
        for (bit in setBits) {
            if (bit >= 0) {
                result = result or (1L shl bit)
            }
        }
        return result
    }

    /**
     * الحصول على بتات العدد
     */
    fun getBits(value: Long, bitWidth: Int = 32): List<Boolean> {
        val bits = mutableListOf<Boolean>()
        for (i in 0 until bitWidth) {
            bits.add((value and (1L shl i)) != 0L)
        }
        return bits
    }

    /**
     * حساب المتمم الثنائي
     */
    fun twosComplement(value: String, bitWidth: Int): String {
        val valueLong = value.toLong(2)
        val mask = (1L shl bitWidth) - 1
        val complement = (valueLong.inv() + 1) and mask
        return complement.toString(2).padStart(bitWidth, '0')
    }

    /**
     * تحويل من متمم ثنائي إلى عدد صحيح
     */
    fun fromTwosComplement(value: String): Long {
        val valueLong = value.toLong(2)
        val bitWidth = value.length

        // إذا كان البت الأعلى 1، فالعدد سالب
        return if ((valueLong and (1L shl (bitWidth - 1))) != 0L) {
            valueLong - (1L shl bitWidth)
        } else {
            valueLong
        }
    }

    /**
     * فئات النتائج
     */
    sealed class ConversionResult {
        data class Success(
            val value: String,
            val fromBase: NumericBase,
            val toBase: NumericBase,
            val decimalValue: String
        ) : ConversionResult()
        data class Error(val message: String) : ConversionResult()
    }

    data class AllBasesResult(
        val binary: String,
        val octal: String,
        val decimal: String,
        val hexadecimal: String
    ) {
        sealed class Result {
            data class Success(val result: AllBasesResult) : Result()
            data class Error(val message: String) : Result()
        }
    }

    sealed class BitwiseResult {
        data class Success(
            val result: String,
            val operation: BitwiseOperation,
            val a: String,
            val b: String,
            val decimalResult: Long
        ) : BitwiseResult()
        data class Error(val message: String) : BitwiseResult()
    }

    sealed class UnaryResult {
        data class Success(
            val result: String,
            val operation: UnaryOperation,
            val original: String,
            val decimalResult: Long,
            val bitWidth: Int
        ) : UnaryResult()
        data class Error(val message: String) : UnaryResult()
    }

    sealed class ArithmeticResult {
        data class Success(
            val result: String,
            val operation: ArithmeticOperation,
            val a: String,
            val b: String,
            val decimalResult: Long
        ) : ArithmeticResult()
        data class Error(val message: String) : ArithmeticResult()
    }

    /**
     * الأنظمة العددية
     */
    enum class NumericBase(val displayName: String, val prefix: String) {
        BINARY("Binary", "0b"),
        OCTAL("Octal", "0o"),
        DECIMAL("Decimal", ""),
        HEXADECIMAL("Hexadecimal", "0x")
    }

    /**
     * العمليات على البتات
     */
    enum class BitwiseOperation(val symbol: String, val displayName: String) {
        AND("&", "AND"),
        OR("|", "OR"),
        XOR("^", "XOR"),
        NAND("↑", "NAND"),
        NOR("↓", "NOR"),
        XNOR("⊙", "XNOR")
    }

    /**
     * العمليات الأحادية
     */
    enum class UnaryOperation(val symbol: String, val displayName: String) {
        NOT("~", "NOT"),
        LEFT_SHIFT("<<", "Left Shift"),
        RIGHT_SHIFT_LOGICAL(">>", "Logical Right Shift"),
        RIGHT_SHIFT_ARITHMETIC(">>>", "Arithmetic Right Shift"),
        ROTATE_LEFT("RL", "Rotate Left"),
        ROTATE_RIGHT("RR", "Rotate Right")
    }

    /**
     * العمليات الحسابية
     */
    enum class ArithmeticOperation(val symbol: String, val displayName: String) {
        ADD("+", "Add"),
        SUBTRACT("-", "Subtract"),
        MULTIPLY("×", "Multiply"),
        DIVIDE("÷", "Divide"),
        MODULUS("%", "Modulo")
    }
}
