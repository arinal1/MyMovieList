package com.arinal.made.utils

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent.getBroadcast
import android.app.job.JobInfo.Builder
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.os.PersistableBundle
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.services.AlarmReceiver
import com.arinal.made.services.DailyUpdateService
import com.arinal.made.services.ReminderService
import com.arinal.made.utils.Constants.reminderChannelId
import com.arinal.made.utils.Constants.reminderChannelName
import com.arinal.made.utils.Constants.reminderServiceId
import com.arinal.made.utils.Constants.updateChannelId
import com.arinal.made.utils.Constants.updateChannelName
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

        fun setReminderJob(context: Context) {
            val jobService = ComponentName(context, ReminderService::class.java)
            val extras = PersistableBundle().apply {
                putString("reminderChannelId", reminderChannelId)
                putString("reminderChannelName", reminderChannelName)
            }
            setJob(context, reminderServiceId, jobService, extras)
        }

        fun setUpdateJob(context: Context) {
            val jobService = ComponentName(context, DailyUpdateService::class.java)
            val extras = PersistableBundle().apply {
                putString("updateChannelId", updateChannelId)
                putString("updateChannelName", updateChannelName)
            }
            setJob(context, updateServiceId, jobService, extras)
        }

        private fun setJob(context: Context, jobId: Int, jobService: ComponentName, extras: PersistableBundle) {
            val builder = Builder(jobId, jobService)
                .setExtras(extras)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPeriodic(15 * 60 * 1000L)
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(builder.build())
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
            val pendingIntent = getBroadcast(context, alarmId, intent, 0)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(RTC_WAKEUP, time, pendingIntent)
        }
    }
}