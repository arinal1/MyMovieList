package com.arinal.made.favorites.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSmoothScroller
import com.arinal.made.favorites.R
import com.arinal.made.favorites.model.FilmModel
import com.arinal.made.favorites.ui.detail.DetailActivity
import com.arinal.made.favorites.ui.main.adapter.MainAdapter
import com.arinal.made.favorites.utils.EndlessScrollListener
import com.bumptech.glide.Glide.with
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.swipeRefresh
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivityForResult

class MainFragment : Fragment() {

    private val dataList = mutableListOf<FilmModel>()
    private var page = 1
    private var tabPos = 0
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel

    companion object {
        private const val TAB_POSITION = "position"
        @JvmStatic
        fun newInstance(tabPosition: Int): MainFragment {
            return MainFragment().apply {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefresh.onRefresh {
            swipeRefresh.isRefreshing = false
            dataList.clear()
            page = 1
            progressBar.visibility = VISIBLE
            viewModel.clearData(tabPos)
            viewModel.getData(tabPos, page)
        }
        recyclerView.addOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore() {
                if (dataList.isNotEmpty()) page += 1
                progressBar.visibility = VISIBLE
                viewModel.getData(tabPos, page)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun initData() {
        tabPos = arguments?.getInt(TAB_POSITION, 0) ?: 0
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
            val layoutInflater = from(context)
            val glide = with(this)
            val onClick: (Int, Int) -> Unit = { id, index ->
                startActivityForResult<DetailActivity>(0, "id" to id, "category" to tabPos, "index" to index)
            }
            adapter = MainAdapter(glide, layoutInflater, dataList, onClick)
        }
        viewModel.initData(tabPos, page, Gson(), getScrollTopAction())
        viewModel.getFavoriteList(tabPos).observe(this, onGotData())
    }

    private fun getScrollTopAction(): () -> Unit {
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }
        return {
            smoothScroller.targetPosition = 0
            recyclerView.layoutManager!!.startSmoothScroll(smoothScroller)
        }
    }

    private fun onGotData(): Observer<MutableList<FilmModel>> = Observer {
        progressBar.visibility = GONE
        dataList.clear()
        dataList.addAll(it)
        adapter.notifyDataSetChanged()
        txEmpty.visibility = if (it.isEmpty()) VISIBLE else GONE
        if (recyclerView.adapter == null) recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 0 && data != null) {
            viewModel.removeAt(tabPos, data.getIntExtra("index", 0))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}