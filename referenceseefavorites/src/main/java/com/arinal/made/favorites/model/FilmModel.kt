package com.arinal.made.favorites.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
data class FilmModel(
    val key: Int,
    @SerializedName("first_air_date") val release: String?,
    val genres: List<Int>,
    val title: String,
    @SerializedName("poster_path") val poster: String?,
    val id: Int,
    val overview: String
) : Parcelable {

    fun getGenre(): String {
        var genre = ""
        for ((i, id) in genres.withIndex()) genre = genre + when (id) {
            12 -> "Adventure"
            14 -> "Fantasy"
            16 -> "Animation"
            18 -> "Drama"
            27 -> "Horror"
            28 -> "Action"
            35 -> "Comedy"
            36 -> "History"
            37 -> "Western"
            53 -> "Thriller"
            80 -> "Crime"
            99 -> "Documentary"
            878 -> "Science Fiction"
            9648 -> "Mystery"
            10402 -> "Music"
            10749 -> "Romance"
            10751 -> "Family"
            10752 -> "War"
            10759 -> "Action & Adventure"
            10762 -> "Kids"
            10763 -> "News"
            10764 -> "Reality"
            10765 -> "Sci-Fi & Fantasy"
            10766 -> "Soap"
            10767 -> "Talk"
            10768 -> "War & Politics"
            10770 -> "TV Movie"
            else -> ""
        } + if (i != genres.size - 1) ", " else ""
        return genre
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        return if (release.isNullOrEmpty()) "" else {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            SimpleDateFormat("dd MMMM yyyy").format(convert.parse(release)?.time)
        }
    }
}