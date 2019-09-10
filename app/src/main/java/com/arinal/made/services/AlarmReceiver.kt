package com.arinal.made.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.utils.AlarmUtils
import com.arinal.made.utils.Constants.reminderServiceId

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getIntExtra("alarmId", 0)
        if (alarmType == reminderServiceId) {
            if (PreferenceManager(context).reminderSet) {
                PreferenceManager(context).reminderJobSet = false
                AlarmUtils(context)
            }
        } else {
            if (PreferenceManager(context).dailyUpdateSet) {
                PreferenceManager(context).dailyUpdateJobSet = false
                AlarmUtils(context)
            }
        }
    }
}
