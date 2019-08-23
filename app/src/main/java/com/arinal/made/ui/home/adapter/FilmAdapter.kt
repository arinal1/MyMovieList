package com.arinal.made.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.home.adapter.FilmAdapter.ItemViewHolder
import com.arinal.made.utils.Constants.tmdbImgUrl
import com.bumptech.glide.Glide

class FilmAdapter(
    private val context: Context,
    private val data: List<FilmModel>,
    private val onClick: (FilmModel, Int) -> Unit
) : Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Glide.with(context).load(tmdbImgUrl + data[position].poster).into(holder.poster)
        holder.judul.text = data[position].title
        holder.rilis.text = data[position].getDate()
        holder.genre.text = data[position].getGenre()
        if (position == data.size - 1) holder.separator.visibility = View.GONE
        holder.txSinopsis.text = if (data[position].overview.length >= 136) {
            val sinopsis = data[position].overview.substring(0, 115)
            "${sinopsis.substring(0, sinopsis.lastIndexOf(" "))}..."
        } else data[position].overview
    }

    inner class ItemViewHolder(view: View) : ViewHolder(view) {
        init {
            super.itemView.setOnClickListener { onClick(data[adapterPosition], adapterPosition) }
        }

        val poster: ImageView = view.findViewById(R.id.ivPoster)
        val judul: TextView = view.findViewById(R.id.txTitle)
        val rilis: TextView = view.findViewById(R.id.txRelease)
        val genre: TextView = view.findViewById(R.id.txGenre)
        val txSinopsis: TextView = view.findViewById(R.id.txTitleOverview)
        val separator: View = view.findViewById(R.id.separator)
    }
}