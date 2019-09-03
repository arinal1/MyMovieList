package com.arinal.made.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.data.DataCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.model.FilmModel

class SearchViewModel : ViewModel() {

    private lateinit var filmList: MutableLiveData<MutableList<FilmModel>>
    private lateinit var onError: (Throwable) -> Unit
    private lateinit var repository: TmdbRepository
    private val filmCallback = object : DataCallback.FilmCallback {
        override fun onFailed(throwable: Throwable) = onError(throwable)
        override fun onGotData(category: Int, data: MutableList<FilmModel>) {
            if (filmList.value == null) filmList.postValue(data)
            else filmList.postValue(filmList.value!!.apply { addAll(data) })
        }
    }

    fun getListFilm(): MutableLiveData<MutableList<FilmModel>> = filmList

    fun initData(repository: TmdbRepository, onError: (Throwable) -> Unit) {
        this.repository = repository
        this.onError = onError
        if (!::filmList.isInitialized) filmList = MutableLiveData()
    }

    fun searchFilm(isFavorite: Boolean, category: Int, query: CharSequence?, language: String, page: Int) {
        if (query.isNullOrEmpty()) filmList.postValue(mutableListOf())
        else {
            if (isFavorite) repository.searchFavorites(category, query.toString(), page,filmCallback)
            else repository.searchFilms(category, language, query.toString(), page, filmCallback)
        }
    }

    fun deleteFavorite(index: Int) = filmList.postValue(filmList.value?.apply { removeAt(index) })

    fun onQueryChanged() {
        filmList.postValue(mutableListOf())
        repository.stopLastDisposable()
    }
}