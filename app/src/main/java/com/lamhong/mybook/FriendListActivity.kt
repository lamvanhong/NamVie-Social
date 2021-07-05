package com.lamhong.mybook

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Adapter.FriendAdapter
import com.lamhong.mybook.Models.User
import kotlinx.android.synthetic.main.activity_friend_list.*

class FriendListActivity : AppCompatActivity() {
    val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser
    private var friendAdapter : FriendAdapter ?=null
    private var lstFriendList : List<String> ?=null
    private var lstStatus : List<String> = ArrayList()
    private var idUser : String =""

    // separate list
    private var lst_trueFriend : List<String> = ArrayList()
    private var lst_waittingFriend: List<String> = ArrayList()
    private var lst_confirmFriend: List<String> = ArrayList()
    private var lst_trueFriends : List<String> = ArrayList()
    private var lst_waittingFriends: List<String> = ArrayList()
    private var lst_confirmFriends: List<String> = ArrayList()
    private var lst_trueFriendAdapter : FriendAdapter  ?=null
    private var lst_waittingFriendAdapter:FriendAdapter  ?=null
    private var lst_confirmFriendAdapter: FriendAdapter  ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        idUser = intent.getStringExtra("userID").toString()
        getListFriend()
        getStatusFriendList()
        getUserName1()
        var recyclerView = recycleView_friendList
        //var recyclerView1 = recycleView_friendList1
        var gridLayoutManager = GridLayoutManager(this, 3)
        var linearLayoutManager= LinearLayoutManager(this)
        recyclerView.layoutManager= gridLayoutManager

        friendAdapter= this?.let { FriendAdapter(it, lstFriendList as ArrayList, lstStatus as ArrayList,idUser  ) }
        lst_trueFriendAdapter= this?.let { FriendAdapter(it, lst_trueFriend as ArrayList, lst_trueFriends as ArrayList,idUser  ) }
        lst_waittingFriendAdapter= this?.let { FriendAdapter(it, lst_waittingFriend as ArrayList, lst_waittingFriends as ArrayList,idUser  ) }
        lst_confirmFriendAdapter= this?.let { FriendAdapter(it, lst_confirmFriend as ArrayList, lst_confirmFriends as ArrayList,idUser  ) }

       // recyclerView.layoutManager=gridLayoutManager
        recyclerView.adapter= lst_trueFriendAdapter

       // recyclerView1.visibility= View.VISIBLE
       // recyclerView1.layoutManager= linearLayoutManager
       // recyclerView1.adapter= lst_waittingFriendAdapter


        btn_return_fromFriendList.setOnClickListener{
            finish()
        }


        //set button selected click
        btn_banbe_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceSelected(btn_banbe_friendlist)
            recyclerView.layoutManager= gridLayoutManager
            recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE

        }
        btn_dagui_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            recyclerView.layoutManager= linearLayoutManager
            recyclerView.adapter= lst_waittingFriendAdapter
           // recyclerView1.visibility= View.GONE
        }
        btn_tatca_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)

            recyclerView.layoutManager=gridLayoutManager
            recyclerView.adapter= lst_trueFriendAdapter

           // recyclerView1.layoutManager= linearLayoutManager
           // recyclerView1.adapter= lst_waittingFriendAdapter
           // recyclerView1.visibility= View.VISIBLE

        }
        btn_dangcho_friendlist.setOnClickListener{
            setBtnAppearanceSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            recyclerView.layoutManager= linearLayoutManager
            recyclerView.adapter= lst_confirmFriendAdapter
           // recyclerView1.visibility= View.GONE

        }

        // friend or this
        if(idUser==firebaseUser.uid!!){

        }
        else{
            btn_dangcho_friendlist.visibility=View.GONE
            btn_dagui_friendlist.visibility=View.GONE
        }


    }
    fun setBtnAppearanceNonSelected(btn : AppCompatButton){
        btn.setTextColor(Color.parseColor("#989898"))
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
    fun setBtnAppearanceSelected(btn : AppCompatButton){

        btn.setTextColor(Color.parseColor("#FFFFFF"))
        btn.setBackgroundColor(Color.parseColor("#00BCD4"))
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
    fun setstatustoFriend(){
        (lstStatus as ArrayList).clear()
        for (i in lstFriendList!!){
            (lstStatus as ArrayList).add("friend")
        }
    }

    fun getStatusFriendList() {
        val ref= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(idUser).child("friendList")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstStatus as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstStatus as ArrayList).add(ss.value.toString())
                        if(ss.value=="pendingconfirm")
                        {
                            (lst_confirmFriend as ArrayList).add(ss.key.toString())
                            (lst_confirmFriends as ArrayList).add(ss.value.toString())
                        }
                        else if(ss.value=="pendinginvite"){
                            (lst_waittingFriend as ArrayList).add(ss.key.toString())
                            (lst_waittingFriends as ArrayList).add(ss.value.toString())
                        }
                        else if(ss.value=="friend"){
                            (lst_trueFriend as ArrayList).add(ss.key.toString())
                            (lst_trueFriends as ArrayList).add(ss.value.toString())
                        }
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