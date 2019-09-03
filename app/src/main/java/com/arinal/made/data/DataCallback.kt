package com.arinal.made.data

import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel

interface DataCallback {
    fun onFailed(throwable: Throwable)

    interface FilmCallback: DataCallback {
        fun onGotData(category: Int, data: MutableList<FilmModel>)
    }

    interface FilmDetailCallback: DataCallback {
        fun onSuccess(add: Boolean)
        fun onGotData(data: FilmDetailModel)
    }
}