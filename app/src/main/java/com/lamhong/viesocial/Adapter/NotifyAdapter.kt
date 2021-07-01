package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.CommentActivity
import com.lamhong.viesocial.DetailPostFragment
import com.lamhong.viesocial.Fragment.ProfileFragment
import com.lamhong.viesocial.Models.Notify
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso

class NotifyAdapter (private val mContext : Context, private val mLstNotify: List<Notify>)
    : RecyclerView.Adapter<NotifyAdapter.ViewHolder>(){
    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var avatar_image: ImageView
        var contentNotify: TextView
        var uname: TextView
        var postImage: ImageView
        init {
            avatar_image= itemView.findViewById(R.id.avatar_innotifi)
            contentNotify= itemView.findViewById(R.id.content_innotifi)
            uname=itemView.findViewById(R.id.userName_innotifi)
            postImage= itemView.findViewById(R.id.post_imageview)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notify  = mLstNotify[position]
        if(notify.getType()=="thichbaiviet" || notify.getType()==""){
            holder.postImage.visibility=View.VISIBLE
            showImagePost(holder.postImage,notify.gePostID())
            // navigate to post detail

            holder.contentNotify.text="Đã thích bài viết của bạn"
        }
        else if (notify.getType()=="binhluan"){
            holder.postImage.visibility=View.VISIBLE
            showImagePost(holder.postImage,notify.gePostID())
            holder.contentNotify.text="Đã bình luận trên bài viết của bạn"
        }
            else if(notify.getType()=="loimoiketban"){
            holder.postImage.visibility=View.GONE
            holder.contentNotify.text="Đã gửi lời mời kết bạn"
        }
        showUserInfor(holder.avatar_image, holder.uname, notify.getUserID())
        holder.itemView.setOnClickListener{
            if(notify.getType()=="loimoiketban"){
                val pref= mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", notify.getUserID())
                pref.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, ProfileFragment()).commit()

            }
            else if(notify.getType()=="thichbaiviet") {
                val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                pref.putString("postID",notify.gePostID())
                pref.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, DetailPostFragment()).commit()


            }
            else if(notify.getType()=="binhluan"){
                val commentIntent= Intent(mContext, CommentActivity::class.java)
                commentIntent.putExtra("postID", notify.gePostID())
                commentIntent.putExtra("publisher", notify.getUserID())
                mContext.startActivity(commentIntent)


            }
        }

    }

    private fun showUserInfor(avatar: ImageView, username: TextView, publisher: String){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(publisher)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.cty).into(avatar)
                    username.text=user.getName()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun showImagePost(postImage: ImageView, postID: String){
        val postRef= FirebaseDatabase.getInstance().reference
            .child("Posts").child(postID)
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val post = snapshot.getValue(Post::class.java)
                    post!!.setpost_image(snapshot.child("post_image").value.toString())
                    Picasso.get().load(post!!.getpost_image()).placeholder(R.drawable.duongtu).into(postImage)

                }
            }
        })
    }

    override fun getItemCount(): Int {
        return mLstNotify.size
    }
}