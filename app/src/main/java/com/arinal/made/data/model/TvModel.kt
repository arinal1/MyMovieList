package com.arinal.made.data.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

data class TvModel(
    val page: Int,
    val results: List<Result>
) {
    data class Result(
        val first_air_date: String,
        val id: Int,
        val original_name: String,
        val overview: String,
        val genre_ids: List<Int>,
        val poster_path: String
    ) {
        fun getGenre(): String {
            var genre = ""
            for ((i, id) in genre_ids.withIndex()) genre = genre + when (id) {
                10759 -> "Action & Adventure"
                16 -> "Animation"
                35 -> "Comedy"
                80 -> "Crime"
                99 -> "Documentary"
                18 -> "Drama"
                10751 -> "Family"
                10762 -> "Kids"
                9648 -> "Mystery"
                10763 -> "News"
                10764 -> "Reality"
                10765 -> "Sci-Fi & Fantasy"
                10766 -> "Soap"
                10767 -> "Talk"
                10768 -> "War & Politics"
                37 -> "Western"
                27 -> "Horror"
                else -> ""
            } + if (i != genre_ids.size - 1) ", " else ""
            return genre
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate(): String {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            return SimpleDateFormat("dd MMMM yyyy").format(convert.parse(first_air_date)?.time)
        }
    }
}