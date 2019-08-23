package com.arinal.made.data.model

import androidx.room.TypeConverter
import com.arinal.made.data.model.FilmDetailModel.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilmTypeConverters {

    @TypeConverter
    fun genreListFromString(genreJson: String?): List<Genre>? = Gson().fromJson(genreJson, object : TypeToken<List<Genre>>() {}.type)

    @TypeConverter
    fun genreListToString(genre: List<Genre>?): String? = Gson().toJson(genre)

    @TypeConverter
    fun genreIdFromString(data: String?): List<Int>? = Gson().fromJson(data, object : TypeToken<List<Int>>() {}.type)

    @TypeConverter
    fun genreIdToString(data: List<Int>?): String? = Gson().toJson(data)
}