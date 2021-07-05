package com.lamhong.viesocial

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.FriendAdapter
import kotlinx.android.synthetic.main.activity_following_list.*
import kotlinx.android.synthetic.main.activity_following_list.btn_banbe_friendlist
import kotlinx.android.synthetic.main.activity_following_list.btn_dagui_friendlist
import kotlinx.android.synthetic.main.activity_following_list.btn_dangcho_friendlist
import kotlinx.android.synthetic.main.activity_following_list.btn_tatca_friendlist
import kotlinx.android.synthetic.main.activity_following_list.recycleView_friendList

class FollowingListActivity : AppCompatActivity() {
    private var userID: String = ""
    private var type: String = ""
    private var lstFollowList : ArrayList<String> = ArrayList()
    private var lstFollowmeList : ArrayList<String> = ArrayList()

    private var lstFollowLists : ArrayList<String> = ArrayList()
    private var lstFollowmeLists : ArrayList<String> = ArrayList()

    private var lstFollowListAdapter : FriendAdapter?=null
    private var lstFollowmeListAdapter : FriendAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)
        btn_return_fromfollowlist.setOnClickListener{
            this.finish()
        }
        //innit something
        userID= intent.getStringExtra("userID").toString()
        type= intent.getStringExtra("type").toString()


        // list
        getList()
        var recyclerView = recycleView_friendList
        var gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager= gridLayoutManager

        lstFollowmeListAdapter= this?.let { FriendAdapter(it, lstFollowmeList as ArrayList, lstFollowmeLists as ArrayList,userID  ) }
        lstFollowListAdapter= this?.let { FriendAdapter(it, lstFollowList as ArrayList, lstFollowLists as ArrayList,userID  ) }


        // set if user choosen
        if(type=="follower"){
            setBtnAppearanceSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_confirmFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowmeListAdapter
        }
        else {
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= gridLayoutManager
            //recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowListAdapter
        }


        //set button selected click
        btn_banbe_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= gridLayoutManager
            //recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowListAdapter

        }
        btn_dagui_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_waittingFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowmeListAdapter

        }

        btn_dangcho_friendlist.setOnClickListener{
            setBtnAppearanceSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_confirmFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowmeListAdapter
        }

    }
    fun getList(){
        // dealine nên tạm tên nhá =))
        lstFollowList= ArrayList()
        val friendRef= FirebaseDatabase.getInstance().reference
            .child("Friends").child(userID).child("followingList")
        friendRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstFollowList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstFollowList as ArrayList).add(ss.key.toString())
                        (lstFollowLists as ArrayList).add("friend")
                    }
                    lstFollowListAdapter!!.notifyDataSetChanged()
                }
            }
        })

        lstFollowmeList= ArrayList()
        val friendRef1= FirebaseDatabase.getInstance().reference
            .child("Friends").child(userID).child("followerList")
        friendRef1.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstFollowmeList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstFollowmeList as ArrayList).add(ss.key.toString())
                        (lstFollowmeLists as ArrayList).add("friend")
                    }
                    lstFollowmeListAdapter!!.notifyDataSetChanged()
                }
            }
        })

    }
    fun setBtnAppearanceNonSelected(btn : AppCompatButton){
        btn.setTextColor(Color.parseColor("#989898"))
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
    fun setBtnAppearanceSelected(btn : AppCompatButton){

        btn.setTextColor(Color.parseColor("#FFFFFF"))
        btn.setBackgroundColor(Color.parseColor("#00BCD4"))
    }
}