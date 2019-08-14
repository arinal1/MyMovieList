package com.arinal.made.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.data.model.DetailModel
import com.arinal.made.data.network.TmdbEndpoint
import com.arinal.made.utils.Constants.tmdbApiKey
import com.arinal.made.utils.extension.setSchedule
import com.arinal.made.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(
    private val tabPosition: Int, private val endpoint: TmdbEndpoint,
    private val scheduler: SchedulerProvider, private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val detail = MutableLiveData<DetailModel>()

    fun getData(id: Int, language: String, onError: (Throwable) -> Unit): MutableLiveData<DetailModel> {
        val lang = if (language == "in") "id" else language
        val api = if (tabPosition == 0) endpoint.getDetailMovie(id, tmdbApiKey, lang)
        else endpoint.getDetailTvShow(id, tmdbApiKey, lang)
        compositeDisposable.add(
            api.setSchedule(scheduler).subscribe({
                detail.postValue(it as DetailModel)
            }, {
                onError(it)
            })
        )
        return detail
    }
}