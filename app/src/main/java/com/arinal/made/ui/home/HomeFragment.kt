package com.arinal.made.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.home.adapter.FilmAdapter
import com.arinal.made.utils.EndlessScrollListener
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.extension.visible
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast

class HomeFragment : Fragment() {

    private lateinit var favoriteAdapter: FilmAdapter
    private lateinit var filmAdapter: FilmAdapter
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
            if (isOnFavorite()) {
                pageFavorite = 1
                viewModel.getListFavorite(tabPos).value?.clear()
            } else {
                pageFilm = 1
                viewModel.getListFilm(tabPos).value?.clear()
            }
            getData()
            progressBar.visible()
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
            favoriteAdapter = FilmAdapter(it, favoriteList) { data, index -> viewModel.goToDetail(data.apply { category = tabPos }, index) }
            filmAdapter = FilmAdapter(it, filmList) { data, index -> viewModel.goToDetail(data.apply { category = tabPos }, index) }
        }
        viewModel.initData(tabPos, viewModel.getLang(), pageFilm) { onError(it) }
        viewModel.getListFavorite(tabPos).observe(this, onGotFavorite())
        viewModel.getListFilm(tabPos).observe(this, onGotFilm())
        viewModel.getShowFavorite().observe(this, onShowFavorite())
    }

    private fun getData() = viewModel.getData(tabPos, viewModel.getLang(), if (isOnFavorite()) pageFavorite else pageFilm)

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
        if (recyclerView.adapter == null) recyclerView.adapter = filmAdapter
    }

    private fun onShowFavorite(): Observer<Boolean> = Observer {
        progressBar.visible()
        recyclerView.adapter = if (it) favoriteAdapter else filmAdapter
        if (it && favoriteList.isEmpty()) getData()
        else {
            progressBar.gone()
            txEmpty.gone()
        }
        setScrollListener()
    }

    private fun onError(throwable: Throwable) = runOnUiThread {
        progressBar.gone()
        toast(throwable.localizedMessage ?: "")
    }
}