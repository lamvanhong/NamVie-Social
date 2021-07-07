package com.lamhong.viesocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        btn_return_fromsetting.setOnClickListener{
            this.finish()
        }
        btnName.setOnClickListener {
            startActivity(Intent(this@SettingActivity, DoiTenActivity::class.java))
        }
        btnrules.setOnClickListener {
            startActivity(Intent(this@SettingActivity, RulesActivity::class.java))
        }
        btnHelp.setOnClickListener {
            startActivity(Intent(this@SettingActivity, HelpActivity::class.java))
        }
        btnTTTG.setOnClickListener {
            startActivity(Intent(this@SettingActivity, TTTGActivity::class.java))
        }
        btnPassword.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ThaydoimatkhauActivity::class.java))
        }
    }
}