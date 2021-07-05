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
import com.lamhong.viesocial.Models.TimelineContent
import kotlinx.android.synthetic.main.activity_user_save_post.*

class UserSavePostActivity : AppCompatActivity() {

    private lateinit var firebaseUser : FirebaseUser
    private var avatarList : ArrayList<Post> = ArrayList()
    private var coverImageList : ArrayList<Post> = ArrayList()
    private var shareList: MutableList<SharePost> = ArrayList()

    private var lstTypeAdapter : List<Int> = ArrayList()
    private var lstIndex : List<Int> = ArrayList()
    private var imagePostList : List<Post> = ArrayList()
    private var postAdapter: PostAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_save_post)
        btn_return_fromsavepost.setOnClickListener{
            this.finish()
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        ShowImagePost1()
        //recycleview_activities
        var recycleview1 : RecyclerView
        recycleview1= findViewById(R.id.recycleview_save)
        val linearLayoutManager1 = LinearLayoutManager(this)
        linearLayoutManager1.stackFromEnd=true
        linearLayoutManager1.reverseLayout=true
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
                    var ind1 = 0
                    var ind2 = 0
                    var ind3=0
                    var ind4=0
                    for (s in snapshot.child("SavePost")
                        .child(firebaseUser.uid).children) {
                        val tl = s.getValue(TimelineContent::class.java)
                        tl!!.setPostType(s.child("post_type").value.toString())
                        if (tl!!.getPostType() == "sharepost") {
                            if(snapshot.child("Share Posts").child(tl.getId()).exists()){
                                var sharePost = snapshot.child("Share Posts").child(tl.getId()).getValue<SharePost>(SharePost::class.java)
                                shareList!!.add(sharePost!!)
                                (lstTypeAdapter as ArrayList).add(1)
                                (lstIndex as ArrayList).add(ind1)
                                ind1 += 1
                            }

                        } else if (tl!!.getPostType() == "post") {


                            val post = snapshot.child("Posts").child(tl.getId()).getValue(Post::class.java)
                            (imagePostList as ArrayList<Post>).add(post!!)
                            (lstTypeAdapter as ArrayList).add(0)
                            (lstIndex as ArrayList).add(ind2)
                            ind2 += 1
                        }
                        else if (tl!!.getPostType()=="changeavatar"){
                            val avatarPost = snapshot.child("AvatarPost").child(tl.getId()).getValue(Post::class.java)
                            avatarPost!!.setpostContent(snapshot.child("AvatarPost").child(tl.getId()).child("post_content")
                                .value.toString())


                            avatarList!!.add(avatarPost!!)
                            (lstTypeAdapter as ArrayList).add(2)
                            (lstIndex as ArrayList).add(ind3)
                            ind3+=1
                        }
                        else if(tl!!.getPostType()=="changecover"){
                            val coverImagePost = snapshot.child("CoverPost").child(tl.getId()).getValue(Post::class.java)
                            coverImagePost!!.setpostContent(snapshot.child("CoverPost").child(tl.getId()).child("post_content").value.toString())
                            coverImageList!!.add(coverImagePost!!)
                            (lstTypeAdapter as ArrayList).add(3)
                            (lstIndex as ArrayList).add(ind4)
                            ind4+=1
                        }
                        //  getPostAndShare()
                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }
        })

    }
}