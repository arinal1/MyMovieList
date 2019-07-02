package com.arinal.made.ui.home

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
import com.arinal.made.ui.home.HomeAdapter.ItemViewHolder

class HomeAdapter(private val context: Context, private val data: List<FilmModel>, private val mInterface: HomeInterface) : Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.poster.setImageResource(data[position].poster)
        holder.judul.text = data[position].judul
        holder.rilis.text = data[position].rilis
        holder.genre.text = data[position].genre
        if (position == data.size - 1) holder.separator.visibility = View.GONE
        holder.txSinopsis.text = if (data[position].deskripsi.length >= 136) {
            val sinopsis = data[position].deskripsi.substring(0, 115)
            "${sinopsis.substring(0, sinopsis.lastIndexOf(" "))}..."
        } else data[position].deskripsi
    }

    inner class ItemViewHolder(view: View) : ViewHolder(view) {
        init {
            super.itemView.setOnClickListener { mInterface.goToDetail(data[adapterPosition]) }
        }

        val poster: ImageView = view.findViewById(R.id.ivPoster)
        val judul: TextView = view.findViewById(R.id.txTitle)
        val rilis: TextView = view.findViewById(R.id.txRelease)
        val genre: TextView = view.findViewById(R.id.txGenre)
        val txSinopsis: TextView = view.findViewById(R.id.txTitleOverview)
        val separator: View = view.findViewById(R.id.separator)
    }
}