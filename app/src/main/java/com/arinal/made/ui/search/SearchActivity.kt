package com.arinal.made.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.TmdbRepository
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.ui.detail.DetailActivity
import com.arinal.made.ui.home.adapter.FilmAdapter
import com.arinal.made.utils.EndlessScrollListener
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.extension.visible
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class SearchActivity : BaseActivity() {

    private lateinit var filmAdapter: FilmAdapter
    private lateinit var viewModel: SearchViewModel
    private val compositeDisposable = CompositeDisposable()
    private var category = 0
    private var filmList: MutableList<FilmModel> = mutableListOf()
    private var isFavorite = false
    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
        initData()
    }

    private fun initView() {
        supportActionBar?.hide()
        searchView.requestFocus()
        btnBack.onClick { onBackPressed() }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(query: String?): Boolean {
                page = 1
                progressBar.visible()
                viewModel.onQueryChanged()
                viewModel.searchFilm(isFavorite, category, query, getLang(), page)
                return true
            }
        })
        recyclerView.addOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore() {
                page += 1
                progressBar.visible()
                viewModel.searchFilm(isFavorite, category, searchView.query, getLang(), page)
            }
        })
    }

    private fun initData() {
        category = intent.getIntExtra("category", 0)
        isFavorite = intent.getBooleanExtra("isFavorite", false)
        searchView.queryHint = getString(
            if (isFavorite) if (category == 0) R.string.search_favorited_movie else R.string.search_favorited_tv
            else if (category == 0) R.string.search_movie else R.string.search_tv
        )
        val layoutInflater = LayoutInflater.from(this)
        val glide = Glide.with(this)
        val onClick: (FilmModel, Int) -> Unit = { data, index ->
            data.category = category
            val extra = ExtraDetailModel(data, index, isFavorite)
            startActivityForResult<DetailActivity>(getReqCode(isFavorite), "data" to extra)
        }
        filmAdapter = FilmAdapter(filmList, glide, layoutInflater, onClick, {}, "")
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val repository = TmdbRepository(
            TmdbDatabase.getInstance(applicationContext).tmdbDao(),
            compositeDisposable, SchedulerProviderImpl(), ApiClient.getTmdb()
        )
        viewModel.initData(repository) { onError(it) }
        viewModel.getListFilm().observe(this, onGotFilm())
    }

    private fun getReqCode(fromFavorite: Boolean) = if (fromFavorite) 2 else 1

    private fun onGotFilm(): Observer<MutableList<FilmModel>> = Observer {
        progressBar.gone()
        filmList.clear()
        filmList.addAll(it)
        txEmpty.visibility = (if (filmList.size == 0 && searchView.query.isNotEmpty()) VISIBLE else GONE)
        filmAdapter.notifyDataSetChanged()
        if (recyclerView.adapter == null) recyclerView.adapter = filmAdapter
    }

    private fun onError(throwable: Throwable) = runOnUiThread {
        progressBar.gone()
        toast(throwable.localizedMessage ?: "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            val removed = !data.getBooleanExtra("added", false)
            val index = data.getIntExtra("index", 0)
            if (removed) viewModel.deleteFavorite(index)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        compositeDisposable.dispose()
        super.finish()
    }
}
