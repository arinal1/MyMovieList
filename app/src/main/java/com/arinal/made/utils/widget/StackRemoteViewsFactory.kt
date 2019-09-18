package com.arinal.made.utils.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.arinal.made.R
import com.arinal.made.data.local.TmdbDao
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.utils.Constants.tmdbImgUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL

class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val favoriteList = mutableListOf<FilmModel>()
    private lateinit var tmdbDao: TmdbDao

    override fun getLoadingView(): RemoteViews = RemoteViews(context.packageName, R.layout.item_widget)
    override fun getItemId(id: Int): Long = id.toLong()
    override fun hasStableIds(): Boolean = true
    override fun getCount(): Int = favoriteList.size
    override fun getViewTypeCount(): Int = 1
    override fun onDestroy() {}
    override fun onCreate() {
        tmdbDao = TmdbDatabase.getInstanceOnMainThread(context).tmdbDao()
    }

    override fun onDataSetChanged() {
        favoriteList.clear()
        favoriteList.addAll(tmdbDao.getFavorites(0, 0))
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.item_widget)
        val url = tmdbImgUrl + favoriteList[position].poster
        val bitmap = Glide.with(context).asBitmap().load(url).centerCrop().submit(SIZE_ORIGINAL, SIZE_ORIGINAL).get()
        remoteViews.setImageViewBitmap(R.id.imageView, bitmap!!)
        val data = ExtraDetailModel(favoriteList[position], position, false)
        val fillInIntent = Intent().putExtra("data", data).putExtra("goToDetail", true)
        remoteViews.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return remoteViews
    }
}