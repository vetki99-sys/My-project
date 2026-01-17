package com.smartcalculator.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartcalculator.ui.activities.MainActivity

/**
 * مستقبل إعادة تشغيل الجهاز
 * لتحديث الويدجت عند إعادة التشغيل
 */
class WidgetBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // فتح التطبيق عند إعادة التشغيل
            val launchIntent = Intent(context, MainActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
    }
}
