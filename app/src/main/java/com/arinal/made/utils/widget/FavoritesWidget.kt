package com.arinal.made.utils.widget

import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent.URI_INTENT_SCHEME
import android.net.Uri.parse
import android.widget.RemoteViews
import com.arinal.made.R
import com.arinal.made.services.StackWidgetService
import com.arinal.made.ui.home.HomeActivity
import org.jetbrains.anko.intentFor

class FavoritesWidget : AppWidgetProvider() {

    private lateinit var widgetManager: AppWidgetManager
    private lateinit var remoteViews: RemoteViews

    override fun onEnabled(context: Context) {}
    override fun onDisabled(context: Context) {}

    override fun onUpdate(context: Context, widgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        this.widgetManager = widgetManager
        for (widgetId in appWidgetIds) {
            remoteViews = RemoteViews(context.packageName, R.layout.widget_favorites)
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
}