package com.arinal.made.favorites.ui.detail

import android.animation.ValueAnimator.ofInt
import android.graphics.BlendModeColorFilter
import android.graphics.Color.parseColor
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.drawable.Drawable
import android.net.Uri.Builder
import android.net.Uri.parse
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.get
import com.arinal.made.favorites.R
import com.arinal.made.favorites.model.FilmDetailModel
import com.arinal.made.favorites.model.FilmDetailModel.Genre
import com.arinal.made.favorites.utils.Constants.author
import com.arinal.made.favorites.utils.Constants.scheme
import com.arinal.made.favorites.utils.Constants.segment
import com.arinal.made.favorites.utils.Constants.tmdbImgUrl
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout.Behavior
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_detail.*
import android.graphics.BlendMode.SRC_ATOP as BlendMode_SRC_ATOP

class DetailActivity : AppCompatActivity() {

    private var category = 0
    private var dwFavorite: Drawable? = null
    private var dwFavorited: Drawable? = null
    private var favoriteMenu: MenuItem? = null
    private var filmTitle = ""
    private var isFavorited = true
    private var posterHeight = 0
    private var uri = parse("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        setAppbarListener()
    }

    private fun setAppbarListener() {
        appbar.post {
            filmTitle = "Detail Film"
            appbar.addOnOffsetChangedListener(OnOffsetChangedListener { _, offset ->
                val isOffset = offset < (posterHeight - 300) / -1
                collapseToolbar.title = if (isOffset) filmTitle else ""
                val typedArray = theme.obtainStyledAttributes(R.style.AppTheme, intArrayOf(R.attr.homeAsUpIndicator))
                val attributeResourceId = typedArray.getResourceId(0, 0)
                val upArrow = getDrawable(baseContext, attributeResourceId)
                typedArray.recycle()
                val color = parseColor(if (isOffset) "#000000" else "#FFFFFF")
                dwFavorite?.setTint(color)
                dwFavorited?.setTint(color)
                if (SDK_INT >= Q) upArrow?.colorFilter = BlendModeColorFilter(color, BlendMode_SRC_ATOP)
                else {
                    @Suppress("DEPRECATION")
                    upArrow?.setColorFilter(color, SRC_ATOP)
                }
                supportActionBar?.setHomeAsUpIndicator(upArrow)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            })
        }
    }

    private fun initData() {
        category = intent.getIntExtra("category", 0)
        val id = intent.getIntExtra("id", 0)
        val table = if (category == 0) "movie" else "tv"
        val gson = Gson()
        uri = Builder().scheme(scheme).authority(author).appendPath(segment).build()
        uri = parse("$uri/$table/$id")
        Thread {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToNext()
                val hashMap = HashMap<String, Any>()
                for (column in 0 until cursor.columnCount) {
                    val columnName = cursor.getColumnName(column)
                    hashMap[columnName] = when (columnName) {
                        "key", "category", "id", "runtime" -> cursor.getInt(column)
                        "budget", "revenue" -> cursor.getLong(column)
                        "voteAverage" -> cursor.getDouble(column)
                        "duration" -> listOf<Int>()
                        "genres" -> {
                            val genres = cursor.getString(column)
                            gson.fromJson(genres, object : TypeToken<List<Genre>>() {}.type) as List<Genre>
                        }
                        else -> cursor.getString(column)
                    }
                }
                val data = gson.fromJson(gson.toJson(hashMap), FilmDetailModel::class.java)
                cursor.close()
                onGotData(data)
            }
        }.start()
    }

    private fun onGotData(data: FilmDetailModel) = runOnUiThread {
        progressBar.visibility = GONE
        posterLayout.visibility = VISIBLE
        txTitleOverview.visibility = VISIBLE
        genre.visibility = VISIBLE
        budget.visibility = VISIBLE
        revenue.visibility = VISIBLE
        val url = "$tmdbImgUrl${data.poster}"
        Glide.with(this).load(url).into(ivPoster)
            .getSize { _, height ->
                posterHeight = height
                ivPoster.layoutParams.height = posterHeight
                ivPoster.requestLayout()
                val behavior = (appbar.layoutParams as LayoutParams).behavior as Behavior
                ofInt().apply {
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animation ->
                        behavior.topAndBottomOffset = animation.animatedValue as Int
                        appbar.requestLayout()
                    }
                    setIntValues(0, -200)
                    duration = 600
                    start()
                }
            }
        data.category = category
        filmTitle = data.title
        txTitle.text = filmTitle
        txGenre.text = data.getGenre()
        txRelease.text = data.getRelease()
        txDuration.text = data.getDuration("Hours", "Minutes")
        if (category == 0) {
            txBudget.text = data.getBudget()
            txRevenue.text = data.getRevenue()
        }
        txOverview.text = data.overview
        ratingBar.rating = (data.voteAverage * 5 / 10).toFloat()
        val rating = "${data.voteAverage}/10 User Score"
        txRating.text = rating
    }

    private fun deleteFavorite() {
        val cursor = contentResolver.delete(uri, null, null)
        println(cursor)
        setResult(RESULT_OK, intent)
    }

    private fun onClickFavorite() {
        isFavorited = !isFavorited
        favoriteMenu?.icon = if (isFavorited) dwFavorited else dwFavorite
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) menuInflater.inflate(R.menu.menu_detail, menu)
        dwFavorited = menu?.get(0)?.icon
        dwFavorite = getDrawable(R.drawable.ic_favorite)
        favoriteMenu = menu?.getItem(0)
        initData()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        else if (item.itemId == R.id.favorite) onClickFavorite()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        if (!isFavorited) deleteFavorite()
        super.finish()
    }
}
