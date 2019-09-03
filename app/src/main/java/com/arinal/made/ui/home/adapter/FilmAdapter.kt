package com.arinal.made.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.home.adapter.FilmAdapter.ItemViewHolder
import com.arinal.made.utils.Constants.tmdbImgUrl
import com.arinal.made.utils.extension.gone
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_film.view.*
import kotlinx.android.synthetic.main.item_film_header.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class FilmAdapter(
    private val dataList: List<FilmModel>,
    private val glide: RequestManager,
    private val layoutInflater: LayoutInflater,
    private val onClick: (FilmModel, Int) -> Unit,
    private val onSearch: () -> Unit,
    private val txSearch: String
) : Adapter<ItemViewHolder>() {

    override fun getItemCount(): Int = dataList.size + 1

    override fun getItemViewType(position: Int): Int = if (position == 0) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = if (viewType == 0) R.layout.item_film_header else R.layout.item_film
        return ItemViewHolder(layoutInflater.inflate(layout, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(position)

    inner class ItemViewHolder(private val view: View) : ViewHolder(view) {

        fun bind(position: Int) {
            if (position == 0) {
                if (txSearch == "") {
                    view.gone()
                    view.layoutParams = RecyclerView.LayoutParams(0, 15)
                } else {
                    view.onClick { onSearch() }
                    view.txSearch.text = txSearch
                }
            } else {
                val data = dataList[position - 1]
                glide.load(tmdbImgUrl + data.poster).into(view.ivPoster)
                view.txTitle.text = data.title
                view.txRelease.text = data.getDate()
                view.txGenre.text = data.getGenre()
                if (position == dataList.size) view.separator.gone()
                view.txOverview.text = if (data.overview.length < 136) data.overview
                else {
                    val sinopsis = data.overview.substring(0, 115)
                    "${sinopsis.substring(0, sinopsis.lastIndexOf(" "))}..."
                }
                view.onClick { onClick(data, position - 1) }
            }
        }
    }
}