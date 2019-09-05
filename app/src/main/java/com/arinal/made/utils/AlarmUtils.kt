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
import com.arinal.made.R
import com.arinal.made.services.AlarmReceiver
import com.arinal.made.services.AlarmService
import com.arinal.made.utils.Constants.reminderChannelId
import com.arinal.made.utils.Constants.reminderChannelName
import com.arinal.made.utils.Constants.reminderServiceId
import org.jetbrains.anko.intentFor
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class AlarmUtils(private val context: Context) {

    init {
        val format = SimpleDateFormat("hhmm", Locale.getDefault())
        if (format.format(currentTimeMillis()) == "0700") setReminderJob()
        else setReminderAlarm()
    }

    private fun setReminderJob() {
        val mServiceComponent = ComponentName(context, AlarmService::class.java)
        val bundle = PersistableBundle().apply {
            putString("title", context.getString(R.string.notif_reminder_title))
            putString("msg", context.getString(R.string.notif_reminder_msg))
            putString("reminderChannelId", reminderChannelId)
            putString("reminderChannelName", reminderChannelName)
        }
        val builder = Builder(reminderServiceId, mServiceComponent)
            .setExtras(bundle)
            .setRequiredNetworkType(NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setPeriodic(24 * 60 * 1000L)
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    private fun setReminderAlarm() {
        val calendar = getInstance().apply {
            set(HOUR_OF_DAY, 7)
            set(MINUTE, 0)
            set(SECOND, 0)
        }
        if (calendar.timeInMillis - currentTimeMillis() < 0) calendar.add(DAY_OF_MONTH, 1)
        val intent = context.intentFor<AlarmReceiver>(
            "title" to context.getString(R.string.notif_reminder_title),
            "msg" to context.getString(R.string.notif_reminder_msg),
            "reminderChannelId" to reminderChannelId,
            "reminderChannelName" to reminderChannelName
        )
        val pendingIntent = getBroadcast(context, reminderServiceId, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun setUpdateJob() {

    }

    fun setUpdateAlarm() {

    }
}