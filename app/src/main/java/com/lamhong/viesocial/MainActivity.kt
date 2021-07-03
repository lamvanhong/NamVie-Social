package com.lamhong.viesocial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lamhong.viesocial.Fragment.*

class MainActivity : AppCompatActivity() {
//    private lateinit var textview: TextView
 //   internal var selectedFragment: Fragment ?=null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveFragment(zHome())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {

               moveFragment(MessageFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                //item.isChecked=false
                startActivity(Intent(this, Post_Activity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveFragment(NotifyFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }

        }


        false
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveFragment(zHome())
    }

    private fun moveFragment(fragment :Fragment){
        val fragmentselect = supportFragmentManager.beginTransaction()
        fragmentselect.replace(R.id.frameLayout, fragment)
        fragmentselect.commit()
    }
}