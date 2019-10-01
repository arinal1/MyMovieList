package com.arinal.made.favorites.ui.main

import android.content.ContentResolver
import android.net.Uri.Builder
import android.net.Uri.parse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.made.favorites.model.FilmModel
import com.arinal.made.favorites.utils.Constants.author
import com.arinal.made.favorites.utils.Constants.scheme
import com.arinal.made.favorites.utils.Constants.segment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainViewModel(private val contentResolver: ContentResolver) : ViewModel() {

    private val scrollTop = mutableListOf<() -> Unit>()
    private lateinit var favoriteList: MutableList<MutableLiveData<MutableList<FilmModel>>>
    private lateinit var gson: Gson

    fun getFavoriteList(category: Int): MutableLiveData<MutableList<FilmModel>> = favoriteList[category]

    fun initData(category: Int, page: Int, gson: Gson, scrollTop: () -> Unit) {
        this.scrollTop.add(scrollTop)
        this.gson = gson
        if (!::favoriteList.isInitialized) favoriteList = mutableListOf()
        if (favoriteList.size < 2) {
            favoriteList.add(MutableLiveData())
            getData(category, page)
        }
    }

    fun getData(category: Int, page: Int) {
        val table = if (category == 0) "movies" else "tvs"
        val start = if (page == 1) 0 else page * 10 - 10
        var uri = Builder().scheme(scheme).authority(author).appendPath(segment).build()
        uri = parse("$uri/$table/$start")
        Thread {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                val dataList = mutableListOf<FilmModel>()
                if (favoriteList[category].value != null) dataList.addAll(favoriteList[category].value!!)
                for (position in 0 until cursor.count) {
                    cursor.moveToNext()
                    val hashMap = HashMap<String, Any>()
                    for (column in 0 until cursor.columnCount) {
                        val columnName = cursor.getColumnName(column)
                        hashMap[columnName] = when (columnName) {
                            "key", "id" -> cursor.getInt(column)
                            "genres" -> {
                                val genres = cursor.getString(column)
                                gson.fromJson(genres, object : TypeToken<List<Int>>() {}.type) as List<Int>
                            }
                            else -> cursor.getString(column)
                        }
                    }
                    dataList.add(gson.fromJson(gson.toJson(hashMap), FilmModel::class.java))
                }
                favoriteList[category].postValue(dataList)
                cursor.close()
            }
        }.start()
    }


    fun removeAt(category: Int, index: Int) = favoriteList[category].postValue(favoriteList[category].value?.apply { removeAt(index) })
    fun clearData(category: Int) = favoriteList[category].value?.clear()
    fun scrollToTop(category: Int) = scrollTop[category]()
}