package com.lamhong.viesocial

import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lamhong.viesocial.Adapter.CommentAdapter
import com.lamhong.viesocial.Adapter.TOPIC
import com.lamhong.viesocial.Models.Comment
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Network.MyFirebaseMessagingService
import com.lamhong.viesocial.Utilities.Constants
import com.lamhong.viesocial.Utilities.Constants.Companion.doSendNotify
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*

class  CommentActivity : AppCompatActivity() {

    private var postID : String= ""
    private var type : String= ""
    private var publisher: String = ""
    private var firebaseUser: FirebaseUser ?=null
    private var commentAdapter : CommentAdapter?=null
    private var commentList : MutableList<Comment>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        btn_close.setOnClickListener{
            this.finish()
        }


        val intent = intent
        postID= intent.getStringExtra("postID").toString()
        publisher= intent.getStringExtra("publisher").toString()
        type= intent.getStringExtra("type").toString()
        firebaseUser=FirebaseAuth.getInstance().currentUser

        //Notification
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                val fbToken = it.result.toString()
                MyFirebaseMessagingService.token =fbToken
            }
        }
        var nameuser =""
        val userRef = FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    nameuser= snapshot.child("fullname").value.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        var token =""
        val userRef2 = FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(publisher)
        userRef2.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    token = (snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        //End notification
        userInfor()
        imageandOwnerInfor()
        // set image to separate post
        if(type=="post"){
            getImagePost()
            container_post_avatar_comment.visibility= View.GONE

        }
        else if(type=="avatar"){
            getImageAvatar()
            image_post_incomment.visibility= View.GONE

        }
        else if (type=="cover"){
            getCoverPost()
            container_post_avatar_comment.visibility= View.GONE

        }

        btn_dangBinhLuan.setOnClickListener {
            if (TextUtils.isEmpty(edit_add_comment.text)) {
                Toast.makeText(this, "Nhập nội dung trước !!", Toast.LENGTH_LONG)
            } else {
                addComment(nameuser, token)
            }
        }
        // add recycleview
        val recyclerView : RecyclerView
        recyclerView= findViewById(R.id.recycleview_comment)
        val linearLayoutManager : LinearLayoutManager= LinearLayoutManager(this)
        //linearLayoutManager.reverseLayout=true
        recyclerView.layoutManager= linearLayoutManager


        commentList= ArrayList()
        commentAdapter= CommentAdapter(this, commentList as ArrayList<Comment>)
        recyclerView.adapter=commentAdapter
        recyclerView.visibility=View.VISIBLE

        viewComment()

    }
    private fun viewComment(){
        val commentRef= FirebaseDatabase.getInstance().reference.child("AllComment")
            .child("Comments").child(postID)
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


    private fun addComment(nameuser : String, token: String){
        val timestamp= System.currentTimeMillis().toString()

        val commentRef= FirebaseDatabase.getInstance().reference.child("AllComment")
            .child("Comments").child(postID)
        val commentMap =HashMap<String, Any>()
        val key : String = commentRef.push().key.toString()
        commentMap["content"]=edit_add_comment.text.toString()
        commentMap["ownerComment"]=firebaseUser!!.uid
        commentMap["idComment"]=key
        commentMap["timestamp"] = timestamp
        commentRef.child(key).setValue(commentMap)

        edit_add_comment.text.clear()
        addNotify()
        doSendNotify(nameuser, token, "đã thích bài viết của bạn")
        val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
    }
    private fun addNotify(){

        val notiRef= FirebaseDatabase.getInstance().reference.child("Notify")
            .child(publisher)
        val notiMap = HashMap<String,Any>()
        val idpush : String = notiRef.push().key.toString()
        notiMap["userID"]=firebaseUser!!.uid
        notiMap["notify"]=type
        notiMap["postID"]=postID
        notiMap["type"]="binhluan"
        notiMap["notifyID"]=idpush

        notiRef.child(idpush).setValue(notiMap)

    }
    private fun userInfor(){
        val userRef=FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                   //Picasso.get().load(user!!.getAvatar()).into(image_post_incomment)
                   Picasso.get().load(user!!.getAvatar()).into(image_avatar_incomment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun imageandOwnerInfor(){
        val userref= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(publisher)
        userref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val fname= snapshot.child("fullname").value.toString()
                    val fnamefix= getColoredSpanned(fname, "#00BCD4")
                    val anhbiacua= getColoredSpanned("Ảnh bìa của ", "#A1B4B6")
                    val baiviet= getColoredSpanned("Bài viết của ", "#A1B4B6")
                    val anhdaidien= getColoredSpanned("Ảnh đại diện của ", "#A1B4B6")
                    if(type=="cover"){
                        tv_comment_appbar.setText(Html.fromHtml(anhbiacua + fname))
                    }
                    else if(type=="post")
                    {
                        tv_comment_appbar.setText(Html.fromHtml(baiviet + fname))
                    }
                    else if(type=="avatar"){
                        tv_comment_appbar.setText(Html.fromHtml(anhdaidien + fname))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }


    private fun getImagePost(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents").child("Posts")
            .child(postID).child("post_image")

        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageContent= snapshot.value.toString()
                    Picasso.get().load(imageContent).into(image_post_incomment)
                }
                else {
                    Log.d("hong","nothing")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun getCoverPost(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents").child("CoverPost")
            .child(postID).child("post_image")

        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageContent= snapshot.value.toString()
                    Picasso.get().load(imageContent).into(image_post_incomment)
                }
                else {
                    Log.d("hong","nothing")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun getImageAvatar(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents").child("AvatarPost")
            .child(postID).child("post_image")

        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageContent= snapshot.value.toString()
                    Picasso.get().load(imageContent).into(post_avatar_comment)
                }
                else {
                    Log.d("hong","nothing")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}