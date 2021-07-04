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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_change_avatar.*
import kotlinx.android.synthetic.main.activity_post_.*
import kotlinx.android.synthetic.main.activity_profile_editting.*
import kotlinx.android.synthetic.main.activity_profile_editting.btn_return

class ChangeAvatarActivity : AppCompatActivity() {
    private var imageUri : Uri?=null
    private var storageRef : StorageReference?=null
    private var followingList : ArrayList<String> = ArrayList()
    lateinit var firebaseUser : FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)

        // initial firebase
        firebaseUser= FirebaseAuth.getInstance().currentUser
        storageRef= FirebaseStorage.getInstance().reference.child("Posts Images")
        getFollowinglist()

        CropImage.activity().setAspectRatio(1,1).start(this)
        btn_return.setOnClickListener{
            finish()
        }
        //edit_content_avatarchanging
        avatar_inchangeAvatar.setOnClickListener{
            CropImage.activity().setAspectRatio(1,1).start(this)
        }
        btn_dangChangeAvatar.setOnClickListener{
            createAvatarChanging()
        }
    }

    private fun createAvatarChanging() {
        when{
            imageUri==null ->{
                Toast.makeText(this, "Vui lòng chọn ảnh !!", Toast.LENGTH_SHORT).show()
            }
            else->{
                var processDiaglog = ProgressDialog(this)
                processDiaglog.setTitle("Tải ảnh lên")
                processDiaglog.setMessage("Đang thay ảnh đại diện, chờ chút ...")
                processDiaglog.show()

                val file = storageRef?.child(firebaseUser.uid + ".jpg")

                val storageTask: StorageTask<*>
                storageTask = file!!.putFile(imageUri!!)
                storageTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->
                    if(!task.isSuccessful){
                        task.exception.let {
                            processDiaglog.dismiss()
                            throw it!!
                        }
                    }else{
                        return@Continuation file.downloadUrl
                    }
                }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
                    if(task.isSuccessful){
                        val downloadurl = task.result.toString()
                        val ref = FirebaseDatabase.getInstance().reference.child("Contents").child("AvatarPost")

                        val inMap = HashMap<String, Any>()
                        val key = ref.push().key.toString()
                        inMap["post_id"]=(key)!!
                        inMap["post_image"]= downloadurl
                        inMap["publisher"]= FirebaseAuth.getInstance().currentUser.uid.toString()
                        inMap["post_content"]= edit_content_avatarchanging.text.toString()
                        ref.child(key).setValue(inMap)

                        val userRef=FirebaseDatabase.getInstance().reference.child("UserInformation")
                            userRef.child(firebaseUser.uid).child("avatar").setValue(downloadurl)

                        val timelineUser= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("ProfileTimeLine").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="changeavatar"
                        pMap["id"]=key
                        pMap["active"]=true
                        timelineUser.child(key).setValue(pMap)

                        // getFollowinglist()

                        for(user in followingList!!){
                            val timelineRef= FirebaseDatabase.getInstance().reference.child("Contents")
                                .child("UserTimeLine")
                                .child(user)
                            val postMap  = HashMap<String, Any>()
                            postMap["post_type"]="changeavatar"
                            postMap["id"]=key
                            postMap["active"]=true

                            timelineRef.child(key).setValue(postMap)
                        }
                        // notify to close user
                        for(user in followingList!!){ // we will custom followinglist later
                            val notifyRef= FirebaseDatabase.getInstance().reference.child("Notify")
                                .child(user)
                            val notifyMap = HashMap<String, Any>()
                            val key1 = notifyRef.push().key.toString()
                            notifyMap["postID"]= key
                            notifyMap["type"]="changeavatar"
                            notifyMap["notifyID"]=key1
                            notifyRef.child(key1).setValue(notifyMap)

                        }

                        Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show()
                        finish()
                        processDiaglog.dismiss()
                    }
                    else{
                        processDiaglog.dismiss()
                        Toast.makeText(this, "Xuất hiện lỗi, vui lòng thử lại sau !!!", Toast.LENGTH_SHORT).show()
                    }


                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK && data!=null){
            imageUri= CropImage.getActivityResult(data).uri
            avatar_inchangeAvatar.setImageURI(imageUri)
        }
    }
    private fun getFollowinglist(){
        val ref = FirebaseDatabase.getInstance().reference.child("Friends")
            .child(firebaseUser.uid!!)
            .child("friendList")
        ref.addValueEventListener(object: ValueEventListener {
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
}