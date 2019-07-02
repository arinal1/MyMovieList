package com.arinal.made.ui.home

import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.view.Menu
import android.view.MenuItem
import com.arinal.made.R
import com.arinal.made.data.model.FilmModel
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.ui.detail.DetailActivity
import com.arinal.made.ui.settinglang.SettingLanguageActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.startActivity

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        hideBackButton()
        collapseToolbar.setExpandedTitleColor(Color.parseColor("#00FFFFFF"))
        collapseToolbar.setCollapsedTitleTextColor(Color.parseColor("#000000"))
        val mInterface = object: HomeInterface{
            override fun goToDetail(data: FilmModel) = startActivity<DetailActivity>("data" to data)
            override fun writeToParcel(parcel: Parcel?, i: Int) {parcel?.writeInt(i) }
            override fun describeContents(): Int  = 0
        }
        viewPager.adapter = HomePagerAdapter(this, mInterface, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) startActivity<SettingLanguageActivity>()
        return super.onOptionsItemSelected(item)
    }
}