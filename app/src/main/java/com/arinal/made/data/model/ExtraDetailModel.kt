package com.arinal.made.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtraDetailModel(val categoryId: Int, val dataId: Int) : Parcelable