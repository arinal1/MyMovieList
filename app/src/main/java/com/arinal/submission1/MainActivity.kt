package com.arinal.submission1

import android.os.Bundle
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.arinal.submission1.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var data: List<MovieModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initView()
    }

    private fun initData(){
        val judul = resources.getStringArray(R.array.judul)
        val deskripsi = resources.getStringArray(R.array.deskripsi)
        val rilis = resources.getStringArray(R.array.tgl_rilis)
        val durasi = resources.getStringArray(R.array.durasi)
        val budget = resources.getStringArray(R.array.budget)
        val revenue = resources.getStringArray(R.array.revenue)
        val skor = resources.getIntArray(R.array.skor)
        val genre = resources.getStringArray(R.array.genre)
        data = generateData(judul, deskripsi, rilis, durasi, budget, revenue, skor, genre)
        listView.adapter = MainAdapter(this, data)
    }
    
    private fun generateData(judul: Array<String>, deskripsi: Array<String>, rilis: Array<String>, durasi: Array<String>,
                             budget: Array<String>, revenue: Array<String>, skor: IntArray, genre: Array<String>): List<MovieModel> {
        val datas = mutableListOf<MovieModel>()
        for (i in 0 until judul.size){
            val poster = when(i){
                0 -> R.drawable.poster_a_star
                1 -> R.drawable.poster_aquaman
                2 -> R.drawable.poster_avengerinfinity
                3 -> R.drawable.poster_birdbox
                4 -> R.drawable.poster_bohemian
                5 -> R.drawable.poster_bumblebee
                6 -> R.drawable.poster_creed
                7 -> R.drawable.poster_deadpool
                8 -> R.drawable.poster_dragon
                else -> R.drawable.poster_dragonball
            }
            val data = MovieModel(i, judul[i], deskripsi[i], rilis[i], durasi[i], budget[i], revenue[i], skor[i], genre[i], poster)
            datas.add(data)
        }
        return datas.toList()
    }

    private fun initView(){
        val header = layoutInflater.inflate(R.layout.item_main_header, listView, false) as ViewGroup
        listView.addHeaderView(header, null, false)
        listView.onItemClickListener = OnItemClickListener {_,_,i,_-> startActivity<DetailActivity>("data" to data[i-1])}
    }
}
