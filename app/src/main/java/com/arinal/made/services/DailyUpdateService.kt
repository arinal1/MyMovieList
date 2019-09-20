package com.arinal.made.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import com.arinal.made.R
import com.arinal.made.ReferenceSeeApplication.Companion.setLocale
import com.arinal.made.data.DataCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.utils.NotificationUtils.Companion.showNotification
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import io.reactivex.disposables.CompositeDisposable
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

class DailyUpdateService : JobService() {

    companion object {
        fun showUpdateNotification(context: Context, channelId: String, channelName: String, onFailed: () -> Unit, onSuccess: () -> Unit) {
            val preference = PreferenceManager(context)
            val date = SimpleDateFormat("yyyy-MM-dd", getDefault()).format(currentTimeMillis())
            if (preference.lastUpdateNotif != date) {
                setLocale(context)
                val compositeDisposable = CompositeDisposable()
                TmdbRepository(
                    TmdbDatabase.getInstance(context).tmdbDao(),
                    compositeDisposable, SchedulerProviderImpl(), ApiClient.getTmdb()
                ).getUpdates(date, object : DataCallback.FilmCallback {
                    override fun onFailed(throwable: Throwable) = onFailed()
                    override fun onGotData(category: Int, data: MutableList<FilmModel>) {
                        for (update in data) {
                            val title = update.title
                            val msg = "$title ${context.getString(R.string.released_today)}"
                            showNotification(context, channelId, channelName, title, msg)
                        }
                        preference.lastUpdateNotif = date
                        onSuccess()
                        compositeDisposable.dispose()
                    }
                })
            }
        }
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        val channelId = job?.extras?.getString("updateChannelId") ?: "1"
        val channelName = job?.extras?.getString("updateChannelName") ?: "Notification"
        val onFailed = { jobFinished(job, true) }
        val onSuccess = { jobFinished(job, false) }
        showUpdateNotification(applicationContext, channelId, channelName, onFailed, onSuccess)
        jobFinished(job, false)
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean = true
}
