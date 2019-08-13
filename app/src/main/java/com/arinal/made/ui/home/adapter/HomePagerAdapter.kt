package com.arinal.made.ui.home.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arinal.made.R
import com.arinal.made.ui.home.HomeFragment

class HomePagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance("movie")
            else -> HomeFragment.newInstance("tv")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = context.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = 2

    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.tab_home_1,
            R.string.tab_home_2
        )
    }
}