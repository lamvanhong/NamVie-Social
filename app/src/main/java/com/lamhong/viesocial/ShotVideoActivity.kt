package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lamhong.viesocial.Adapter.ShotVideoAdapter
import com.lamhong.viesocial.Models.ShortVideo
import kotlinx.android.synthetic.main.activity_shot_video.*

class ShotVideoActivity : AppCompatActivity() {

    private var adapter: ShotVideoAdapter?=null
    private var listShot : ArrayList<ShortVideo> = ArrayList()
    private var followingList : ArrayList<String> = ArrayList()

    private var type : String="" // video hot or video follow || maybe a id
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shot_video)

        getFollowinglist()
        type= intent.getStringExtra("type").toString()
        getResourceList(type) // it can be a id
        adapter = ShotVideoAdapter(this, listShot as ArrayList<ShortVideo?>)
        vpaper.adapter=adapter
    }

    fun getResourceList( type : String){
        val ref = FirebaseDatabase.getInstance().reference.child("ShotVideos")
        when (type){
            "videohot"->{
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        listShot.clear()
                        if(snapshot.exists()){
                            for (s in snapshot.children){
                                val shot= s.getValue(ShortVideo::class.java)
                                shot!!.setVideo(s.child("video").value.toString())
                                listShot.add(shot!!)
                            }
                            adapter!!.notifyItemChanged(0)
                        }
                        listShot.reverse()
                    }
                })
            }
            "videofollow"->{
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        listShot.clear()
                        if(snapshot.exists()){
                            for (s in snapshot.children){
                                val shot= s.getValue(ShortVideo::class.java)
                                shot!!.setVideo(s.child("video").value.toString())

                                for (user in followingList as ArrayList){
                                    if(user== shot.getPublisher()){
                                        listShot.add(shot!!)
                                    }
                                }
                            }
                            listShot.reverse()
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                })

            }
            else ->{
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        listShot.clear()
                        if(snapshot.exists()){
                                val shot= snapshot.child(type).getValue(ShortVideo::class.java)
                                shot!!.setVideo(snapshot.child(type).child("video").value.toString())
                                listShot.add(shot!!)
                            adapter!!.notifyDataSetChanged()
                        }
                    }
                })

            }
        }


    }
    private fun getFollowinglist() {
        followingList=ArrayList()

        val followRef = FirebaseDatabase.getInstance().reference
                .child("Friends").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("followingList")
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (s in snapshot.children){
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onStart() {
        super.onStart()
        //adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
       // adapter.stopListening()
    }
}