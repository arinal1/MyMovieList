package com.arinal.made.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.arinal.made.R
import com.arinal.made.data.DataCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.utils.NotificationUtils
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

class DailyUpdateService : JobService() {

    override fun onStartJob(job: JobParameters?): Boolean {
        setLocale()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
        val compositeDisposable = CompositeDisposable()
        TmdbRepository(
            TmdbDatabase.getInstance(applicationContext).tmdbDao(),
            compositeDisposable, SchedulerProviderImpl(), ApiClient.getTmdb()
        ).getUpdates(date, object : DataCallback.FilmCallback {

            override fun onFailed(throwable: Throwable) = jobFinished(job, true)
            override fun onGotData(category: Int, data: MutableList<FilmModel>) {
                for (update in data) {
                    val title = update.title
                    val msg = "$title ${applicationContext.getString(R.string.released_today)}"
                    val channelId = job?.extras?.getString("updateChannelId") ?: "1"
                    val channelName = job?.extras?.getString("updateChannelName") ?: "Notification"
                    NotificationUtils().showNotification(applicationContext, channelId, channelName, title, msg)
                }
                jobFinished(job, false)
                compositeDisposable.dispose()
            }
        })
        jobFinished(job, false)
        return true
    }

    @Suppress("DEPRECATION")
    private fun setLocale() {
        val locale = Locale(PreferenceManager(applicationContext).language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onStopJob(job: JobParameters?): Boolean = true
}
