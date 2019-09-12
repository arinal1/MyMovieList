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

class AlarmUtils(private val context: Context) {

    private val preference: PreferenceManager = PreferenceManager(context)

    init {
        val format = SimpleDateFormat("HHmm", Locale.getDefault())
        if (preference.reminderSet && !preference.reminderJobSet) {
            if (format.format(currentTimeMillis()) == "0700") setReminderJob()
            else setAlarm(7, reminderServiceId)
            preference.reminderJobSet = true
        }
        if (preference.dailyUpdateSet && !preference.dailyUpdateJobSet) {
            if (format.format(currentTimeMillis()) == "0800") setUpdateJob()
            else setAlarm(8, updateServiceId)
            preference.dailyUpdateJobSet = true
        }
    }

    private fun setReminderJob() {
        val jobService = ComponentName(context, ReminderService::class.java)
        val extras = PersistableBundle().apply {
            putString("reminderChannelId", reminderChannelId)
            putString("reminderChannelName", reminderChannelName)
        }
        setJob(reminderServiceId, jobService, extras)
    }

    private fun setUpdateJob() {
        val jobService = ComponentName(context, DailyUpdateService::class.java)
        val extras = PersistableBundle().apply {
            putString("updateChannelId", updateChannelId)
            putString("updateChannelName", updateChannelName)
        }
        setJob(updateServiceId, jobService, extras)
    }

    private fun setJob(jobId: Int, jobService: ComponentName, extras: PersistableBundle) {
        val builder = Builder(jobId, jobService)
            .setExtras(extras)
            .setRequiredNetworkType(NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setPeriodic(24 * 60 * 60 * 1000L)
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    private fun setAlarm(hour: Int, alarmId: Int) {
        val calendar = getInstance().apply {
            set(HOUR_OF_DAY, hour)
            set(MINUTE, 0)
            set(SECOND, 0)
        }
        if (calendar.timeInMillis - currentTimeMillis() < 0) calendar.add(DAY_OF_MONTH, 1)
        val intent = context.intentFor<AlarmReceiver>("alarmId" to alarmId)
        val pendingIntent = getBroadcast(context, alarmId, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}