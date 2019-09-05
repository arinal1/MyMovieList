package com.arinal.made.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.arinal.made.utils.NotificationUtils

class AlarmService : JobService() {

    override fun onStartJob(job: JobParameters?): Boolean {
        val title = job?.extras?.getString("title") ?: "Reference See"
        val msg = job?.extras?.getString("msg") ?: ""
        val channelId = job?.extras?.getString("reminderChannelId") ?: "1"
        val channelName = job?.extras?.getString("reminderChannelName") ?: "Notification"
        NotificationUtils().showNotification(applicationContext, channelId, channelName, title, msg)
        jobFinished(job, false)
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean = true
}
