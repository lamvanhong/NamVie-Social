package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.lamhong.viesocial.ReplayCommentActivity
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private val mContext: Context, private val mComment : MutableList<Comment>)
    :RecyclerView.Adapter<CommentAdapter.ViewHolder>(){


    private var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout , parent, false)
        return ViewHolder(view)
    }
    inner class ViewHolder(@NonNull itemview: android.view.View): RecyclerView.ViewHolder(itemview){
        val imageAvatar : CircleImageView
        val username : TextView
        val content: TextView
        val btnThich : TextView
        val numLike : TextView
        val imageNumlike : ImageView
        val btnComment: TextView
        val timeCmtTV : TextView
        init {
            imageAvatar=itemView.findViewById(R.id.image_avatar_eachComment)
            username=itemView.findViewById(R.id.tv_username_item)
            content=itemView.findViewById(R.id.tv_content_item)
            btnThich = itemview.findViewById(R.id.btn_thich_cmt)
            numLike= itemview.findViewById(R.id.numLike_cmt)
            imageNumlike = itemview.findViewById(R.id.image_numlike_cmt)
            btnComment= itemview.findViewById(R.id.btn_cmt_cmt)
            timeCmtTV = itemview.findViewById(R.id.time_cmt)
        }
    }
    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {

        val comment = mComment[position]
        holder.content.text=comment.getContent()
        holder.timeCmtTV.text = Constants.getTimeCmt(comment.getTimeStamp())
        getUserInfor(holder.imageAvatar, holder.username, comment.getOwner())
            //test
        // function to btn
        checkLike(holder.btnThich, comment.getIdComment()  )
        setLike(comment.getIdComment() , holder.numLike, holder.imageNumlike)
        setCommentNumber(comment.getIdComment(), holder.btnComment)
        holder.btnThich.setOnClickListener{
            if(holder.btnThich.tag=="Like"){
                FirebaseDatabase.getInstance().reference.child("LikeComment")
                    .child(comment.getIdComment()).child(firebaseUser.uid).setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().reference.child("LikeComment")
                    .child(comment.getIdComment()).child(firebaseUser.uid).removeValue()
            }
        }
        holder.btnComment.setOnClickListener{
            val cmtIntent : Intent = Intent(mContext, ReplayCommentActivity::class.java)
            cmtIntent.putExtra("idUser" , comment.getOwner())
            cmtIntent.putExtra("content", comment.getContent())
            cmtIntent.putExtra("idComment", comment.getIdComment())
            cmtIntent.putExtra("timecmt",comment.getTimeStamp())
            mContext.startActivity(cmtIntent)
        }
    }

    private fun setLike(idComment: String , numLike : TextView , imageNumlike : ImageView) {
        val ref =FirebaseDatabase.getInstance().reference.child("LikeComment")
            .child(idComment)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    numLike.text=snapshot.childrenCount.toString()
                    imageNumlike.visibility= View.VISIBLE
                    numLike.visibility=View.VISIBLE
                }
                else{
                    numLike.text="0"
                    imageNumlike.visibility= View.GONE
                    numLike.visibility=View.GONE
                }
            }
        })
    }

    private fun setCommentNumber(idComment: String , numComment : TextView) {
        val ref =FirebaseDatabase.getInstance().reference.child("AllComment").child("CommentReplays")
            .child(idComment)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    numComment.text="Trả lời (${snapshot.childrenCount.toString()})"

                }
                else{
                    numComment.text="Trả lời"
                }
            }
        })
    }

    private fun checkLike(btnThich: TextView, idComment: String) {
        val ref= FirebaseDatabase.getInstance().reference
            .child("LikeComment").child(idComment).child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    btnThich.tag="Liked"
                    btnThich.setTextColor((Color.parseColor("#FF77ED")))
                }
                else{
                    btnThich.tag="Like"
                    btnThich.setTextColor((Color.parseColor("#9F9F9F")))
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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

}