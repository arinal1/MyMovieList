package com.arinal.made.data.local

import android.content.res.Resources
import com.arinal.made.R.array.*
import com.arinal.made.R.drawable.*
import com.arinal.made.data.model.FilmModel

class FilmData(private val resources: Resources) {

    fun getMovie(): List<FilmModel>{
        val judul = resources.getStringArray(movies_judul)
        val deskripsi = resources.getStringArray(movies_deskripsi)
        val rilis = resources.getStringArray(movies_tgl_rilis)
        val durasi = resources.getStringArray(movies_durasi)
        val budget = resources.getStringArray(movies_budget)
        val revenue = resources.getStringArray(movies_revenue)
        val skor = resources.getIntArray(movies_skor)
        val genre = resources.getStringArray(movies_genre)
        return generateData(true, judul, deskripsi, rilis, durasi, budget, revenue, skor, genre)
    }

    fun getTvShow(): List<FilmModel>{
        val judul = resources.getStringArray(tv_shows_judul)
        val deskripsi = resources.getStringArray(tv_shows_deskripsi)
        val rilis = resources.getStringArray(tv_shows_tgl_rilis)
        val durasi = resources.getStringArray(tv_shows_durasi)
        val skor = resources.getIntArray(tv_shows_skor)
        val genre = resources.getStringArray(tv_shows_genre)
        return generateData(false, judul, deskripsi, rilis, durasi, null, null, skor, genre)}

    private fun generateData(isMovie: Boolean, judul: Array<String>, deskripsi: Array<String>, rilis: Array<String>, durasi: Array<String>,
                             budgets: Array<String>?, revenues: Array<String>?, skor: IntArray, genre: Array<String>): List<FilmModel> {
        val datas = mutableListOf<FilmModel>()
        for (i in 0 until judul.size){
            val poster = when(i){
                0 -> if (isMovie) poster_a_star else poster_opm
                1 -> if (isMovie) poster_aquaman else poster_the_flash
                2 -> if (isMovie) poster_avengerinfinity else poster_arrow
                3 -> if (isMovie) poster_birdbox else poster_fairy_tail
                4 -> if (isMovie) poster_bohemian else poster_dragon_ball
                5 -> if (isMovie) poster_bumblebee else poster_demon_slayer
                6 -> if (isMovie) poster_creed else poster_see_no_evil
                7 -> if (isMovie) poster_deadpool else poster_got
                8 -> if (isMovie) poster_dragon else poster_marvels_agents
                else -> if (isMovie) poster_dragonball else poster_fear_the_walking_dead
            }
            val budget = if (budgets == null) "" else budgets[i]
            val revenue = if (revenues == null) "" else revenues[i]
            val data = FilmModel(i, judul[i], deskripsi[i], rilis[i], durasi[i], budget, revenue, skor[i], genre[i], poster)
            datas.add(data)
        }
        return datas.toList()
    }
}