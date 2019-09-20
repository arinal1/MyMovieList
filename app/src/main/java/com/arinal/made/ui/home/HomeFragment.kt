package com.arinal.made.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSmoothScroller
import com.arinal.made.R
import com.arinal.made.data.DataCallback.FilmCallback
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.home.adapter.FilmAdapter
import com.arinal.made.ui.search.SearchActivity
import com.arinal.made.utils.EndlessScrollListener
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.extension.visible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class HomeFragment : Fragment() {

    private lateinit var favoriteAdapter: FilmAdapter
    private lateinit var favoriteCallback: FilmCallback
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var filmCallback: FilmCallback
    private lateinit var scrollListener: EndlessScrollListener
    private lateinit var viewModel: HomeViewModel
    private var favoriteList: MutableList<FilmModel> = mutableListOf()
    private var filmList: MutableList<FilmModel> = mutableListOf()
    private var pageFavorite = 1
    private var pageFilm = 1
    private var tabPos = 0

    companion object {
        private const val TAB_POSITION = "position"
        @JvmStatic
        fun newInstance(tabPosition: Int): HomeFragment {
            return HomeFragment().apply {
                arguments = Bundle().apply {
                    putInt(TAB_POSITION, tabPosition)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setScrollListener()
        swipeRefresh.onRefresh {
            swipeRefresh.isRefreshing = false
            viewModel.onRefresh()
            if (isOnFavorite()) {
                pageFavorite = 1
                viewModel.getListFavorite(tabPos).value?.clear()
            } else {
                pageFilm = 1
                viewModel.getListFilm(tabPos).value?.clear()
            }
            setScrollListener()
            progressBar.visible()
            getData()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setScrollListener() {
        if (::scrollListener.isInitialized) recyclerView.removeOnScrollListener(scrollListener)
        scrollListener = object : EndlessScrollListener() {
            override fun onLoadMore() {
                if (isOnFavorite()) {
                    if (favoriteList.isNotEmpty()) pageFavorite += 1
                } else if (filmList.isNotEmpty()) pageFilm += 1
                progressBar.visible()
                getData()
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun initData() {
        tabPos = arguments?.getInt(TAB_POSITION, 0) ?: 0
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(HomeViewModel::class.java)
            val layoutInflater = LayoutInflater.from(it)
            val glide = Glide.with(it)
            val txSearch = getString(if (tabPos == 0) R.string.search_movie else R.string.search_tv)
            val txSearchFavorite = getString(if (tabPos == 0) R.string.search_favorited_movie else R.string.search_favorited_tv)
            val onClick: (FilmModel, Int) -> Unit = { data, index -> viewModel.goToDetail(data.apply { category = tabPos }, index) }
            val onSearch: () -> Unit = { startActivity<SearchActivity>("category" to tabPos, "isFavorite" to isOnFavorite()) }
            favoriteAdapter = FilmAdapter(favoriteList, glide, layoutInflater, onClick, onSearch, txSearchFavorite)
            filmAdapter = FilmAdapter(filmList, glide, layoutInflater, onClick, onSearch, txSearch)
        }
        favoriteCallback = object : FilmCallback {
            override fun onFailed(throwable: Throwable) = onError(throwable)
            override fun onGotData(category: Int, data: MutableList<FilmModel>) = viewModel.addFavorite(category, data)
        }
        filmCallback = object : FilmCallback {
            override fun onFailed(throwable: Throwable) = onError(throwable)
            override fun onGotData(category: Int, data: MutableList<FilmModel>) = viewModel.addFilm(category, data)
        }
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }
        val scrollTop = {
            smoothScroller.targetPosition = 0
            recyclerView.layoutManager!!.startSmoothScroll(smoothScroller)
        }
        viewModel.initData(tabPos, viewModel.getLang(), pageFilm, favoriteCallback, filmCallback, scrollTop)
        viewModel.getListFavorite(tabPos).observe(this, onGotFavorite())
        viewModel.getListFilm(tabPos).observe(this, onGotFilm())
        viewModel.getShowFavorite().observe(this, onShowFavorite())
    }

    private fun getData() = viewModel.getData(
        tabPos, viewModel.getLang(), if (isOnFavorite()) pageFavorite else pageFilm, favoriteCallback, filmCallback
    )

    private fun isOnFavorite(): Boolean = viewModel.getShowFavorite().value == true

    private fun onGotFavorite(): Observer<MutableList<FilmModel>> = Observer {
        progressBar.gone()
        favoriteList.clear()
        favoriteList.addAll(it)
        favoriteAdapter.notifyDataSetChanged()
        if (it.isEmpty()) txEmpty.visible() else txEmpty.gone()
        if (recyclerView.adapter == null) recyclerView.adapter = favoriteAdapter
    }

    private fun onGotFilm(): Observer<MutableList<FilmModel>> = Observer {
        progressBar.gone()
        filmList.clear()
        filmList.addAll(it)
        filmAdapter.notifyDataSetChanged()
        if (it.isEmpty()) txEmpty.visible() else txEmpty.gone()
        if (recyclerView.adapter == null) recyclerView.adapter = filmAdapter
    }

    private fun onShowFavorite(): Observer<Boolean> = Observer {
        progressBar.visible()
        recyclerView.adapter = if (it) favoriteAdapter else filmAdapter
        txEmpty.visibility = if ((it && favoriteList.isEmpty()) || (!it && filmList.isEmpty())) VISIBLE else GONE
        progressBar.gone()
        setScrollListener()
    }

    private fun onError(throwable: Throwable) = runOnUiThread {
        progressBar.gone()
        if (isOnFavorite() && favoriteList.isEmpty()) txEmpty.visible()
        else if (!isOnFavorite() && filmList.isEmpty()) txEmpty.visible()
        toast(throwable.localizedMessage ?: "")
    }
}