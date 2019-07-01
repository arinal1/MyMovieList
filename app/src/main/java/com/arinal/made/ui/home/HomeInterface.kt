package com.arinal.made.ui.home

import android.os.Parcelable
import com.arinal.made.data.model.FilmModel

interface HomeInterface: Parcelable{
    fun goToDetail(data: FilmModel)
}