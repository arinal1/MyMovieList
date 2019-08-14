package com.arinal.made.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.model.MovieModel
import com.arinal.made.data.model.TvModel
import com.arinal.made.ui.home.adapter.MoviesAdapter
import com.arinal.made.ui.home.adapter.TvShowsAdapter
import com.arinal.made.utils.EndlessScrollListener
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.extension.visible
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.toast

class HomeFragment : Fragment() {

    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var tvShowsAdapter: TvShowsAdapter
    private lateinit var viewModel: HomeViewModel
    private var tabPosition = 0
    private var movieList: MutableList<MovieModel.Result> = mutableListOf()
    private var page = 1
    private var tvList: MutableList<TvModel.Result> = mutableListOf()

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
        getData(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.addOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore() {
                page += 1
                progressBar.visible()
                getData(false)
            }
        })
        swipeRefresh.onRefresh {
            viewModel.getListTv().value?.clear()
            viewModel.getListMovie().value?.clear()
            swipeRefresh.isRefreshing = false
            page = 1
            getData(false)
            progressBar.visible()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initData() {
        tabPosition = arguments?.getInt(TAB_POSITION, 0) ?: 0
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(HomeViewModel::class.java)
            moviesAdapter = MoviesAdapter(it, movieList) { dataId -> viewModel.goToDetail(tabPosition, dataId) }
            tvShowsAdapter = TvShowsAdapter(it, tvList) { dataId -> viewModel.goToDetail(tabPosition, dataId) }
        }
        viewModel.getListMovie().observe(this, onGotMovie())
        viewModel.getListTv().observe(this, onGotTv())
        recyclerView.adapter = viewModel.getAdapter(tabPosition, moviesAdapter, tvShowsAdapter)
    }

    private fun getData(onInit: Boolean) =
        viewModel.getData(onInit, tabPosition, viewModel.getLang(), page) { onError(it) }

    private fun onGotMovie(): Observer<MutableList<MovieModel.Result>> = Observer {
        progressBar.gone()
        movieList.clear()
        movieList.addAll(it)
        moviesAdapter.notifyDataSetChanged()
    }

    private fun onGotTv(): Observer<MutableList<TvModel.Result>> = Observer {
        progressBar.gone()
        tvList.clear()
        tvList.addAll(it)
        tvShowsAdapter.notifyDataSetChanged()
    }

    private fun onError(throwable: Throwable) {
        progressBar.gone()
        toast(throwable.localizedMessage ?: "")
    }
}