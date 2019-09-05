package com.arinal.made.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arinal.made.utils.AlarmUtils
import com.arinal.made.utils.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reference See"
        val msg = intent.getStringExtra("msg") ?: ""
        val channelId = intent.getStringExtra("reminderChannelId") ?: "1"
        val channelName = intent.getStringExtra("reminderChannelName") ?: "Notification"
        NotificationUtils().showNotification(context, channelId, channelName, title, msg)
        AlarmUtils(context)
    }
}
