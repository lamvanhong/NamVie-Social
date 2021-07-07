package com.lamhong.viesocial

import android.content.Context
import android.os.Bundle

import android.os.Handler
import android.os.Looper

import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.lamhong.viesocial.Fragment.SettingFragment
import com.lamhong.viesocial.Fragment.*
import com.lamhong.viesocial.Network.MyFirebaseMessagingService
import com.lamhong.viesocial.Utilities.Constants

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
               // startActivity(Intent(this, Post_Activity::class.java))
              //  moveFragment(ShortVideoFragment())
                moveFragment(VideoFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveFragment(NotifyFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveFragment(SettingFragment())
                return@OnNavigationItemSelectedListener true
            }

        }




        false
    }

    var meUid : String? = FirebaseAuth.getInstance().uid
    private fun sendFCMTokenToDatabase(token : String){
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .child(meUid.toString())
            .child(Constants.KEY_FCM_TOKEN)
            .setValue(token)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Firebase.messaging.isAutoInitEnabled = true

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveFragment(zHome())

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                val fbToken = it.result.toString()
                // DO your thing with your firebase token
                sendFCMTokenToDatabase(fbToken)
            }
        }
        MyFirebaseMessagingService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            moveFragment(zHome())
        }, 3500)
        Handler(Looper.getMainLooper()).postDelayed({
            moveFragment(zHome())
        }, 2000)
        //moveFragment(zHome())

    }


    private fun moveFragment(fragment :Fragment){
        val fragmentselect = supportFragmentManager.beginTransaction()
        fragmentselect.replace(R.id.frameLayout, fragment)
        fragmentselect.commit()
    }
    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Xác nhận")
        alert.setIcon(R.drawable.ic_exit)
        alert.setMessage("Bạn muốn thoát khỏi ứng dụng ?")
        alert.setCancelable(false)
        alert.setNegativeButton("Thoát") { dialog, which -> finish() }
        alert.setPositiveButton("Không") { dialog, which -> }
        val alertDialog = alert.create()
        alertDialog.show()
    }

}