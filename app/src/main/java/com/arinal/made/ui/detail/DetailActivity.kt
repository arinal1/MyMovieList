package com.arinal.made.ui.detail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.model.DetailModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.utils.Constants
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.toast
import kotlin.math.roundToInt

class DetailActivity : BaseActivity() {

    private lateinit var viewModel: DetailViewModel
    private var category = ""
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = getString(R.string.title_detail)
        initData()
    }

    private fun initData() {
        category = intent.getStringExtra("category") ?: "movie"
        val id = intent.getIntExtra("id", 0)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(category, ApiClient.getTmdb(), SchedulerProviderImpl(), compositeDisposable) as T
            }
        }
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel::class.java)
        viewModel.getData(id, getLang()) { onError(it) }.observe(this, onGotData())
    }

    private fun onGotData(): Observer<DetailModel> = Observer {
        progressBar.gone()
        Glide.with(this).load(Constants.tmdbImgUrl + it.poster_path).into(ivPoster)
        txTitle.text = it.getTitle(category)
        txGenre.text = it.getGenre()
        txRelease.text = it.getRelease(category)
        txDuration.text = it.getDuration(category, getString(R.string.hours), getString(R.string.minutes))
        if (category == "tv") txBudget.visibility = View.GONE else txBudget.text = it.getBudget()
        if (category == "tv") txRevenue.visibility = View.GONE else txRevenue.text = it.getRevenue()
        txOverview.text = it.overview
        circleBar.progress = (it.vote_average * 10).roundToInt()
    }

    private fun onError(throwable: Throwable) {
        progressBar.gone()
        toast(throwable.localizedMessage ?: "Error")
    }

    override fun finish() {
        compositeDisposable.dispose()
        super.finish()
    }
}
