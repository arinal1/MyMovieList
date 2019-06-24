package com.arinal.submission1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MainAdapter(private val context: Context, private val data: List<MovieModel>): BaseAdapter() {

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.bind(data[i])
        return view
    }

    override fun getItem(i: Int): Any = data[i]

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getCount(): Int = data.size

    private class ViewHolder(view: View) {

        private val poster: ImageView = view.findViewById(R.id.ivPoster)
        private val judul: TextView = view.findViewById(R.id.txJudul)
        private val rilis: TextView = view.findViewById(R.id.txRilis)
        private val genre: TextView = view.findViewById(R.id.txGenre)
        private val txSinopsis: TextView = view.findViewById(R.id.txSinopsis)

        fun bind(data: MovieModel){
            poster.setImageResource(data.poster)
            judul.text = data.judul
            rilis.text = data.rilis
            genre.text = data.genre
            var sinopsis = ""
            if (data.deskripsi.length >= 136) {
                sinopsis = data.deskripsi.substring(0, 115)
                sinopsis = "${sinopsis.substring(0, sinopsis.lastIndexOf(" "))}..."
            } else data.deskripsi

            txSinopsis.text = sinopsis
        }
    }
}