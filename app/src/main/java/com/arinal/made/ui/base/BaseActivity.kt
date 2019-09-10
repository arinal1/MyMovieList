package com.arinal.made.ui.base

import android.os.Bundle
import android.text.Html.fromHtml
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.arinal.made.data.local.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var preference: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        preference = PreferenceManager(this)
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
        preference.language = locale.language
    }

    internal fun getLang():String = preference.language

    override fun onResume() {
        setLocale(Locale(getLang()))
        super.onResume()
    }

    internal fun showSnackbar(message: String){
        @Suppress("DEPRECATION") val msg = fromHtml("<font color=\"#ffffff\">$message</font>")
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }
}