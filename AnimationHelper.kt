package com.smartcalculator.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.smartcalculator.R

/**
 * فئة مساعدة للرسوم المتحركة والاهتزاز
 */
object AnimationHelper {

    private const val DURATION_SHORT = 100L
    private const val DURATION_MEDIUM = 200L
    private const val DURATION_LONG = 300L

    /**
     * رسم متحرك للضغط على الزر
     */
    fun animatePress(context: Context, view: View) {
        // اهتزاز خفيف
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

        // تصغير الزر قليلاً
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.9f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = DURATION_SHORT
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * رسم متحرك للنجاح
     */
    fun animateSuccess(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.2f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = DURATION_MEDIUM
            interpolator = OvershootInterpolator()
            start()
        }
    }

    /**
     * رسم متحرك للخطأ
     */
    fun animateError(view: View) {
        val shakeX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)

        AnimatorSet().apply {
            playTogether(shakeX)
            duration = DURATION_LONG
            start()
        }
    }

    /**
     * رسم متحرك للدوران
     */
    fun animateRotation(view: View, degrees: Float = 360f) {
        val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, degrees)
        rotation.duration = DURATION_LONG
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.start()
    }

    /**
     * رسم متحرك للظهور (Fade In)
     */
    fun animateFadeIn(view: View, duration: Long = DURATION_MEDIUM) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    /**
     * رسم متحرك للاختفاء (Fade Out)
     */
    fun animateFadeOut(view: View, duration: Long = DURATION_MEDIUM) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }

    /**
     * رسم متحرك للانزلاق للداخل
     */
    fun animateSlideInFromBottom(view: View, duration: Long = DURATION_MEDIUM) {
        view.translationY = view.height.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    /**
     * رسم متحرك للانزلاق للخارج
     */
    fun animateSlideOutToBottom(view: View, duration: Long = DURATION_MEDIUM) {
        view.animate()
            .translationY(view.height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }

    /**
     * رسم متحرك للتوسع
     */
    fun animateExpand(view: View, duration: Long = DURATION_MEDIUM) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    /**
     * رسم متحرك للطي
     */
    fun animateCollapse(view: View, duration: Long = DURATION_MEDIUM) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }

    /**
     * تغيير لون الخلفية بشكل متحرك
     */
    fun animateBackgroundColorChange(
        view: View,
        fromColor: Int,
        toColor: Int,
        duration: Long = DURATION_MEDIUM
    ) {
        view.setBackgroundColor(fromColor)
        view.animate()
            .backgroundColor(toColor)
            .setDuration(duration)
            .start()
    }

    /**
     * رسم متحرك للنتيجة
     */
    fun animateResultChange(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.05f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.05f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.8f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = DURATION_SHORT
            start()
        }
    }

    /**
     * تحميل رسم متحرك من الموارد
     */
    fun loadAnimation(context: Context, animationId: Int): Animation {
        return AnimationUtils.loadAnimation(context, animationId)
    }

    /**
     * تطبيق رسم متحرك على View
     */
    fun applyAnimation(view: View, animationId: Int) {
        val animation = loadAnimation(view.context, animationId)
        view.startAnimation(animation)
    }

    /**
     * اهتزاز خفيف
     */
    fun vibrate(context: Context, view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    /**
     * اهتزاز طويل
     */
    fun vibrateLong(context: Context, view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
