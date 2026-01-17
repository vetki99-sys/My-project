package com.smartcalculator.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcalculator.core.CalculatorEngine
import com.smartcalculator.data.database.entity.HistoryEntity
import com.smartcalculator.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel للآلة الحاسبة الأساسية
 * يدير حالة الواجهة والعمليات الحسابية
 */
@HiltViewModel
class BasicCalculatorViewModel @Inject constructor(
    private val calculatorEngine: CalculatorEngine,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var currentInput = StringBuilder()
    private var lastOperation = ""

    /**
     * إضافة مدخل للمستخدم
     */
    fun appendInput(input: String) {
        // إذا كان هناك خطأ، امسحه
        if (_error.value != null) {
            _error.value = null
        }

        // إذا كانت النتيجة معروضة وأردنا بدء مدخل جديد
        if (currentInput.isNotEmpty() && isOperator(currentInput.last().toString())) {
            // لا شيء
        }

        currentInput.append(input)
        updateExpression()
        clearResult()
    }

    /**
     * إضافة عملية
     */
    fun appendOperation(operation: String) {
        if (_error.value != null) {
            _error.value = null
        }

        if (currentInput.isNotEmpty()) {
            val lastChar = currentInput.last().toString()

            // إذا كان آخر حرف عملية، استبدلها
            if (isOperator(lastChar) && operation != "(" && operation != ")") {
                currentInput.deleteCharAt(currentInput.length - 1)
            }

            currentInput.append(operation)
            updateExpression()
            clearResult()
        } else if (operation == "(") {
            currentInput.append(operation)
            updateExpression()
        }
    }

    /**
     * حذف最后一个 حرف
     */
    fun deleteLast() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            updateExpression()
            clearResult()
        }
    }

    /**
     * مسح الكل
     */
    fun clear() {
        currentInput.clear()
        _expression.value = ""
        _result.value = "0"
        _error.value = null
    }

    /**
     * حساب النتيجة
     */
    fun calculate() {
        val expr = currentInput.toString()
        if (expr.isBlank()) return

        val calculationResult = calculatorEngine.evaluate(expr)

        when (calculationResult) {
            is CalculatorEngine.CalculationResult.Success -> {
                _result.value = calculationResult.value
                saveToHistory(expr, calculationResult.value)
            }
            is CalculatorEngine.CalculationResult.Error -> {
                _error.value = calculationResult.message
                _result.value = "Error"
            }
        }
    }

    /**
     * حفظ في السجل
     */
    private fun saveToHistory(expression: String, result: String) {
        viewModelScope.launch {
            try {
                historyRepository.addToHistory(
                    expression = expression,
                    result = result,
                    calculatorType = "basic"
                )
            } catch (e: Exception) {
                // تجاهل أخطاء الحفظ
            }
        }
    }

    /**
     * تحديث العرض
     */
    private fun updateExpression() {
        _expression.value = currentInput.toString()
    }

    /**
     * مسح النتيجة
     */
    private fun clearResult() {
        if (_error.value == null) {
            _result.value = "0"
        }
    }

    /**
     * التحقق من العملية
     */
    private fun isOperator(char: String): Boolean {
        return char in "+-×÷%^"
    }

    /**
     * قلب الإشارة
     */
    fun negate() {
        if (currentInput.isNotEmpty()) {
            // البحث عن آخر رقم
            var i = currentInput.length - 1
            while (i >= 0 && !isDigit(currentInput[i].toString()) && currentInput[i] != '.') {
                i--
            }

            if (i >= 0) {
                // البحث عن بداية الرقم
                var start = i
                while (start >= 0 && (isDigit(currentInput[start].toString()) || currentInput[start] == '.')) {
                    start--
                }

                // إضافة أو إزالة علامة السالب
                if (start >= 0 && currentInput[start] == '-') {
                    currentInput.deleteCharAt(start)
                } else {
                    currentInput.insert(start + 1, "-")
                }

                updateExpression()
                clearResult()
            }
        }
    }

    /**
     * التحقق من الرقم
     */
    private fun isDigit(char: String): Boolean {
        return char.all { it.isDigit() }
    }
}
