package com.arinal.made.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel

@Dao
interface TmdbDao {
    @Query("SELECT * FROM filmDetail WHERE category = :category AND id = :id LIMIT 1")
    fun getDetailFilm(category: Int, id: Int): List<FilmDetailModel>

    @Query("SELECT `key` FROM filmDetail WHERE category = :category AND id = :id LIMIT 1")
    fun getDetailKey(category: Int, id: Int): Int

    @Insert
    fun insertDetailFilm(film: List<FilmDetailModel>)

    @Query("DELETE FROM filmDetail WHERE `key` = :key")
    fun deleteDetailFilm(key: Int)

    @Query("SELECT * FROM film WHERE category = :category LIMIT :start , 10")
    fun getFavorites(category: Int, start: Int): List<FilmModel>

    @Query("SELECT `key` FROM film WHERE category = :category AND id = :id LIMIT 1")
    fun getFavoriteKey(category: Int, id: Int): Int

    @Query("SELECT * FROM film WHERE category = :category AND (title LIKE :query OR overview LIKE :query) LIMIT :start , 10")
    fun searchFavorites(category: Int, query: String, start: Int): List<FilmModel>

    @Insert
    fun insertFavorite(film: FilmModel)

    @Query("DELETE FROM film WHERE `key` = :key")
    fun deleteFavorite(key: Int)

    @Query("SELECT COUNT(id) FROM film WHERE category = :category AND id = :id")
    fun isFilmFavorited(category: Int, id: Int): Int
}