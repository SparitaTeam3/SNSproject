package com.android.hikers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class UserInfoActivity : AppCompatActivity() {
    private val
    private lateinit var idValue: String
    private lateinit var pwValue: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        initValues()
    }

    private fun initValues() {
        idValue = intent.getStringExtra(EXTRA_ID) ?: ""
        pwValue = intent.getStringExtra(EXTRA_PW) ?: ""

    }
}