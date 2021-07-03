package com.lamhong.viesocial

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_post_.*

class Post_Activity : AppCompatActivity() {
    private var myUrl=""
    private var imageUir : Uri?=null
    private var storagePostRef : StorageReference? = null
    private var followingList: ArrayList<String> =ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_)
        getFollowinglist()
        storagePostRef = FirebaseStorage.getInstance().reference.child( "Posts Images")

        btn_publish.setOnClickListener {
            uploadImage()
        }
        image_content.setOnClickListener{
            CropImage.activity()
                .setAspectRatio(7,5)
                .start(this)
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode==Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUir=result.uri
            image_content.setImageURI(imageUir)
        }
    }
    private fun checkFollowing() {
        followingList=ArrayList()

        val followRef = FirebaseDatabase.getInstance().reference
            .child("Friends").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("friendList")
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (s in snapshot.children){
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })}


    private fun getFollowinglist(){
        val ref = FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .child("friendList")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    followingList!!.clear()
                    for (s in snapshot.children){
                        followingList!!.add(s.key.toString())

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun checkStatusFriend() {
        followingList=ArrayList()

        val followRef = FirebaseDatabase.getInstance().reference
            .child("Friends").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("friendList")
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (s in snapshot.children){
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })}
    private fun uploadImage(){
        if(imageUir==null) {
            Toast.makeText(this, "Vui lòng chọn ảnh !!", Toast.LENGTH_SHORT).show()
            return
            }


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

                val ref= FirebaseDatabase.getInstance().reference.child("Contents").child("Posts")

                val inMap = HashMap<String, Any>()
                // inMap["post_id"]=(ref.push().key)!!
                val nunu : String = ref.push().key.toString()

                inMap["post_id"]=(nunu)!!
                inMap["post_image"]= myUrl
                inMap["publisher"]= FirebaseAuth.getInstance().currentUser.uid.toString()
                inMap["post_content"]= edit_content.text.toString()

                ref.child(nunu).setValue(inMap)

                val timelineUser= FirebaseDatabase.getInstance().reference.child("Contents")
                    .child("ProfileTimeLine").child(FirebaseAuth.getInstance().currentUser.uid)
                val pMap = HashMap<String, Any>()
                pMap["post_type"]="post"
                pMap["id"]=nunu
                pMap["active"]=true
                timelineUser.push().setValue(pMap)

                // getFollowinglist()

                for(user in followingList!!){
                    val timelineRef= FirebaseDatabase.getInstance().reference.child("Contents")
                        .child("UserTimeLine")
                        .child(user)
                    val postMap  = HashMap<String, Any>()
                    postMap["post_type"]="post"
                    postMap["id"]=nunu
                    postMap["active"]=true

                    timelineRef.push().setValue(postMap)

                }

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