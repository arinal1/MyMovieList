package com.arinal.made

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import com.arinal.made.utils.AlarmUtils
import com.arinal.made.utils.widget.FavoritesWidget
import org.jetbrains.anko.intentFor

class ReferenceSeeApplication : Application() {

    companion object {
        lateinit var app: ReferenceSeeApplication
        fun updateFavoritesWidget() {
            val widgetManager = AppWidgetManager.getInstance(app)
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(app, FavoritesWidget::class.java))
            app.sendBroadcast(app.intentFor<FavoritesWidget>(EXTRA_APPWIDGET_IDS to widgetIds).apply { action = ACTION_APPWIDGET_UPDATE })
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        AlarmUtils(this)
        updateFavoritesWidget()
    }
}