package com.lamhong.viesocial

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.CommentAdapter
import com.lamhong.viesocial.Models.Comment
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_replay_comment.*
import kotlinx.android.synthetic.main.activity_replay_comment.btn_close
import kotlinx.android.synthetic.main.activity_replay_comment.btn_dangBinhLuan
import kotlinx.android.synthetic.main.activity_replay_comment.edit_add_comment

class ReplayCommentActivity : AppCompatActivity() {
    private var idUser : String =""
    private var content : String =""
    private var idComment : String =""
    private var timecmt :String ?= null

    private var commentAdapter : CommentAdapter?=null
    private var commentList : MutableList<Comment>?=null

    private var firebaseUser: FirebaseUser= FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replay_comment)
        btn_close.setOnClickListener{
            this.finish()
        }

        idUser= intent.getStringExtra("idUser").toString()
        content= intent.getStringExtra("content").toString()
        idComment= intent.getStringExtra("idComment").toString()
        timecmt = intent.getStringExtra("timecmt").toString()

        showUserInfor()
        //show cmt
        var recyclerView  : RecyclerView = findViewById(R.id.recycleview_repcmt)
        val linearLayoutManager : LinearLayoutManager = LinearLayoutManager(this)
        //linearLayoutManager.reverseLayout=true
        recyclerView.layoutManager= linearLayoutManager

        commentList= ArrayList()
        commentAdapter= CommentAdapter(this, commentList as ArrayList<Comment>)
        recyclerView.adapter=commentAdapter
        recyclerView.visibility= View.VISIBLE

        viewComment()
        btn_dangBinhLuan.setOnClickListener {
            if (TextUtils.isEmpty(edit_add_comment.text)) {
                Toast.makeText(this, "Nhập nội dung trước !!", Toast.LENGTH_LONG)
            } else {
                addComment()
//                linearLayoutManager.stackFromEnd= true
                recyclerView.scrollToPosition((commentList as ArrayList).size- 1);
//                recyclerView.layoutManager= linearLayoutManager
            }
        }

        // set function to btn in top bar
        checkLike(btn_thich_repcmt, idComment)
        setLike(idComment,numLike_repcmt, image_numlike_repcmt)
        setCommentNumber(idComment,btn_cmt_repcmt )
        btn_thich_repcmt.setOnClickListener{
            if(btn_thich_repcmt.tag=="Like"){
                FirebaseDatabase.getInstance().reference.child("LikeComment")
                    .child(idComment).child(firebaseUser.uid).setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().reference.child("LikeComment")
                    .child(idComment).child(firebaseUser.uid).removeValue()
            }
        }


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
    private fun viewComment(){
        val commentRef= FirebaseDatabase.getInstance().reference.child("AllComment")
            .child("CommentReplays").child(idComment)
        commentRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    commentList!!.clear()
                    for(snap in snapshot.children){
                        val comment : Comment= snap.getValue(Comment::class.java)!!
                        comment.setOwner(snap.child("ownerComment").value.toString())
                        comment.setTimeStamp(snap.child("timestamp").value.toString())
                        commentList!!.add(comment)
                    }
                    // (commentList as ArrayList).reverse()
                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun addComment() {
        val timestamp= System.currentTimeMillis().toString()
        val commentRef= FirebaseDatabase.getInstance().reference.child("AllComment")
            .child("CommentReplays").child(idComment)
        val commentMap =HashMap<String, Any>()
        val key : String = commentRef.push().key.toString()
        commentMap["content"]=edit_add_comment.text.toString()
        commentMap["ownerComment"]=firebaseUser!!.uid
        commentMap["idComment"]=key
        commentMap["timestamp"] = timestamp
        commentRef.child(key).setValue(commentMap)

         edit_add_comment.text.clear()
         addNotify()
        val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
             imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
    }

    private fun addNotify() {

    }

    private fun showUserInfor() {
        val ref= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(idUser)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user.getAvatar()).into(image_avatar_repcmt)
                    tv_username_repcmt.text=user.getName()
                    tv_content_repcmt.text=content
                    time_repcmt.text=Constants.getTimeCmt(timecmt)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        val ref1= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser.uid)
        ref1.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user.getAvatar()).into(avatar_this_repcmt)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}