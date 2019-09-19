package com.arinal.made.utils.widget

import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.net.Uri.parse
import android.widget.RemoteViews
import com.arinal.made.R
import com.arinal.made.services.StackWidgetService
import com.arinal.made.ui.home.HomeActivity
import org.jetbrains.anko.intentFor

class FavoritesWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}

    override fun onUpdate(context: Context, widgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val remoteViews = getRemoteViews(context)
            val serviceIntent = context.intentFor<StackWidgetService>(EXTRA_APPWIDGET_ID to widgetId)
            serviceIntent.data = parse(serviceIntent.toUri(URI_INTENT_SCHEME))
            remoteViews.setRemoteAdapter(R.id.listView, serviceIntent)
            val clickIntent = context.intentFor<HomeActivity>()
            val clickPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntent)
                .getPendingIntent(0, FLAG_UPDATE_CURRENT)
            remoteViews.setPendingIntentTemplate(R.id.listView, clickPendingIntent)
            remoteViews.setEmptyView(R.id.listView, R.id.txEmpty)
            widgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent != null) when (intent.action) {
            ACTION_APPWIDGET_UPDATE -> {
                val widgetManager = getInstance(context)
                val widgetIds = intent.getIntArrayExtra(EXTRA_APPWIDGET_IDS)
                widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.listView)
            }
        }
    }

    private fun getRemoteViews(context: Context): RemoteViews = RemoteViews(context.packageName, R.layout.widget_favorites)
}