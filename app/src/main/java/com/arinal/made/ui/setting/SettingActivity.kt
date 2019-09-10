package com.arinal.made.ui.setting

import android.app.job.JobScheduler
import android.os.Bundle
import com.arinal.made.R
import com.arinal.made.data.local.PreferenceManager
import com.arinal.made.ui.base.BaseActivity
import com.arinal.made.ui.home.HomeActivity
import com.arinal.made.utils.AlarmUtils
import com.arinal.made.utils.Constants.reminderServiceId
import com.arinal.made.utils.Constants.updateServiceId
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import java.util.*

class SettingActivity : BaseActivity() {

    private lateinit var jobScheduler: JobScheduler
    private lateinit var preference: PreferenceManager
    private var goHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initData()
        initView()
    }

    private fun initData() {
        jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        preference = PreferenceManager(this)
        if (getLang() == "en") cbEnglish.isChecked = true else cbBahasa.isChecked = true
        if (intent.getBooleanExtra("afterSuccess", false)) {
            showSnackbar(getString(R.string.msg_success_set_language))
            goHome = true
        }
        if (preference.reminderSet) switchReminder.isChecked = true
        if (preference.dailyUpdateSet) switchUpdate.isChecked = true
    }

    private fun initView() {
        title = getString(R.string.setting)
        cbBahasa.setOnCheckedChangeListener { _, b -> if (b) localize(Locale("id")) }
        cbEnglish.setOnCheckedChangeListener { _, b -> if (b) localize(Locale("en")) }
        switchReminder.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                preference.reminderSet = true
                AlarmUtils(this)
            } else {
                jobScheduler.cancel(reminderServiceId)
                preference.reminderSet = false
                preference.reminderJobSet = false
            }
        }
        switchUpdate.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                preference.dailyUpdateSet = true
                AlarmUtils(this)
            } else {
                jobScheduler.cancel(updateServiceId)
                preference.dailyUpdateSet = false
                preference.dailyUpdateJobSet = false
            }
        }
    }

    private fun localize(locale: Locale) {
        setLocale(locale)
        goHome = false
        startActivity<SettingActivity>("afterSuccess" to true)
        finish()
    }

    override fun finish() {
        if (goHome) startActivity(intentFor<HomeActivity>().clearTop())
        super.finish()
    }
}
