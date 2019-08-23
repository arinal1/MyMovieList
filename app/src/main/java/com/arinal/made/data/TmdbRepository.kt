package com.arinal.made.data

import com.arinal.made.BuildConfig.TMDB_API_KEY
import com.arinal.made.data.DataCallback.FilmCallback
import com.arinal.made.data.DataCallback.FilmDetailCallback
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.extension.setSchedule
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class TmdbRepository(
    private val tmdbDao: TmdbDao, private val compositeDisposable: CompositeDisposable,
    private val scheduler: SchedulerProvider, private val endpoints: TmdbEndpoint
) {

    fun getDetailFilm(category: Int, id: Int, language: String, callback: FilmDetailCallback) = Thread {
        val film = tmdbDao.getDetailFilm(category, id)
        if (film.isNotEmpty()) {
            callback.onGotData(film[0])
            callback.onSuccess(true)
        }
        else {
            val endpoint = if (category == 0) endpoints.getDetailMovie(id, TMDB_API_KEY, language)
            else endpoints.getDetailTvShow(id, TMDB_API_KEY, language)
            compositeDisposable.add(
                endpoint.setSchedule(scheduler).subscribe({
                    callback.onGotData(it as FilmDetailModel)
                }, {
                    callback.onFailed(it)
                })
            )
        }
    }.start()

    fun getFavoriteMovies(page: Int, filmCallback: FilmCallback) {
        val start = if (page == 1) 0 else page * 10 - 10
        getFavorites(start, 0, filmCallback)
    }

    fun getFavoriteTvShows(page: Int, filmCallback: FilmCallback) {
        val start = if (page == 1) 0 else page * 10 - 10
        getFavorites(start, 1, filmCallback)
    }

    private fun getFavorites(start: Int, category: Int, filmCallback: FilmCallback) = Thread {
        val favorites = tmdbDao.getFavorites(category, start)
        filmCallback.onGotData(favorites.toMutableList())
    }.start()

    fun isFilmFavorited(callback: FilmDetailCallback, category: Int, id: Int) = Thread {
        val exist = tmdbDao.isFilmFavorited(category, id)
        callback.onSuccess(exist == 1)
    }.start()

    fun insertFavorite(callback: FilmDetailCallback, dataFilm: FilmModel, dataDetail: FilmDetailModel) = Thread {
        tmdbDao.insertFavorite(dataFilm)
        tmdbDao.insertDetailFilm(listOf(dataDetail))
        callback.onSuccess(true)
    }.start()

    fun deleteFavorite(callback: FilmDetailCallback, dataFilm: FilmModel, dataDetail: FilmDetailModel) = Thread {
        val favKey = tmdbDao.getFavoriteKey(dataFilm.category, dataFilm.id)
        val detailKey = tmdbDao.getDetailKey(dataDetail.category, dataDetail.id)
        tmdbDao.deleteFavorite(favKey)
        tmdbDao.deleteDetailFilm(detailKey)
        callback.onSuccess(false)
    }.start()
}