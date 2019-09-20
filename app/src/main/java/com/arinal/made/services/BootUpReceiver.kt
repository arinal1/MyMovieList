package com.arinal.made.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.utils.AlarmUtils.Companion.setScheduleAlarm

class BootUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val quickBoot = "android.intent.action.QUICKBOOT_POWERON"
        if (intent.action == ACTION_BOOT_COMPLETED || intent.action == quickBoot) {
            val preference = PreferenceManager(context)
            preference.reminderJobSet = false
            preference.dailyUpdateJobSet = false
            setScheduleAlarm(context)
        }
    }
}
