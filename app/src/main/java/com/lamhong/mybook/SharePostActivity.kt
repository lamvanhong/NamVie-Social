package com.lamhong.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Fragment.zHome
import com.lamhong.mybook.Models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_share_post.*

class SharePostActivity : AppCompatActivity() {

    private var postID: String=""
    private var publisher: String=""
    private var firebaseUser : FirebaseUser?=null
    private var followingList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post)
        firebaseUser=FirebaseAuth.getInstance().currentUser
        postID= intent.getStringExtra("postID").toString()
        publisher= intent.getStringExtra("publisher").toString()
        btn_dangShare.setOnClickListener{
            sharePost()
        }
        btn_close.setOnClickListener{
            finish()
        }
        getFollowinglist()
        showProfile()

    }

    private fun showProfile() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("UserInformation")
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val userSharing = snapshot.child(firebaseUser!!.uid!!).getValue(User::class.java)
                    userSharing!!.setName(snapshot.child(firebaseUser!!.uid!!).child("fullname").value.toString())
                    val userShared = snapshot.child(publisher).getValue(User::class.java)
                    userShared!!.setName(snapshot.child(publisher).child("fullname").value.toString())

                    name_userSharing.setText(userSharing.getName().toString())
                    name_userShared.setText(userShared.getName())
                    Picasso.get().load(userSharing.getAvatar()).into(avatar_userSharing)
                    Picasso.get().load(userShared.getAvatar()).into(avatar_userShared)
                }

            }
        })
        val postRef= FirebaseDatabase.getInstance().reference
            .child("Contents").child("Posts").child(postID)
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageuri= snapshot.child("post_image").value.toString()
                    val content= snapshot.child("post_content").value.toString()
                    content_inshared.text=content
                    Picasso.get().load(imageuri).into(imagePost)
                }
            }
        })

    }

    private fun getFollowinglist(){
        val ref = FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .child("friendList")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    ( followingList as ArrayList).clear()
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
        val shareRef= FirebaseDatabase.getInstance().reference.child("Contents")
            .child("Share Posts")
        val shareMap = HashMap<String, Any>()

        val idref= shareRef.push().key.toString()
        shareMap["shareID"]=idref
        shareMap["postID"]=postID
        shareMap["content"]=edit_content.text.toString()
        shareMap["type"]="baiviet"
        shareMap["typeshare"]="friend"
        shareMap["publisher"]=firebaseUser!!.uid


        val timelineUser= FirebaseDatabase.getInstance().reference.child("Contents")
            .child("ProfileTimeLine").child(FirebaseAuth.getInstance().currentUser.uid)
        val pMap = HashMap<String, Any>()
        pMap["post_type"]="sharepost"
        pMap["id"]=idref
        pMap["active"]=true


        shareRef.child(idref).setValue(shareMap)
        timelineUser.push().setValue(pMap)
        for(user in followingList!!){
            val timelineRef= FirebaseDatabase.getInstance().reference.child("Contents")
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