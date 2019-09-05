package com.arinal.made.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.arinal.made.R
import com.arinal.made.ui.home.HomeActivity
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtils {

    fun showNotification(context: Context, channelId: String, channelName: String, title: String, msg: String) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getActivity(context, 0, context.intentFor<HomeActivity>(), FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(msg)
            .setGroup(channelName)
            .setColor(ContextCompat.getColor(context, android.R.color.black))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.getDefault()).format(Date()))
        val summaryNotification = NotificationCompat.Builder(context, channelId)
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