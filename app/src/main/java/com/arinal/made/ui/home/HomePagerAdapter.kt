package com.arinal.made.ui.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arinal.made.R
import com.arinal.made.data.local.FilmRepo

class HomePagerAdapter(private val context: Context, private val mInterface: HomeInterface, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val dataRepo = FilmRepo(context.resources)
        return when (position) {
            0 -> HomeFragment.newInstance(dataRepo.getMovie(), mInterface)
            else -> HomeFragment.newInstance(dataRepo.getTvShow(), mInterface)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }

    companion object {
        private val TAB_TITLES = arrayOf(
            R.string.tab_home_1,
            R.string.tab_home_2
        )
    }
}