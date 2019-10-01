package com.arinal.made.favorites.model

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat

data class FilmDetailModel(
    val key: Int,
    val genres: List<Genre>,
    val budget: Float,
    val duration: List<Int>?,
    val title: String,
    val poster: String,
    val releaseDate: String?,
    val revenue: Float,
    val voteAverage: Double,
    val category: Int,
    val id: Int,
    val overview: String,
    val runtime: Int
) {
    data class Genre(
        val id: Int,
        val name: String
    )

    fun getBudget(): String = "$${DecimalFormat("#,###").format(budget)}"

    fun getRevenue(): String = "$${DecimalFormat("#,###").format(revenue)}"

    fun getGenre(): String {
        var result = ""
        for ((i, genre) in genres.withIndex()) result = result + genre.name + if (i != genres.size - 1) ", " else ""
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun getRelease(): String {
        return if (releaseDate.isNullOrEmpty()) "" else {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            SimpleDateFormat("dd MMMM yyyy").format(convert.parse(releaseDate)?.time)
        }
    }

    fun getDuration(txHour: String, txMinute: String): String {
        val duration = when {
            category == 0 -> runtime
            !duration.isNullOrEmpty() -> {
                var total = 0
                for (eps in duration)
                    total += eps
                total / duration.size
            }
            else -> 0
        }
        val minutes = duration.rem(60)
        val hour = (duration - minutes) / 60
        val h = if (hour != 0) "$hour $txHour " else ""
        val m = "$minutes $txMinute"
        return "$h$m"
    }
}