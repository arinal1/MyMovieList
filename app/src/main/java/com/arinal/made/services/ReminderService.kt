package com.arinal.made.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import com.arinal.made.R
import com.arinal.made.ReferenceSeeApplication.Companion.setLocale
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.utils.NotificationUtils.Companion.showNotification
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

class ReminderService : JobService() {

    companion object {
        fun showReminderNotification(context: Context, channelId: String, channelName: String) {
            val preference = PreferenceManager(context)
            val date = SimpleDateFormat("yyyy-MM-dd", getDefault()).format(currentTimeMillis())
            if (preference.lastReminderNotif != date) {
                setLocale(context)
                val title = context.getString(R.string.notif_reminder_title)
                val msg = context.getString(R.string.notif_reminder_msg)
                showNotification(context, channelId, channelName, title, msg)
                preference.lastReminderNotif = date
            }
        }
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        val channelId = job?.extras?.getString("reminderChannelId") ?: "1"
        val channelName = job?.extras?.getString("reminderChannelName") ?: "Notification"
        showReminderNotification(applicationContext, channelId, channelName)
        jobFinished(job, false)
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean = true
}
