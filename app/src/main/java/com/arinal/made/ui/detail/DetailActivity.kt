package com.arinal.made.ui.detail

import android.os.Bundle
import android.view.View
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = getString(R.string.title_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
    }

    private fun initData(){
        val data = intent.getParcelableExtra<FilmModel>("data")!!
        ivPoster.setImageResource(data.poster)
        txTitle.text = data.judul
        txGenre.text = data.genre
        txRelease.text = data.rilis
        txDuration.text = data.durasi
        if (data.budget == "") txBudget.visibility = View.GONE else txBudget.text = data.budget
        if (data.revenue == "") txRevenue.visibility = View.GONE else txRevenue.text = data.revenue
        txOverview.text = data.deskripsi
        circleBar.progress = data.skor
    }
}
