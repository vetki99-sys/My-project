package com.smartcalculator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.smartcalculator.R
import com.smartcalculator.ui.activities.MainActivity

/**
 * مزود الويدجت للآلة الحاسبة
 * يعرض ويدجت بسيط على الشاشة الرئيسية
 */
class CalculatorWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_calculator)

            // إعداد نية لفتح التطبيق عند النقر
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            views.setOnClickPendingIntent(R.id.widget_display, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_buttons, pendingIntent)

            // تحديث الويدجت
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
