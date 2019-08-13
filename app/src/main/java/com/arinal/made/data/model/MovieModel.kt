package com.arinal.made.data.model

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

data class MovieModel(
    val page: Int,
    val results: List<Result>
) {
    data class Result(
        val id: Int,
        val original_title: String,
        val overview: String,
        val poster_path: String,
        val genre_ids: List<Int>,
        val release_date: String
    ) {
        fun getGenre(): String {
            var genre = ""
            for ((i, id) in genre_ids.withIndex()) genre = genre + when (id) {
                28 -> "Action"
                12 -> "Adventure"
                16 -> "Animation"
                35 -> "Comedy"
                80 -> "Crime"
                99 -> "Documentary"
                18 -> "Drama"
                10751 -> "Family"
                14 -> "Fantasy"
                36 -> "History"
                27 -> "Horror"
                10402 -> "Music"
                9648 -> "Mystery"
                10749 -> "Romance"
                878 -> "Science Fiction"
                10770 -> "TV Movie"
                53 -> "Thriller"
                10752 -> "War"
                37 -> "Western"
                else -> ""
            } + if (i != genre_ids.size - 1) ", " else ""
            return genre
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate(): String {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            return SimpleDateFormat("dd MMMM yyyy").format(convert.parse(release_date)?.time)
        }
    }
}