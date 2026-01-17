package com.smartcalculator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * الفئة الرئيسية للتطبيق
 * تهيئة Hilt وإعداد الإشعارات
 */
@HiltAndroidApp
class SmartCalculatorApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // قناة للإشعارات العادية
        val generalChannel = NotificationChannel(
            CHANNEL_GENERAL,
            getString(R.string.channel_general_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.channel_general_description)
            enableLights(false)
            enableVibration(false)
        }

        // قناة للإعلانات
        val adsChannel = NotificationChannel(
            CHANNEL_ADS,
            getString(R.string.channel_ads_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.channel_ads_description)
            setSound(null, null)
        }

        notificationManager.createNotificationChannels(listOf(generalChannel, adsChannel))
    }

    companion object {
        const val CHANNEL_GENERAL = "general_channel"
        const val CHANNEL_ADS = "ads_channel"
    }
}
