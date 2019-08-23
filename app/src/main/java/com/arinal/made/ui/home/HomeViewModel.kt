package com.arinal.made.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.BuildConfig.TMDB_API_KEY
import com.arinal.made.data.DataCallback.FilmCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.model.FilmResponseModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.extension.setSchedule
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(
    private val endpoint: TmdbEndpoint, private val scheduler: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable, private val tmdbDao: TmdbDao
) : ViewModel() {

    private lateinit var favoriteList: MutableList<MutableLiveData<MutableList<FilmModel>>>
    private lateinit var filmList: MutableList<MutableLiveData<MutableList<FilmModel>>>
    private lateinit var onError: (Throwable) -> Unit
    private lateinit var repository: TmdbRepository
    private val movieCallback = object : FilmCallback {
        override fun onFailed(throwable: Throwable) = onError(throwable)
        override fun onGotData(data: MutableList<FilmModel>) =
            if (favoriteList[0].value == null) favoriteList[0].postValue(data)
            else favoriteList[0].postValue(favoriteList[0].value?.apply { addAll(data) })
    }
    private val showFavorite = MutableLiveData<Boolean>()
    private val tvCallback = object : FilmCallback {
        override fun onFailed(throwable: Throwable) = onError(throwable)
        override fun onGotData(data: MutableList<FilmModel>) =
            if (favoriteList[1].value == null) favoriteList[1].postValue(data)
            else favoriteList[1].postValue(favoriteList[1].value?.apply { addAll(data) })
    }
    private var language: () -> String = { "" }
    private var onClick: (ExtraDetailModel) -> Unit = { _  -> }

    fun getListFavorite(category: Int): MutableLiveData<MutableList<FilmModel>> = favoriteList[category]
    fun getListFilm(category: Int): MutableLiveData<MutableList<FilmModel>> = filmList[category]
    fun getShowFavorite(): MutableLiveData<Boolean> = showFavorite

    fun initData(category: Int, language: String, page: Int, onError: (Throwable) -> Unit) {
        this.onError = onError
        repository = TmdbRepository(tmdbDao, compositeDisposable, scheduler, endpoint)
        if (!::filmList.isInitialized) filmList = mutableListOf()
        if (filmList.size < 2) {
            filmList.add(MutableLiveData())
            getData(category, language, page)
        }
        if (!::favoriteList.isInitialized) favoriteList = mutableListOf()
        if (favoriteList.size < 2) favoriteList.add(MutableLiveData())
    }

    fun getData(category: Int, language: String, page: Int) {
        if (showFavorite.value == true) {
            if (category == 0) repository.getFavoriteMovies(page, movieCallback)
            else repository.getFavoriteTvShows(page, tvCallback)
        } else {
            val lang = if (language == "in") "id" else language
            val api = if (category == 0) endpoint.getMovies(TMDB_API_KEY, lang, page)
            else endpoint.getTvShows(TMDB_API_KEY, lang, page)
            compositeDisposable.add(
                api.setSchedule(scheduler).subscribe({
                    it as FilmResponseModel
                    if (filmList[category].value == null) filmList[category].value = it.results.toMutableList()
                    else filmList[category].postValue(filmList[category].value!!.apply { addAll(it.results) })
                }, {
                    onError(it)
                })
            )
        }
    }

    fun showFavorites(show: Boolean) = showFavorite.postValue(show)

    fun setOnclick(onClick: (ExtraDetailModel) -> Unit) {
        this.onClick = onClick
    }

    fun setLanguage(language: () -> String) {
        this.language = language
    }

    fun goToDetail(extra: FilmModel, index: Int) = onClick(ExtraDetailModel(extra, index, showFavorite.value == true))
    fun getLang(): String = language()
    fun addFavorite(category: Int, index: Int) = favoriteList[category].postValue(
        favoriteList[category].value?.apply { add(filmList[category].value!![index]) }
    )
    fun deleteFavorite(category: Int, index: Int) = favoriteList[category].postValue(
        favoriteList[category].value?.apply { removeAt(index) }
    )
}