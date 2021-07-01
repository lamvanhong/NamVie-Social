package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_share_post.*

class SharePostActivity : AppCompatActivity() {

    private var postID: String=""
    private var firebaseUser : FirebaseUser?=null
    private var followingList : ArrayList<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)
        firebaseUser=FirebaseAuth.getInstance().currentUser
        postID= intent.getStringExtra("postID").toString()
        btn_dangShare.setOnClickListener{
            sharePost()
        }
        btn_close.setOnClickListener{
            finish()
        }


    }
    private fun getFollowinglist(){
        val ref = FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .child("friendList")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    followingList!!.clear()
                    for (s in snapshot.children){
                        followingList!!.add(s.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun sharePost(){
        val shareRef= FirebaseDatabase.getInstance().reference
            .child("Share Posts").child(firebaseUser!!.uid)
        val shareMap = HashMap<String, Any>()

        val idref= shareRef.push().key.toString()
        shareMap["shareID"]=idref
        shareMap["postID"]=postID
        shareMap["content"]=edit_content.text.toString()
        shareMap["type"]="baiviet"
        shareMap["typeshare"]="friend"
        shareMap["publisher"]=firebaseUser!!.uid


        shareRef.child(idref).setValue(shareMap)

        for(user in followingList!!){
            val timelineRef= FirebaseDatabase.getInstance().reference
                .child("UserTimeLine")
                .child(user)
            val postMap  = HashMap<String, Any>()
            postMap["post_type"]="sharepost"
            postMap["id"]=idref
            postMap["active"]=true

            timelineRef.push().setValue(postMap)
        }
        finish()
        Toast.makeText(this, "Chia sẻ thành công",Toast.LENGTH_SHORT).show()
    }

}