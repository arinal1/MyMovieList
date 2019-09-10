package com.arinal.made.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.utils.NotificationUtils
import java.util.*

class ReminderService : JobService() {

    override fun onStartJob(job: JobParameters?): Boolean {
        setLocale()
        val title = job?.extras?.getString("title") ?: "Reference See"
        val msg = job?.extras?.getString("msg") ?: ""
        val channelId = job?.extras?.getString("reminderChannelId") ?: "1"
        val channelName = job?.extras?.getString("reminderChannelName") ?: "Notification"
        NotificationUtils().showNotification(applicationContext, channelId, channelName, title, msg)
        jobFinished(job, false)
        return true
    }

    @Suppress("DEPRECATION")
    private fun setLocale(){
        val locale = Locale(PreferenceManager(applicationContext).language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onStopJob(job: JobParameters?): Boolean = true
}
