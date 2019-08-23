package com.arinal.made.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.data.DataCallback.FilmDetailCallback
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(private val category: Int, endpoint: TmdbEndpoint, tmdbDao: TmdbDao,
    compositeDisposable: CompositeDisposable, scheduler: SchedulerProvider, private val onError: (Throwable) -> Unit
) : ViewModel(), FilmDetailCallback {

    private val detailData = MutableLiveData<FilmDetailModel>()
    private val isFavorited = MutableLiveData<Boolean>()
    private val repository = TmdbRepository(tmdbDao, compositeDisposable, scheduler, endpoint)

    fun getDetailData(): MutableLiveData<FilmDetailModel> = detailData
    fun getIsFavorited(): MutableLiveData<Boolean> = isFavorited

    fun getData(id: Int, language: String) {
        val lang = if (language == "in") "id" else language
        repository.getDetailFilm(category, id, lang, this)
    }

    fun getFavorite(id: Int) = repository.isFilmFavorited(this, category, id)

    fun onClickFavorite(dataFilm: FilmModel, dataDetail: FilmDetailModel) {
        if (isFavorited.value == true) repository.deleteFavorite(this, dataFilm, dataDetail)
        else repository.insertFavorite(this, dataFilm, dataDetail)
    }

    override fun onFailed(throwable: Throwable) = onError(throwable)
    override fun onGotData(data: FilmDetailModel) = detailData.postValue(data)
    override fun onSuccess(add: Boolean) = isFavorited.postValue(add)
}