package com.arinal.made.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
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
        private val sLock = Object()
        fun getInstance(context: Context): TmdbDatabase {
            synchronized(sLock) {
                return databaseBuilder(context, TmdbDatabase::class.java, "tmdb").build()
            }
        }

        fun getInstanceOnMainThread(context: Context): TmdbDatabase {
            synchronized(sLock) {
                return databaseBuilder(context, TmdbDatabase::class.java, "tmdb").allowMainThreadQueries().build()
            }
        }
    }
}