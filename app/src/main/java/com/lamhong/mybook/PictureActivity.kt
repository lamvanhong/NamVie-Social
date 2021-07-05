package com.lamhong.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Adapter.ImageProfileAdapter
import com.lamhong.mybook.Models.Post
import com.lamhong.mybook.Models.SharePost
import com.lamhong.mybook.Models.TimelineContent
import kotlinx.android.synthetic.main.activity_picture.*

class PictureActivity : AppCompatActivity() {
    private var postList : List<Post> = ArrayList()
    private var ImageAdapter : ImageProfileAdapter ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)


        // adapter
        var recyclerView : RecyclerView
        recyclerView= findViewById(R.id.recycleview_fullpicture)
        var linearLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager= linearLayoutManager
        ImageAdapter= this?.let { ImageProfileAdapter(it, postList as ArrayList, 700) }
        recyclerView.adapter=ImageAdapter
        getPicture()

        //function
        btn_return_picture.setOnClickListener{
            this.finish()
        }
        btn_selectMode.tag="option1"
        btn_selectMode.setOnClickListener{
            if(btn_selectMode.tag=="option1")
            {
                linearLayoutManager= GridLayoutManager(this, 2)
                ImageAdapter= this?.let { ImageProfileAdapter(it, postList as ArrayList, 400) }
                btn_selectMode.tag="option2"
            }
            else if (btn_selectMode.tag=="option2"){
                linearLayoutManager= GridLayoutManager(this, 1)
                ImageAdapter= this?.let { ImageProfileAdapter(it, postList as ArrayList, 700) }
                btn_selectMode.tag="option1"
            }
            recyclerView.layoutManager= linearLayoutManager

            recyclerView.adapter=ImageAdapter

        }



    }
    private fun getPicture(){
        val postRef=FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (postList as ArrayList<Post>).clear()
                    for (s in snapshot.children){
                        val post= s.getValue(Post::class.java)
                        post!!.setpost_id(s.child("post_id").value.toString())
                        (postList as ArrayList<Post>).add(post!!)
                        //Collections.reverse(postList)
                        ImageAdapter!!.notifyDataSetChanged()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}