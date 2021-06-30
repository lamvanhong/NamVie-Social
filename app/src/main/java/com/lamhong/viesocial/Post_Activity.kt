package com.lamhong.viesocial

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.lamhong.viesocial.Fragment.zHome
import com.lamhong.viesocial.Models.Comment
import com.lamhong.viesocial.Models.User
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_post_.*

class Post_Activity : AppCompatActivity() {
    private var myUrl=""
    private var imageUir : Uri?=null
    private var storagePostRef : StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_)

        storagePostRef = FirebaseStorage.getInstance().reference.child("Posts Images")

        btn_publish.setOnClickListener {
            uploadImage()
        }
        image_content.setOnClickListener{
            CropImage.activity()
                .setAspectRatio(7,5)
                .start(this)
        }



    }
//    private fun viewComment(){
//        val commentRef= FirebaseDatabase.getInstance().reference
//            .child("Comments").child(postID)
//        commentRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    commentList!!.clear()
//                    for(snap in snapshot.children){
//                        val comment : Comment = snap.getValue(Comment::class.java)!!
//                        comment.setOwner(snap.child("ownerComment").value.toString())
//                        commentList!!.add(comment)
//                    }
//                    commentAdapter!!.notifyDataSetChanged()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }
//    private fun addComment(){
//        val commentRef= FirebaseDatabase.getInstance().reference
//            .child("Comments").child(postID)
//        val commentMap =HashMap<String, Any>()
//        commentMap["content"]=edit_add_comment.text.toString()
//        commentMap["ownerComment"]=firebaseUser!!.uid
//        commentRef.push().setValue(commentMap)
//
//        edit_add_comment.text.clear()
//    }
//    private fun userInfor(){
//        val userRef=FirebaseDatabase.getInstance().reference
//            .child("UserInformation").child(firebaseUser!!.uid)
//        userRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    val user = snapshot.getValue(User::class.java)
//                    //Picasso.get().load(user!!.getAvatar()).into(image_post_incomment)
//                    Picasso.get().load(user!!.getAvatar()).into(image_avatar_incomment)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }
//    private fun imageandOwnerInfor(){
//        val userref= FirebaseDatabase.getInstance().reference
//            .child("UserInformation").child(publisher)
//        userref.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    val fname= snapshot.child("fullname").value.toString()
//                    tv_comment_appbar.text= "Bài viết của "+ fname
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//
//    }
//    private fun getImage(){
//        val postRef= FirebaseDatabase.getInstance().reference.child("Posts")
//            .child(postID).child("post_image")
//
//        postRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    val imageContent= snapshot.value.toString()
//                    Picasso.get().load(imageContent).into(image_post_incomment)
//                }
//                else {
//                    Log.d("hong","nothing")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode==Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUir=result.uri
            image_content.setImageURI(imageUir)
        }
    }
    fun onActivityResult1(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode==Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUir=result.uri
            image_content.setImageURI(imageUir)
        }
    }
    private fun uploadImage(){

        when{
            imageUir==null -> Toast.makeText(this, "Vui lòng chọn ảnh !!" , Toast.LENGTH_SHORT).show()
            else ->{
                val progressDialog =ProgressDialog(this)
                progressDialog.setTitle("Tạo bài viết")
                progressDialog.setMessage("Đang tạo bài viết, chờ chút ...")
                progressDialog.show()

                val file = storagePostRef?.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask : StorageTask<*>
                uploadTask=file!!.putFile(imageUir!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->
                    if(!task.isSuccessful){
                        task.exception.let {
                            progressDialog.dismiss()
                            throw it!!

                        }
                    }else{
                        return@Continuation file.downloadUrl

                    }
                }).addOnCompleteListener(OnCompleteListener<Uri>{ task ->
                    if(task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl=downloadUrl.toString()

                        val ref= FirebaseDatabase.getInstance().reference.child("Posts")

                        val inMap = HashMap<String, Any>()
                        inMap["post_id"]=(ref.push().key)!!
                        inMap["post_image"]= myUrl
                        inMap["publisher"]= FirebaseAuth.getInstance().currentUser.uid.toString()
                        inMap["post_content"]= edit_content.text.toString()




                        ref.child(ref.push().key.toString()).setValue(inMap)
                        val intent = Intent(this@Post_Activity, zHome::class.java)
                        Toast.makeText(this, "Đã cập nhật thông tin !!", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()


                    }else{
                        progressDialog.dismiss()
                    }
                })
            }
        }

    }
}