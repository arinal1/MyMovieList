package com.arinal.made.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.net.Uri.Builder
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.FilmModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteContentProvider : ContentProvider() {

    companion object {
        private const val FAVORITE_MOVIE = 1
        private const val FAVORITE_TV = 2
        private const val FAVORITE_MOVIE_ID = 3
        private const val FAVORITE_TV_ID = 4
        private const val AUTHORITY = "com.arinal.made"
        private const val TABLE_NAME = "film"
        private val CONTENT_URI = Builder().scheme("content").authority(AUTHORITY).appendPath(TABLE_NAME).build()
    }

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private lateinit var tmdbDao: TmdbDao

    override fun onCreate(): Boolean {
        tmdbDao = TmdbDatabase.getInstanceOnMainThread(context!!).tmdbDao()
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/movies/#", FAVORITE_MOVIE)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/tvs/#", FAVORITE_TV)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/movie/#", FAVORITE_MOVIE_ID)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/movie", FAVORITE_MOVIE_ID)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/tv/#", FAVORITE_TV_ID)
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/tv", FAVORITE_TV_ID)
        return true
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            FAVORITE_MOVIE -> "MOVIES FAVORITE"
            FAVORITE_TV -> "TV SHOWS FAVORITE"
            FAVORITE_MOVIE_ID -> "MOVIE FAVORITE BY ID"
            FAVORITE_TV_ID -> "TV SHOW FAVORITE BY ID"
            else -> "NONE"
        }
    }

    override fun query(uri: Uri, p0: Array<String>?, p1: String?, p2: Array<String>?, p3: String?): Cursor? {
        val category = when (uriMatcher.match(uri)) {
            FAVORITE_MOVIE, FAVORITE_MOVIE_ID -> 0
            FAVORITE_TV, FAVORITE_TV_ID -> 1
            else -> -1
        }
        return if (category == -1) null
        else when (uriMatcher.match(uri)) {
            FAVORITE_MOVIE, FAVORITE_TV -> tmdbDao.getFavoritesContent(category, uri.lastPathSegment?.toInt() ?: 0)
            FAVORITE_MOVIE_ID, FAVORITE_TV_ID -> tmdbDao.getFavoriteContent(category, uri.lastPathSegment?.toInt() ?: 0)
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        var json = Gson().toJson(values)
        json = json.substring(11, json.length -1)
        val data = Gson().fromJson<FilmModel>(json, object : TypeToken<FilmModel>() {}.type)
        val added = when (uriMatcher.match(uri)) {
            FAVORITE_MOVIE_ID, FAVORITE_TV_ID -> {
                tmdbDao.insertFavorite(data)
                0
            }
            else -> -1
        }
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (uriMatcher.match(uri)) {
            FAVORITE_MOVIE_ID, FAVORITE_TV_ID -> {
                val category = if (uriMatcher.match(uri) == FAVORITE_MOVIE_ID) 0 else 1
                val id = uri.lastPathSegment?.toInt() ?: 0
                val key = tmdbDao.getFavoriteKey(category, id)
                tmdbDao.deleteFavorite(key)
                val detailKey = tmdbDao.getDetailKey(category, id)
                tmdbDao.deleteDetailFilm(detailKey)
                0
            }
            else -> -1
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = -1
}
