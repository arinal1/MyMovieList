package com.arinal.made.data.model

import android.annotation.SuppressLint
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@Entity(tableName = "filmDetail")
data class FilmDetailModel(
    @PrimaryKey(autoGenerate = true) val key: Int?,
    val genres: List<Genre>,

    @SerializedName("budget")
    @ColumnInfo(name = "budget") val budgets: Float,

    @SerializedName("episode_run_time")
    @ColumnInfo(name = "duration") val duration: List<Int>?,

    @SerializedName(value = "original_title", alternate = ["original_name"])
    @ColumnInfo(name = "title") val title: String,

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster") val poster: String,

    @SerializedName(value = "release_date", alternate = ["first_air_date"])
    @ColumnInfo(name = "releaseDate") val releaseDate: String?,

    @SerializedName("revenue")
    @ColumnInfo(name = "revenue") val revenues: Float,

    @SerializedName("vote_average")
    @ColumnInfo(name = "voteAverage") val voteAverage: Double,

    @ColumnInfo(name = "category") var category: Int,
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "runtime") val runtime: Int
) {
    data class Genre(
        @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "name") val name: String
    )

    @Ignore
    fun getBudget(): String = "$${DecimalFormat("#,###").format(budgets)}"

    @Ignore
    fun getRevenue(): String = "$${DecimalFormat("#,###").format(revenues)}"

    @Ignore
    fun getGenre(): String {
        var result = ""
        for ((i, genre) in genres.withIndex()) result = result + genre.name + if (i != genres.size - 1) ", " else ""
        return result
    }

    @SuppressLint("SimpleDateFormat")
    @Ignore
    fun getRelease(): String {
        return if (releaseDate.isNullOrEmpty()) "" else {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            SimpleDateFormat("dd MMMM yyyy").format(convert.parse(releaseDate)?.time)
        }
    }

    @Ignore
    fun getDuration(txHour: String, txMinute: String): String {
        val duration = when {
            category == 0 -> runtime
            !duration.isNullOrEmpty()-> {
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