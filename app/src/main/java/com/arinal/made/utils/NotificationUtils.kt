package com.arinal.made.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_LAUNCHER
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat.Builder
import com.arinal.made.R
import com.arinal.made.ui.home.HomeActivity
import org.jetbrains.anko.intentFor
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

class NotificationUtils {

    fun showNotification(context: Context, channelId: String, channelName: String, title: String, msg: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = context.intentFor<HomeActivity>().setAction(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER)
        val pendingIntent = getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
        val notification = Builder(context, channelId)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(msg)
            .setGroup(channelName)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .build()
        if (SDK_INT >= O) {
            val channel = NotificationChannel(channelId, channelName, IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val id = SimpleDateFormat("ssSSS", getDefault()).format(currentTimeMillis()).toInt()
        val summaryNotification = Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setGroup(channelName)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(id, notification)
        notificationManager.notify(channelId.toInt(), summaryNotification)
    }
}