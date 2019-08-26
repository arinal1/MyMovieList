package com.arinal.made.ui.detail

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.model.ExtraDetailModel
import com.arinal.made.data.model.FilmDetailModel
import com.arinal.made.data.model.FilmModel
import com.arinal.made.data.network.ApiClient
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.utils.Constants
import com.arinal.made.utils.extension.gone
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.toast
import java.util.concurrent.atomic.AtomicBoolean

class DetailActivity : BaseActivity() {
    private lateinit var dataExtra: ExtraDetailModel
    private lateinit var dataFilm: FilmModel
    private lateinit var dataDetail: FilmDetailModel
    private lateinit var initFavoriteStatus: AtomicBoolean
    private lateinit var viewModel: DetailViewModel
    private var compositeDisposable = CompositeDisposable()
    private var dwFavorite: Drawable? = null
    private var dwFavorited: Drawable? = null
    private var favoriteMenu: MenuItem? = null
    private var filmTitle = ""
    private var posterHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        setAppbarListener()
    }

    private fun setAppbarListener() {
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
            appbar.post {
                val isOffset = offset < (posterHeight - 300) / -1
                collapseToolbar.title = if (isOffset) filmTitle else ""
                val typedArray = theme.obtainStyledAttributes(
                    R.style.AppTheme,
                    intArrayOf(R.attr.homeAsUpIndicator)
                )
                val attributeResourceId = typedArray.getResourceId(0, 0)
                val upArrow = ContextCompat.getDrawable(baseContext, attributeResourceId)
                typedArray.recycle()
                val color = Color.parseColor(if (isOffset) "#000000" else "#FFFFFF")
                dwFavorite?.setTint(color)
                dwFavorited?.setTint(color)
                if (SDK_INT >= Q) upArrow?.colorFilter =
                    BlendModeColorFilter(color, BlendMode.SRC_ATOP)
                else {
                    @Suppress("DEPRECATION")
                    upArrow?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                supportActionBar?.setHomeAsUpIndicator(upArrow)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        })
    }

    private fun initData() {
        dataExtra = intent.getParcelableExtra("data")!!
        dataFilm = dataExtra.filmModel
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(
                    dataFilm.category,
                    ApiClient.getTmdb(),
                    TmdbDatabase.getInstance(applicationContext).tmdbDao(),
                    compositeDisposable,
                    SchedulerProviderImpl()
                ) { onFailed(it) } as T
            }
        }
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel::class.java)
        viewModel.getDetailData().observe(this, onGotData())
        viewModel.getIsFavorited().observe(this, onFavoriteSet())
        viewModel.getData(dataFilm.id, getLang())
    }

    private fun onGotData(): Observer<FilmDetailModel> = Observer {
        dataDetail = it
        progressBar.gone()
        Glide.with(this).load(Constants.tmdbImgUrl + it.poster).into(ivPoster)
            .getSize { _, height ->
                posterHeight = height - 200
                ivPoster.layoutParams.height = posterHeight
                ivPoster.requestLayout()
            }
        it.category = dataFilm.category
        filmTitle = it.title
        txTitle.text = filmTitle
        txGenre.text = it.getGenre()
        txRelease.text = it.getRelease()
        txDuration.text = it.getDuration(getString(R.string.hours), getString(R.string.minutes))
        if (dataFilm.category == 0) {
            txBudget.text = it.getBudget()
            txRevenue.text = it.getRevenue()
        } else {
            txBudget.gone()
            txRevenue.gone()
            budget.gone()
            revenue.gone()
        }
        txOverview.text = it.overview
        ratingBar.rating = (it.voteAverage * 5 / 10).toFloat()
        val rating = "${it.voteAverage}/10 ${getString(R.string.user_score)}"
        txRating.text = rating
    }

    private fun onFailed(throwable: Throwable) {
        progressBar.gone()
        toast(throwable.localizedMessage ?: "Error")
    }

    private fun onClickFavorite() {
        if (::dataDetail.isInitialized) viewModel.onClickFavorite(dataFilm, dataDetail)
    }

    private fun onFavoriteSet(): Observer<Boolean> = Observer {
        favoriteMenu?.icon = if (it) dwFavorited else dwFavorite
        if (!::initFavoriteStatus.isInitialized) initFavoriteStatus = AtomicBoolean(it)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) menuInflater.inflate(R.menu.menu_detail, menu)
        dwFavorite = menu?.get(0)?.icon
        dwFavorited = getDrawable(R.drawable.ic_favorited)
        favoriteMenu = menu?.getItem(0)
        viewModel.getFavorite(dataFilm.id)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorite) onClickFavorite()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        compositeDisposable.dispose()
        val category = dataFilm.category
        val index = dataExtra.index
        val currentFavoriteStatus = viewModel.getIsFavorited().value == true
        val intent = Intent()
            .putExtra("category", category)
            .putExtra("index", index)
        if (initFavoriteStatus.get() != currentFavoriteStatus) setResult(RESULT_OK, intent.putExtra("added", currentFavoriteStatus))
        super.finish()
    }
}
