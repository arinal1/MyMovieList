package com.arinal.made.favorites.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {
    private var mPreviousTotal = 0
    private var mLoading = true
    private var firstInit = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = recyclerView.layoutManager!!.itemCount
        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false
                mPreviousTotal = totalItemCount
            }
        }
        val visibleThreshold = 5
        if (totalItemCount != 0 && !mLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            if (firstInit && totalItemCount > 1) firstInit = false
            else {
                onLoadMore()
                mLoading = true
            }
        }
    }

    abstract fun onLoadMore()

}