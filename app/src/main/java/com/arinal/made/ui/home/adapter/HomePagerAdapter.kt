package com.arinal.made.ui.home.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arinal.made.R
import com.arinal.made.ui.home.HomeFragment

class HomePagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val tabTitles = arrayOf(R.string.title_tab_home_1, R.string.title_tab_home_2)
    override fun getItem(position: Int): Fragment = HomeFragment.newInstance(position)
    override fun getPageTitle(position: Int): CharSequence? = context.resources.getString(tabTitles[position])
    override fun getCount(): Int = 2
}