package com.arinal.submission1

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieModel(val id: Int,
                      val judul: String,
                      val deskripsi: String,
                      val rilis: String,
                      val durasi: String,
                      val budget: String,
                      val revenue: String,
                      val skor: Int,
                      val genre: String,
                      val poster: Int
) : Parcelable