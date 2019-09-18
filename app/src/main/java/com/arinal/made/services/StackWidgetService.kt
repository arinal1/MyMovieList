package com.arinal.made.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.arinal.made.utils.widget.StackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StackRemoteViewsFactory(applicationContext)
    }

}