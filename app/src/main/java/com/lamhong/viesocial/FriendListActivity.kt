package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.FriendAdapter
import kotlinx.android.synthetic.main.activity_friend_list.*

class FriendListActivity : AppCompatActivity() {
    val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser
    private var friendAdapter : FriendAdapter ?=null
    private var lstFriendList : List<String> ?=null
    private var lstStatus : List<String> = ArrayList()
    private var idUser : String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        idUser = intent.getStringExtra("userID").toString()
        getListFriend()
        getStatusFriendList()
        getUserName1()
        var recyclerView = recycleView_friendList
        recyclerView.layoutManager= LinearLayoutManager(this)

        friendAdapter= this?.let { FriendAdapter(it, lstFriendList as ArrayList, lstStatus as ArrayList,idUser  ) }
        recyclerView.adapter= friendAdapter

        btn_return_fromFriendList.setOnClickListener{
            finish()
        }


    }
    fun getUserName1(){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(idUser)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userName.text=snapshot.child("fullname").value.toString()
                }
            }
        })
    }

    fun getStatusFriendList() {
        val ref= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(firebaseUser.uid!!).child("friendList")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstStatus as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstStatus as ArrayList).add(ss.value.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun getListFriend() {
        lstFriendList= ArrayList()
        val friendRef= FirebaseDatabase.getInstance().reference
            .child("Friends").child(idUser).child("friendList")
        friendRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstFriendList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstFriendList as ArrayList).add(ss.key.toString())
                    }
                    friendAdapter!!.notifyDataSetChanged()
                }
            }
        })

    }
}