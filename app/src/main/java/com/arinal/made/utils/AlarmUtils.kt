package com.arinal.made.utils

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Context.ALARM_SERVICE
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.services.AlarmReceiver
import com.arinal.made.utils.Constants.reminderServiceId
import com.arinal.made.utils.Constants.updateServiceId
import org.jetbrains.anko.intentFor
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class AlarmUtils {
    companion object {
        fun setScheduleAlarm(context: Context) {
            val preference = PreferenceManager(context)
            val format = SimpleDateFormat("HHmm", Locale.getDefault())
            val time = currentTimeMillis()
            if (preference.reminderSet && !preference.reminderJobSet) {
                if (format.format(time) == "0700") setAlarm(time, context, reminderServiceId)
                else setAlarm(context, 7, reminderServiceId)
                preference.reminderJobSet = true
            }
            if (preference.dailyUpdateSet && !preference.dailyUpdateJobSet) {
                if (format.format(time) == "0800") setAlarm(time, context, updateServiceId)
                else setAlarm(context, 8, updateServiceId)
                preference.dailyUpdateJobSet = true
            }
        }

        private fun setAlarm(context: Context, hour: Int, alarmId: Int) {
            val calendar = getInstance().apply {
                set(HOUR_OF_DAY, hour)
                set(MINUTE, 0)
                set(SECOND, 0)
            }
            if (calendar.timeInMillis - currentTimeMillis() < 0) calendar.add(DAY_OF_MONTH, 1)
            setAlarm(calendar.timeInMillis, context, alarmId)
        }

        private fun setAlarm(time: Long, context: Context, alarmId: Int) {
            val intent = context.intentFor<AlarmReceiver>("alarmId" to alarmId)
            val pendingIntent = getBroadcast(context, alarmId, intent, FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(RTC_WAKEUP, time, INTERVAL_DAY, pendingIntent)
        }
    }
}