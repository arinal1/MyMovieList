package com.arinal.made.favorites.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arinal.made.favorites.R
import com.arinal.made.favorites.ui.main.adapter.MainPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        val tabTitles = arrayOf(getString(R.string.title_tab_home_1), getString(R.string.title_tab_home_2))
        val fragments = mutableListOf<Fragment>().apply { for (i in 0..1) add(MainFragment.newInstance(i)) }
        viewPager.adapter = MainPagerAdapter(fragments.toTypedArray(), tabTitles, supportFragmentManager)
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
                return MainViewModel(contentResolver) as T
            }
        }
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
    }
}
