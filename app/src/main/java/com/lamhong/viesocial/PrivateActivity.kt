package com.lamhong.viesocial

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_private.*

class PrivateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)
        card11.setOnClickListener {
            tv11.setTextColor(Color.parseColor("#00FF00"))
            tv12.setTextColor(Color.parseColor("#808080"))
            tv13.setTextColor(Color.parseColor("#808080"))
        }
        card12.setOnClickListener {
            tv12.setTextColor(Color.parseColor("#00FF00"))
            tv11.setTextColor(Color.parseColor("#808080"))
            tv13.setTextColor(Color.parseColor("#808080"))
        }
        card13.setOnClickListener {
            tv13.setTextColor(Color.parseColor("#00FF00"))
            tv12.setTextColor(Color.parseColor("#808080"))
            tv11.setTextColor(Color.parseColor("#808080"))
        }

        card21.setOnClickListener {
            tv21.setTextColor(Color.parseColor("#00FF00"))
            tv22.setTextColor(Color.parseColor("#808080"))
            tv23.setTextColor(Color.parseColor("#808080"))
        }
        card22.setOnClickListener {
            tv22.setTextColor(Color.parseColor("#00FF00"))
            tv21.setTextColor(Color.parseColor("#808080"))
            tv23.setTextColor(Color.parseColor("#808080"))
        }
        card23.setOnClickListener {
            tv23.setTextColor(Color.parseColor("#00FF00"))
            tv22.setTextColor(Color.parseColor("#808080"))
            tv21.setTextColor(Color.parseColor("#808080"))
        }

        card31.setOnClickListener {
            tv31.setTextColor(Color.parseColor("#00FF00"))
            tv32.setTextColor(Color.parseColor("#808080"))
            tv33.setTextColor(Color.parseColor("#808080"))
        }
        card32.setOnClickListener {
            tv32.setTextColor(Color.parseColor("#00FF00"))
            tv31.setTextColor(Color.parseColor("#808080"))
            tv33.setTextColor(Color.parseColor("#808080"))
        }
        card33.setOnClickListener {
            tv33.setTextColor(Color.parseColor("#00FF00"))
            tv32.setTextColor(Color.parseColor("#808080"))
            tv31.setTextColor(Color.parseColor("#808080"))
        }
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
    }
}