package com.arinal.submission1.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arinal.submission1.MovieModel
import com.arinal.submission1.R
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initData()
    }

    private fun initData(){
        val data = intent.getParcelableExtra<MovieModel>("data")
        ivPoster.setImageResource(data.poster)
        txJudul.text = data.judul
        txGenre.text = data.genre
        txRilis.text = data.rilis
        txDurasi.text = data.durasi
        txBudget.text = data.budget
        txRevenue.text = data.revenue
        txDeskripsi.text = data.deskripsi
        circleBar.progress = data.skor
    }
}
