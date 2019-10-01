package com.arinal.made.favorites.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arinal.made.favorites.R
import com.arinal.made.favorites.model.FilmModel
import com.arinal.made.favorites.ui.main.adapter.MainAdapter.ItemViewHolder
import com.arinal.made.favorites.utils.Constants.tmdbImgUrl
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_main.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainAdapter(
    private val glide: RequestManager,
    private val layoutInflater: LayoutInflater,
    private val dataList: List<FilmModel>,
    private val onClick: (FilmModel, Int) -> Unit
) : Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(layoutInflater.inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(position)

    inner class ItemViewHolder(private val view: View) : ViewHolder(view) {

        fun bind(position: Int) {
            val data = dataList[position]
            val url = "$tmdbImgUrl${data.poster}"
            glide.load(url).into(view.ivPoster)
            view.txTitle.text = data.title
            view.txRelease.text = data.getDate()
            view.txGenre.text = data.getGenre()
            if (position == dataList.size - 1) view.separator.visibility = View.GONE
            view.txOverview.text = if (data.overview.length >= 136) {
                val sinopsis = data.overview.substring(0, 115)
                "${sinopsis.substring(0, sinopsis.lastIndexOf(" "))}..."
            } else data.overview
            view.onClick { onClick(data, position) }
        }
    }
}