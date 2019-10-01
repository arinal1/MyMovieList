package com.arinal.made.favorites.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPagerAdapter(
    private val fragments: Array<Fragment>,
    private val titles: Array<String>,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = fragments[position]
    override fun getPageTitle(position: Int): CharSequence? = titles[position]
    override fun getCount(): Int = 2

}