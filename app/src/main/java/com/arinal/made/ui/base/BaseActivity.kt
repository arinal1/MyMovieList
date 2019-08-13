package com.arinal.made.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html.fromHtml
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        preference = getSharedPreferences("locale", Context.MODE_PRIVATE)
    }

    internal fun hideBackButton() {
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    internal fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        val editor = preference.edit()
        editor?.putString("lang", locale.language)?.apply()
    }

    internal fun getLang():String = preference.getString("lang", "id")?:"id"

    override fun onResume() {
        setLocale(Locale(getLang()))
        super.onResume()
    }

    internal fun showSnackbar(message: String){
        @Suppress("DEPRECATION") val msg = fromHtml("<font color=\"#ffffff\">$message</font>")
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }
}