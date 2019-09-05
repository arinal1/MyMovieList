package com.arinal.made.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.local.TmdbDatabase
import com.arinal.made.data.network.ApiClient
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.ui.detail.DetailActivity
import com.arinal.made.ui.home.adapter.HomePagerAdapter
import com.arinal.made.ui.settings.SettingLanguageActivity
import com.arinal.made.utils.AlarmUtils
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import com.google.android.material.tabs.TabLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.util.*

class HomeActivity : BaseActivity() {

    private val compositeDisposable = CompositeDisposable()
    private var favoriteIcon: Drawable? = null
    private var favoritedIcon: Drawable? = null
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        AlarmUtils(this)
    }

    private fun initView() {
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        hideBackButton()
        collapseToolbar.setExpandedTitleColor(Color.parseColor("#00FFFFFF"))
        collapseToolbar.setCollapsedTitleTextColor(Color.parseColor("#000000"))
        setLocale(Locale(getLang()))
        val tabTitles = arrayOf(getString(R.string.title_tab_home_1), getString(R.string.title_tab_home_2))
        val fragments = mutableListOf<Fragment>().apply { for (i in 0..1) add(HomeFragment.newInstance(i)) }
        viewPager.adapter = HomePagerAdapter(fragments.toTypedArray(), tabTitles, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) = viewModel.scrollToTop(tab?.position ?: 0)
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initData() {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(
                    ApiClient.getTmdb(), SchedulerProviderImpl(), compositeDisposable,
                    TmdbDatabase.getInstance(applicationContext).tmdbDao()
                ) as T
            }
        }
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        viewModel.setOnclick { data -> startActivityForResult<DetailActivity>(getReqCode(data.isFromFavorite), "data" to data) }
        viewModel.setLanguage { getLang() }
    }

    private fun getReqCode(fromFavorite: Boolean) = if (fromFavorite) 2 else 1

    private fun setIconColor() {
        favoriteIcon?.setTint(Color.parseColor("#000000"))
        favoritedIcon?.setTint(Color.parseColor("#000000"))
    }

    private fun onClickFavorite(item: MenuItem) {
        viewModel.showFavorites(item.icon == favoriteIcon)
        item.icon = if (item.icon == favoriteIcon) favoritedIcon else favoriteIcon
        setIconColor()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) menuInflater.inflate(R.menu.menu_home, menu)
        favoritedIcon = getDrawable(R.drawable.ic_favorited)
        favoriteIcon = menu?.get(0)?.icon
        setIconColor()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) startActivity<SettingLanguageActivity>()
        if (item.itemId == R.id.favorite) onClickFavorite(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && data != null) {
            val added = data.getBooleanExtra("added", false)
            val category = data.getIntExtra("category", 0)
            val index = data.getIntExtra("index", 0)
            val fromFavorite = requestCode == 2
            if (added) viewModel.addFavoriteFromFilm(category, index)
            else viewModel.deleteFavorite(category, index, fromFavorite)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        compositeDisposable.dispose()
        super.finish()
    }
}