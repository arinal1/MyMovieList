package com.arinal.made

import android.app.Application
import com.arinal.made.utils.AlarmUtils

class ReferenceSeeApplication : Application() {

    val app = this

    override fun onCreate() {
        super.onCreate()
        AlarmUtils(this)
    }
}