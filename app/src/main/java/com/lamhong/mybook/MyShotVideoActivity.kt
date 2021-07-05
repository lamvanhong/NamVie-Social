package com.lamhong.mybook

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lamhong.mybook.Adapter.ImageProfileAdapter
import com.lamhong.mybook.Adapter.ShotVideoListAdapter
import com.lamhong.mybook.Models.Post
import com.lamhong.mybook.Models.ShortVideo
import com.lamhong.mybook.Models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_shot_video.*

class MyShotVideoActivity : AppCompatActivity() {

    private var shotList : List<ShortVideo> = ArrayList()
    private var shotvideoListAdapter : ShotVideoListAdapter?=null


    private var id : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_shot_video)
        id = intent.getStringExtra("id").toString()
        btn_return_shot.setOnClickListener{
            this.finish()
        }

        var recycleview :RecyclerView
        getShot()

        recycleview=    findViewById(R.id.recycleview_listShotVideo)
        var gridLayoutManager= GridLayoutManager(this, 3)
        recycleview.layoutManager= gridLayoutManager
        shotvideoListAdapter= this?.let { ShotVideoListAdapter(it, shotList as ArrayList) }
        recycleview.adapter=shotvideoListAdapter
        showInfor()
        setNumberProfile()
        followStatus()
        if(id==FirebaseAuth.getInstance().currentUser.uid){
            container_theodoi.visibility= View.GONE
        }

        // func button
        btn_trangcannhan.setOnClickListener{
            val userIntent = Intent(this, ProfileActivity::class.java)
            userIntent.putExtra("profileID",id)
            startActivity(userIntent)
        }
        btn_theodoi.setOnClickListener{
            if(btn_theodoi.tag=="follow"){
                FirebaseDatabase.getInstance().reference.child("Friends")
                        .child(FirebaseAuth.getInstance().currentUser.uid).child("followingList")
                        .child(id).setValue("true")
            }
            else{
                FirebaseDatabase.getInstance().reference.child("Friends")
                        .child(FirebaseAuth.getInstance().currentUser.uid).child("followingList")
                        .child(id).removeValue()
            }
        }

    }

    private fun followStatus() {
        val ref = FirebaseDatabase.getInstance().reference.child("Friends").child(
                FirebaseAuth.getInstance().currentUser.uid).child("followingList").child(id)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    btn_theodoi.tag="followed"
                    btn_theodoi.setBackgroundResource(R.drawable.custom_button_whi)
                    btn_theodoi.text="Bỏ theo dõi"
                    btn_theodoi.setTextColor(Color.parseColor("#03A9F4"))
                }
                else{
                    btn_theodoi.tag="follow"
                    btn_theodoi.setBackgroundResource(R.drawable.custom_btn_whi_blue)
                    btn_theodoi.text="Theo dõi"
                    btn_theodoi.setTextColor(Color.parseColor("#FFFFFF"))

                }
            }
        })
    }

    private fun setNumberProfile() {
        val ref= FirebaseDatabase.getInstance().reference
                .child("Friends").child(id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("followerList").exists()){
                        numFollower_shotprofile.text=snapshot.child("followerList").childrenCount.toString()
                    }
                    else{
                        numFollower_shotprofile.text="0"
                    }
                    if(snapshot.child("followingList").exists()){
                        numFollowinng_shotprofile.text=snapshot.child("followingList").childrenCount.toString()
                    }
                    else{
                        numFollowinng_shotprofile.text="0"
                    }
                }
                else{
                    numFollowinng_shotprofile.text="0"
                    numFollower_shotprofile.text="0"
                }
            }
        })
        val postRef= FirebaseDatabase.getInstance().reference
                .child("ShotVideos")
        postRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var ss: Int=0
                if(snapshot.exists()){
                    for(s in snapshot.children)
                    {
                        if(s.child("publisher").value==id){
                            ss+=1
                        }
                    }
                }
                numvideo_shotprofile.text=ss.toString()
            }
        })
    }
    private fun showInfor() {
        val userRef= FirebaseDatabase.getInstance().reference
                .child("UserInformation").child(id)
        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val curUser = snapshot.getValue(User::class.java)
                    curUser!!.setName(snapshot.child("fullname").value.toString())
                    curUser!!.setAvatar(snapshot.child("avatar").value.toString())
                    username_shotlist.text=curUser!!.getName()
                    Picasso.get().load(curUser.getAvatar()).into(avatar_shotprofile1)


                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun getShot(){
        val postRef= FirebaseDatabase.getInstance().reference.child("ShotVideos")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (shotList as ArrayList<ShortVideo>).clear()
                    for (s in snapshot.children){
                        val shot= s.getValue(ShortVideo::class.java)
                        shot!!.setVideo(s.child("video").value.toString())
                        shot!!.setView((s.child("views").value.toString().toInt()))
                        shot!!.setThumb(s.child("thumb").value.toString())
                        if(shot.getPublisher()==id){
                            (shotList as ArrayList).add(shot!!)
                        }
                        (shotList as ArrayList).reverse()



                    }
                    shotvideoListAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}