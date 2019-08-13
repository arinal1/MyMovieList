package com.arinal.made.utils.extension

import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.Single

fun Single<out Any>.setSchedule(scheduler: SchedulerProvider): Single<out Any> =
    this.subscribeOn(scheduler.io()).observeOn(scheduler.ui())
