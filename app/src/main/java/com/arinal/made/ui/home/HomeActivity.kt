package com.arinal.made.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.R
import com.arinal.made.data.network.ApiClient
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.ui.detail.DetailActivity
import com.arinal.made.ui.home.adapter.HomePagerAdapter
import com.arinal.made.ui.settings.SettingLanguageActivity
import com.arinal.made.utils.scheduler.SchedulerProviderImpl
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity

class HomeActivity : BaseActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        hideBackButton()
        collapseToolbar.setExpandedTitleColor(Color.parseColor("#00FFFFFF"))
        collapseToolbar.setCollapsedTitleTextColor(Color.parseColor("#000000"))
        viewPager.adapter = HomePagerAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun initData() {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(ApiClient.getTmdb(), SchedulerProviderImpl(), compositeDisposable) as T
            }
        }
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        viewModel.setOnclick { category, id -> startActivity<DetailActivity>("id" to id, "category" to category) }
        viewModel.setLanguage { getLang() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) startActivity<SettingLanguageActivity>()
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        compositeDisposable.dispose()
        super.finish()
    }
}