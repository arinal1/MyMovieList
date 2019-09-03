package com.arinal.made.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.data.DataCallback.FilmCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(
    private val endpoint: TmdbEndpoint, private val scheduler: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable, private val tmdbDao: TmdbDao
) : ViewModel() {

    private lateinit var favoriteList: MutableList<MutableLiveData<MutableList<FilmModel>>>
    private lateinit var filmList: MutableList<MutableLiveData<MutableList<FilmModel>>>
    private lateinit var repository: TmdbRepository
    private val showFavorite = MutableLiveData<Boolean>()
    private var language: () -> String = { "" }
    private var onClick: (ExtraDetailModel) -> Unit = { _ -> }
    private val scrollTop = mutableListOf<() -> Unit>()

    fun getListFavorite(category: Int): MutableLiveData<MutableList<FilmModel>> = favoriteList[category]
    fun getListFilm(category: Int): MutableLiveData<MutableList<FilmModel>> = filmList[category]
    fun getShowFavorite(): MutableLiveData<Boolean> = showFavorite

    fun initData(
        category: Int, language: String, page: Int, favoriteCallback: FilmCallback,
        filmCallback: FilmCallback, scrollTop: () -> Unit
    ) {
        this.scrollTop.add(scrollTop)
        repository = TmdbRepository(tmdbDao, compositeDisposable, scheduler, endpoint)
        if (!::filmList.isInitialized) filmList = mutableListOf()
        if (filmList.size < 2) {
            filmList.add(MutableLiveData())
            getData(category, language, page, favoriteCallback, filmCallback)
        }
        if (!::favoriteList.isInitialized) favoriteList = mutableListOf()
        if (favoriteList.size < 2) favoriteList.add(MutableLiveData())
    }

    fun getData(category: Int, language: String, page: Int, favoriteCallback: FilmCallback, filmCallback: FilmCallback) {
        if (showFavorite.value == true) repository.getFavorites(page, category, favoriteCallback)
        else repository.getFilms(category, language, page, filmCallback)
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
    fun scrollToTop(category: Int) = scrollTop[category]()

    fun addFavorite(category: Int, data: MutableList<FilmModel>) {
        if (favoriteList[category].value == null) favoriteList[category].postValue(data)
        else favoriteList[category].postValue(favoriteList[category].value?.apply { addAll(data) })
    }

    fun addFavoriteFromFilm(category: Int, index: Int) = addFavorite(category, mutableListOf(filmList[category].value!![index]))

    fun addFilm(category: Int, data: MutableList<FilmModel>) {
        if (filmList[category].value == null) filmList[category].value = data
        else filmList[category].postValue(filmList[category].value!!.apply { addAll(data) })
    }

    fun deleteFavorite(category: Int, index: Int, fromFavorite: Boolean) {
        val favIndex = if (fromFavorite) index else {
            var idx = -1
            for ((i, data) in favoriteList[category].value!!.withIndex())
                if (data.id == filmList[category].value?.get(index)!!.id) idx = i
            idx
        }
        if (favIndex != -1) favoriteList[category].postValue(favoriteList[category].value?.apply { removeAt(favIndex) })
    }

    fun onRefresh() = repository.stopLastDisposable()
}