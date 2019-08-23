package com.arinal.made.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.model.FilmTypeConverters

@Database(entities = [FilmDetailModel::class, FilmModel::class], version = 1, exportSchema = false)
@TypeConverters(FilmTypeConverters::class)
abstract class TmdbDatabase : RoomDatabase() {

    abstract fun tmdbDao(): TmdbDao

    companion object {
        private lateinit var INSTANCE: TmdbDatabase
        private val sLock = Object()
        fun getInstance(context: Context): TmdbDatabase {
            synchronized(sLock) {
                INSTANCE = Room.databaseBuilder(context, TmdbDatabase::class.java, "tmdb").build()
                return INSTANCE
            }
        }
    }
}