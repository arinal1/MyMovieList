package com.arinal.made.data

import com.arinal.made.BuildConfig.TMDB_API_KEY
import com.arinal.made.ReferenceSeeApplication.Companion.updateFavoritesWidget
import com.arinal.made.data.DataCallback.FilmCallback
import com.arinal.made.data.DataCallback.FilmDetailCallback
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.model.FilmResponseModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.extension.setSchedule
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.Single
import io.reactivex.Single.fromCallable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class TmdbRepository(
    private val tmdbDao: TmdbDao, private val compositeDisposable: CompositeDisposable,
    private val scheduler: SchedulerProvider, private val endpoint: TmdbEndpoint
) {

    private lateinit var disposable: Disposable

    fun getDetailFilm(category: Int, id: Int, language: String, callback: FilmDetailCallback) {
        compositeDisposable.add(fromCallable { tmdbDao.getDetailFilm(category, id) }
            .subscribeOn(scheduler.io())
            .flatMap {
                if (it.isEmpty()) {
                    if (category == 0) endpoint.getDetailMovie(id, TMDB_API_KEY, language)
                    else endpoint.getDetailTvShow(id, TMDB_API_KEY, language)
                } else {
                    callback.onGotData(it[0])
                    callback.onSuccess(true)
                    Single.just("")
                }
            }
            .observeOn(scheduler.ui())
            .subscribe({
                if (it is FilmDetailModel) callback.onGotData(it)
            }, {
                callback.onFailed(it)
            })
        )
    }

    fun getFilms(category: Int, lang: String, page: Int, filmCallback: FilmCallback) {
        val language = if (lang == "in") "id" else lang
        val api = if (category == 0) endpoint.getMovies(TMDB_API_KEY, language, page)
        else endpoint.getTvShows(TMDB_API_KEY, language, page)
        disposable = api.setSchedule(scheduler).subscribe({
            filmCallback.onGotData(category, (it as FilmResponseModel).results.toMutableList())
        }, {
            filmCallback.onFailed(it)
        })
        compositeDisposable.add(disposable)
    }

    fun searchFilms(category: Int, lang: String, query: String, page: Int, filmCallback: FilmCallback) {
        val language = if (lang == "in") "id" else lang
        val api = if (category == 0) endpoint.searchMovies(TMDB_API_KEY, language, query, page)
        else endpoint.searchTvShows(TMDB_API_KEY, language, query, page)
        disposable = api.setSchedule(scheduler).subscribe({
            filmCallback.onGotData(category, (it as FilmResponseModel).results.toMutableList())
        }, {
            filmCallback.onFailed(it)
        })
        compositeDisposable.add(disposable)
    }

    fun getFavorites(page: Int, category: Int, filmCallback: FilmCallback) {
        val start = if (page == 1) 0 else page * 10 - 10
        disposable = fromCallable { tmdbDao.getFavorites(category, start) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                filmCallback.onGotData(category, it?.toMutableList() ?: mutableListOf())
            }, {
                filmCallback.onFailed(it)
            })
        compositeDisposable.add(disposable)
    }

    fun getUpdates(date: String, filmCallback: FilmCallback) {
        disposable = endpoint.getUpdates(TMDB_API_KEY, date, date).setSchedule(scheduler).subscribe({
            filmCallback.onGotData(0, (it as FilmResponseModel).results.toMutableList())
        }, {
            filmCallback.onFailed(it)
        })
        compositeDisposable.add(disposable)
    }

    fun searchFavorites(category: Int, query: String, page: Int, filmCallback: FilmCallback) {
        val start = if (page == 1) 0 else page * 10 - 10
        disposable = fromCallable { tmdbDao.searchFavorites(category, "%$query%", start) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                filmCallback.onGotData(category, it?.toMutableList() ?: mutableListOf())
            }, {
                filmCallback.onFailed(it)
            })
        compositeDisposable.add(disposable)
    }

    fun isFilmFavorited(callback: FilmDetailCallback, category: Int, id: Int) {
        compositeDisposable.add(fromCallable { tmdbDao.isFilmFavorited(category, id) }
            .setSchedule(scheduler)
            .subscribe({
                if (it == null) callback.onFailed(Throwable())
                else callback.onSuccess(it == 1)
            }, {
                callback.onFailed(it)
            })
        )
    }

    fun insertFavorite(callback: FilmDetailCallback, dataFilm: FilmModel, dataDetail: FilmDetailModel) {
        compositeDisposable.add(fromCallable { tmdbDao.insertFavorite(dataFilm) }
            .subscribeOn(scheduler.io())
            .flatMap { fromCallable { tmdbDao.insertDetailFilm(listOf(dataDetail)) } }
            .observeOn(scheduler.ui())
            .subscribe({
                callback.onSuccess(true)
                updateFavoritesWidget()
            }, {
                callback.onFailed(it)
            })
        )
    }

    fun deleteFavorite(callback: FilmDetailCallback, dataFilm: FilmModel, dataDetail: FilmDetailModel) {
        compositeDisposable.add(fromCallable { tmdbDao.getFavoriteKey(dataFilm.category, dataFilm.id) }
            .subscribeOn(scheduler.io())
            .flatMap { fromCallable { tmdbDao.deleteFavorite(it) } }
            .flatMap { fromCallable { tmdbDao.getDetailKey(dataDetail.category, dataDetail.id) } }
            .flatMap { fromCallable { tmdbDao.deleteDetailFilm(it) } }
            .observeOn(scheduler.ui())
            .subscribe({
                callback.onSuccess(false)
                updateFavoritesWidget()
            }, {
                callback.onFailed(it)
            })
        )
    }

    fun stopLastDisposable() {
        if (::disposable.isInitialized) disposable.dispose()
    }
}