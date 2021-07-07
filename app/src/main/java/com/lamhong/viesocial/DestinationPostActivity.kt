package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.PostAdapter
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.Models.SharePost
import kotlinx.android.synthetic.main.activity_user_save_post.*

class DestinationPostActivity : AppCompatActivity() {

    private lateinit var firebaseUser : FirebaseUser
    private var avatarList : ArrayList<Post> = ArrayList()
    private var coverImageList : ArrayList<Post> = ArrayList()
    private var shareList: MutableList<SharePost> = ArrayList()

    private var lstTypeAdapter : List<Int> = ArrayList()
    private var lstIndex : List<Int> = ArrayList()
    private var imagePostList : List<Post> = ArrayList()
    private var postAdapter: PostAdapter?=null

    var type : String =""
    var id : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_post)

        type= intent.getStringExtra("type").toString()
        id = intent.getStringExtra("id").toString()
        btn_return_fromsavepost.setOnClickListener{
            this.finish()
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        ShowImagePost1()
        //recycleview_activities
        var recycleview1 : RecyclerView
        recycleview1= findViewById(R.id.recycleview_des)
        val linearLayoutManager1 = LinearLayoutManager(this)
        recycleview1.layoutManager= linearLayoutManager1
        postAdapter=  PostAdapter(this, imagePostList as ArrayList<Post> , lstIndex as ArrayList,
            lstTypeAdapter as ArrayList, shareList as ArrayList , avatarList as ArrayList,
            coverImageList as ArrayList)
        recycleview1.adapter= postAdapter
    }
    private fun ShowImagePost1(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    shareList!!.clear()
                    (imagePostList as ArrayList<Post>).clear()
                    (lstIndex as ArrayList).clear()
                    (lstTypeAdapter as ArrayList).clear()
                    if (type== "sharepost") {
                        if(snapshot.child("Share Posts").child(id).exists()){
                            var sharePost = snapshot.child("Share Posts").child(id).getValue<SharePost>(SharePost::class.java)
                            shareList!!.add(sharePost!!)
                            (lstTypeAdapter as ArrayList).add(1)
                            (lstIndex as ArrayList).add(0)
                        }

                    } else if (type== "post") {


                        val post = snapshot.child("Posts").child(id).getValue(Post::class.java)
                        (imagePostList as ArrayList<Post>).add(post!!)
                        (lstTypeAdapter as ArrayList).add(0)
                        (lstIndex as ArrayList).add(0)

                    }
                    else if (type=="changeavatar"){
                        val avatarPost = snapshot.child("AvatarPost").child(id).getValue(Post::class.java)
                        avatarPost!!.setpostContent(snapshot.child("AvatarPost").child(id).child("post_content")
                            .value.toString())


                        avatarList!!.add(avatarPost!!)
                        (lstTypeAdapter as ArrayList).add(2)
                        (lstIndex as ArrayList).add(0)
                    }
                    else if(type=="changecover"){
                        val coverImagePost = snapshot.child("CoverPost").child(id).getValue(Post::class.java)
                        coverImagePost!!.setpostContent(snapshot.child("CoverPost").child(id).child("post_content").value.toString())
                        coverImageList!!.add(coverImagePost!!)
                        (lstTypeAdapter as ArrayList).add(3)
                        (lstIndex as ArrayList).add(0)
                    }
                    //  getPostAndShare()
                    postAdapter!!.notifyDataSetChanged()
                   
                        
                    
                }

            }
        })

    }
}