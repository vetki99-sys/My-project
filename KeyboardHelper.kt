package com.smartcalculator.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * فئة مساعدة لإدارة لوحة المفاتيح
 */
object KeyboardHelper {

    /**
     * إخفاء لوحة المفاتيح
     */
    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: return
        hideKeyboard(activity, view)
    }

    /**
     * إخفاء لوحة المفاتيح من View
     */
    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    /**
     * إظهار لوحة المفاتيح
     */
    fun showKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: return
        showKeyboard(activity, view)
    }

    /**
     * إظهار لوحة المفاتيح في EditText
     */
    fun showKeyboard(activity: Activity, editText: EditText) {
        editText.requestFocus()
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * التبديل بين إظهار وإخفاء لوحة المفاتيح
     */
    fun toggleKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (imm.isActive) {
            hideKeyboard(activity, view)
        } else {
            showKeyboard(activity, view)
        }
    }

    /**
     * التحقق من حالة لوحة المفاتيح
     */
    fun isKeyboardVisible(activity: Activity): Boolean {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.isActive
    }
}
