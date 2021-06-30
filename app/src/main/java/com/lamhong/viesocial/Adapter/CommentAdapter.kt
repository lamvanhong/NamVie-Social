package com.lamhong.viesocial.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.Comment
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private val mContext: Context, private val mComment : MutableList<Comment>)
    :RecyclerView.Adapter<CommentAdapter.ViewHolder>(){


    private var firebaseUser : FirebaseUser?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment[position]
        holder.content.text=comment.getContent()
        getUserInfor(holder.imageAvatar, holder.username, comment.getOwner())
            //test
    }

    private fun getUserInfor(imageAvatar: CircleImageView, username: TextView, owner: String) {
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(owner)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user: User= snapshot.getValue(User::class.java)!!
                    user.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user.getAvatar()).placeholder(R.drawable.duongtu).into(imageAvatar)
                    username.text=user.getName()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemCount(): Int {
        return mComment.size
    }
    inner class ViewHolder(@NonNull itemview: android.view.View): RecyclerView.ViewHolder(itemview){
        val imageAvatar : CircleImageView
        val username : TextView
        val content: TextView
        init {
            imageAvatar=itemView.findViewById(R.id.image_avatar_eachComment)
            username=itemView.findViewById(R.id.tv_username_item)
            content=itemView.findViewById(R.id.tv_content_item)
        }
    }
}