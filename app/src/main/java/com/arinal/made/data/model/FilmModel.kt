package com.arinal.made.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

@Parcelize
@Entity(tableName = "film")
data class FilmModel(
    @PrimaryKey(autoGenerate = true) val key: Int,

    @SerializedName(value = "first_air_date", alternate = ["release_date", "release"])
    @ColumnInfo(name = "first_air_date") val release: String?,

    @SerializedName("genre_ids", alternate = ["genres"])
    val genres: List<Int>,

    @SerializedName(value = "original_name", alternate = ["original_title", "title"])
    @ColumnInfo(name = "title") val title: String,

    @SerializedName("poster_path", alternate = ["poster"])
    @ColumnInfo(name = "poster_path") val poster: String?,

    @ColumnInfo(name = "category") var category: Int,
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "overview") val overview: String
) : Parcelable {
    @Ignore
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
    @Ignore
    fun getDate(): String {
        return if (release.isNullOrEmpty()) "" else {
            val convert = SimpleDateFormat("yyyy-MM-dd")
            SimpleDateFormat("dd MMMM yyyy").format(convert.parse(release)?.time)
        }
    }
}