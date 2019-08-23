package com.arinal.made.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExtraDetailModel(
    val filmModel: FilmModel,
    val index: Int,
    val isFromFavorite: Boolean
) : Parcelable