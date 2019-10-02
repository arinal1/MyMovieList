package com.arinal.made.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.arinal.made.R
import com.arinal.made.ReferenceSeeApplication
import com.arinal.made.data.DataCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.utils.Constants
import com.arinal.made.utils.Constants.reminderServiceId
import com.arinal.made.utils.Constants.updateServiceId
import com.arinal.made.utils.NotificationUtils
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmType = intent.getIntExtra("alarmId", 0)
        val preference = PreferenceManager(context)
        if (alarmType == reminderServiceId && preference.reminderSet) showReminderNotification(context)
        else if (alarmType == updateServiceId && preference.dailyUpdateSet) showUpdateNotification(context)
    }

    private fun showReminderNotification(context: Context) {
        val preference = PreferenceManager(context)
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
        if (preference.lastReminderNotif != date) {
            ReferenceSeeApplication.setLocale(context)
            val title = context.getString(R.string.notif_reminder_title)
            val msg = context.getString(R.string.notif_reminder_msg)
            val channelId = Constants.reminderChannelId
            val channelName = Constants.reminderChannelName
            NotificationUtils.showNotification(context, channelId, channelName, title, msg)
            preference.lastReminderNotif = date
        }
    }

    private fun showUpdateNotification(context: Context) {
        val preference = PreferenceManager(context)
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
        if (preference.lastUpdateNotif != date) {
            ReferenceSeeApplication.setLocale(context)
            val compositeDisposable = CompositeDisposable()
            TmdbRepository(
                TmdbDatabase.getInstance(context).tmdbDao(),
                compositeDisposable, SchedulerProviderImpl(), ApiClient.getTmdb()
            ).getUpdates(date, object : DataCallback.FilmCallback {
                override fun onFailed(throwable: Throwable) {}
                override fun onGotData(category: Int, data: MutableList<FilmModel>) {
                    for (update in data) {
                        val title = update.title
                        val msg = "$title ${context.getString(R.string.released_today)}"
                        val channelId = Constants.updateChannelId
                        val channelName = Constants.updateChannelName
                        NotificationUtils.showNotification(context, channelId, channelName, title, msg)
                    }
                    preference.lastUpdateNotif = date
                    compositeDisposable.dispose()
                }
            })
        }
    }
}