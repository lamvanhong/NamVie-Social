    package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lamhong.viesocial.Adapter.UserAdapter
import com.lamhong.viesocial.Models.User
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

    class UserReacted : AppCompatActivity() {

    var postID : String =""
    var userAdapter : UserAdapter?= null
    var listUser : List<User>?=null
    var listID : List<String> ?=null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_reacted)


        postID= intent.getStringExtra("postID").toString()
        val toolbar : Toolbar= findViewById(R.id.toolbar_user_react)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Người dùng bài tỏ cảm xúc"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            finish()
        }
        var recyclerView : RecyclerView= findViewById(R.id.recycleview_user_react)
        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=linearLayoutManager

        listUser= ArrayList()
        userAdapter= UserAdapter(this, listUser as ArrayList<User>,false, "home")
        recyclerView.adapter=userAdapter

        listID= ArrayList()
        getLike()
        showListUser()

    }


        private fun getLike() {
            val likeRef= FirebaseDatabase.getInstance().reference
                .child("Likes").child(postID)
            likeRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        (listID as ArrayList<String>).clear()
                        for (s in snapshot.children){
                            (listID as ArrayList<String>).add(s.key!!)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        private fun showListUser(){
            val userRef= FirebaseDatabase.getInstance().reference
                .child("UserInformation")
            userRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    (listUser as ArrayList<User>).clear()
                    if (snapshot.exists()){
                        for (s in snapshot.children){
                            val user= s.getValue(User::class.java)
                            for (id in listID!!){
                                if (user!!.getUid().equals(id)){
                                    (listUser as ArrayList<User>).add(user)
                                }
                            }
                        }
                        userAdapter!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }