package com.arinal.made.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.services.DailyUpdateService.Companion.showUpdateNotification
import com.arinal.made.services.ReminderService.Companion.showReminderNotification
import com.arinal.made.utils.AlarmUtils.Companion.setReminderJob
import com.arinal.made.utils.AlarmUtils.Companion.setUpdateJob
import com.arinal.made.utils.Constants

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getIntExtra("alarmId", 0)
        val preference = PreferenceManager(context)
        if (alarmType == Constants.reminderServiceId) {
            if (preference.reminderSet) {
                preference.reminderJobSet = false
                if (SDK_INT < N) {
                    val channelId = Constants.reminderChannelId
                    val channelName = Constants.reminderChannelName
                    showReminderNotification(context, channelId, channelName)
                }
                setReminderJob(context)
            }
        } else if (alarmType == Constants.updateServiceId) {
            if (preference.dailyUpdateSet) {
                preference.dailyUpdateJobSet = false
                if (SDK_INT < N) {
                    val channelId = Constants.updateChannelId
                    val channelName = Constants.updateChannelName
                    showUpdateNotification(context, channelId, channelName, {}, {})
                }
                setUpdateJob(context)
            }
        }
    }
}