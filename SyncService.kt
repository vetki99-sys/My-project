package com.smartcalculator.data.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

/**
 * خدمة المزامنة
 * تستخدم لمزامنة البيانات في الخلفية
 */
@AndroidEntryPoint
class SyncService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: تنفيذ المزامنة
        return START_STICKY
    }

    companion object {
        const val ACTION_SYNC = "com.smartcalculator.ACTION_SYNC"
    }
}
