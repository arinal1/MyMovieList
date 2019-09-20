package com.arinal.made.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.arinal.made.utils.Constants.preferenceSee

class PreferenceManager(context: Context) {
    private val contextMode = MODE_PRIVATE
    private val pref: SharedPreferences = context.getSharedPreferences(preferenceSee, contextMode)
    private lateinit var editor: SharedPreferences.Editor

    var language: String
        get() = pref.getString("LANGUAGE", "id") ?: "id"
        set(data) {
            editor = pref.edit()
            editor.putString("LANGUAGE", data)?.apply()
        }

    var reminderSet: Boolean
        get() = pref.getBoolean("REMINDER", true)
        set(status) {
            editor = pref.edit()
            editor.putBoolean("REMINDER", status)?.apply()
        }

    var dailyUpdateSet: Boolean
        get() = pref.getBoolean("DAILY_UPDATE", true)
        set(status) {
            editor = pref.edit()
            editor.putBoolean("DAILY_UPDATE", status)?.apply()
        }

    var reminderJobSet: Boolean
        get() = pref.getBoolean("REMINDER_JOB", false)
        set(status) {
            editor = pref.edit()
            editor.putBoolean("REMINDER_JOB", status)?.apply()
        }

    var dailyUpdateJobSet: Boolean
        get() = pref.getBoolean("DAILY_UPDATE_JOB", false)
        set(status) {
            editor = pref.edit()
            editor.putBoolean("DAILY_UPDATE_JOB", status)?.apply()
        }

    var lastUpdateNotif: String
        get() = pref.getString("LAST_UPDATE_NOTIF", "") ?: ""
        set(data) {
            editor = pref.edit()
            editor.putString("LAST_UPDATE_NOTIF", data)?.apply()
        }

    var lastReminderNotif: String
        get() = pref.getString("LAST_REMINDER_NOTIF", "") ?: ""
        set(data) {
            editor = pref.edit()
            editor.putString("LAST_REMINDER_NOTIF", data)?.apply()
        }
}