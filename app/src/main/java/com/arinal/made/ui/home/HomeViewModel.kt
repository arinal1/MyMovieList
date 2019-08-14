package com.arinal.made.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.MovieModel
import com.arinal.made.data.model.TvModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.ui.home.adapter.MoviesAdapter
import com.arinal.made.ui.home.adapter.TvShowsAdapter
import com.arinal.made.utils.Constants.tmdbApiKey
import com.arinal.made.utils.scheduler.SchedulerProvider
import com.arinal.made.utils.extension.setSchedule
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(
    private val endpoint: TmdbEndpoint, private val scheduler: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val listMovie = MutableLiveData<MutableList<MovieModel.Result>>()
    private val listTv = MutableLiveData<MutableList<TvModel.Result>>()
    private var onClick: (ExtraDetailModel) -> Unit = { _ -> }
    private var language: () -> String = { "" }

    fun getListMovie(): MutableLiveData<MutableList<MovieModel.Result>> = listMovie

    fun getListTv(): MutableLiveData<MutableList<TvModel.Result>> = listTv

    fun getData(onInit: Boolean, tabPosition: Int, language: String, page: Int, onError: (Throwable) -> Unit) {
        var shouldLoad = (listMovie.value == null || listTv.value == null) && onInit
        shouldLoad = if (!shouldLoad) (listMovie.value != null || listTv.value != null) && !onInit else shouldLoad
        if (shouldLoad) {
            val lang = if (language == "in") "id" else language
            val api = if (tabPosition == 0) endpoint.getMovies(tmdbApiKey, lang, page)
            else endpoint.getTvShows(tmdbApiKey, lang, page)
            compositeDisposable.add(
                api.setSchedule(scheduler).subscribe({
                    if (it is TvModel) {
                        if (listTv.value == null) listTv.value = it.results.toMutableList()
                        else listTv.postValue(listTv.value!!.apply { addAll(it.results) })
                    } else if (it is MovieModel) {
                        if (listMovie.value == null) listMovie.value = it.results.toMutableList()
                        else listMovie.postValue(listMovie.value!!.apply { addAll(it.results) })
                    }
                }, {
                    onError(it)
                })
            )
        }
    }

    fun getAdapter(tabPosition: Int, moviesAdapter: MoviesAdapter, tvAdapter: TvShowsAdapter)
            : Adapter<out ViewHolder> = if (tabPosition == 0) moviesAdapter else tvAdapter

    fun setOnclick(onClick: (ExtraDetailModel) -> Unit) {
        this.onClick = onClick
    }

    fun setLanguage(language: () -> String) {
        this.language = language
    }

    fun goToDetail(categoryId: Int, dataId: Int) = onClick(ExtraDetailModel(categoryId, dataId))
    fun getLang(): String = language()
}