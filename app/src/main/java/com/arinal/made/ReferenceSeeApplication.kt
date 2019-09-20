package com.arinal.made

import android.app.Application
import android.appwidget.AppWidgetManager.*
import android.content.ComponentName
import android.content.Context
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.utils.AlarmUtils.Companion.setScheduleAlarm
import com.arinal.made.utils.widget.FavoritesWidget
import org.jetbrains.anko.intentFor
import java.util.*

class ReferenceSeeApplication : Application() {

    companion object {
        lateinit var app: ReferenceSeeApplication
        fun updateFavoritesWidget() {
            val widgetManager = getInstance(app)
            val widgetIds = widgetManager.getAppWidgetIds(ComponentName(app, FavoritesWidget::class.java))
            app.sendBroadcast(app.intentFor<FavoritesWidget>(EXTRA_APPWIDGET_IDS to widgetIds).apply {
                action = ACTION_APPWIDGET_UPDATE
            })
        }

        fun setLocale(context: Context) {
            val locale = Locale(PreferenceManager(context).language)
            setLocale(locale, context)
        }

        @Suppress("DEPRECATION")
        fun setLocale(locale: Locale, context: Context) {
            Locale.setDefault(locale)
            val configuration = context.resources.configuration
            configuration.locale = locale
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        setScheduleAlarm(this)
        updateFavoritesWidget()
    }
}