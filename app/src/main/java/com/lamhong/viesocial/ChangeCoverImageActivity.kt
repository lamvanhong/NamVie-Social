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
import kotlinx.android.synthetic.main.activity_change_cover_image.*
import kotlinx.android.synthetic.main.activity_profile_editting.*
import kotlinx.android.synthetic.main.activity_profile_editting.anhbia
import kotlinx.android.synthetic.main.activity_profile_editting.btn_return


class ChangeCoverImageActivity : AppCompatActivity() {

    lateinit var firebaseUser : FirebaseUser
    private var storageRef : StorageReference ?=null
    private var coverImageUri : Uri ?=null
    private var lstFriend : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_cover_image)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference.child("UserImage").child("CoverImage")
        getFollowList()

        // choosing change cover
        CropImage.activity().setAspectRatio(1920, 900).start(this)
        anhbia.setOnClickListener{
            CropImage.activity().setAspectRatio(1920, 900).start(this)
        }
        btn_return.setOnClickListener{
            this.finish()
        }
        btn_dangChangeCoverImage.setOnClickListener{
            createCoverImageChanging()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK
            && data!=null){
            coverImageUri  = CropImage.getActivityResult(data).uri
            anhbia.setImageURI(coverImageUri)
        }
    }
    private fun createCoverImageChanging() {
        if (coverImageUri == null){
            Toast.makeText(this, "Vui lòng chọn ảnh trước !!", Toast.LENGTH_SHORT).show()
            return
        }
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Tải ảnh lên")
        progressDialog.setMessage("Đang tải ảnh lên, chờ chút ...")
        progressDialog.show()


        val file = storageRef!!.child(firebaseUser.uid + System.currentTimeMillis() + ".jpg")

        val storageTask: StorageTask<*>
        storageTask = file!!.putFile(coverImageUri!!)
        storageTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->
            if(!task.isSuccessful){
                task.exception.let {
                    progressDialog.dismiss()
                    throw it!!
                }
            }else{
                return@Continuation file.downloadUrl
            }
        }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
            if(task.isSuccessful){
                val downloadurl = task.result.toString()
                val postCoverRef= FirebaseDatabase.getInstance().reference.child("Contents")
                    .child("CoverPost")

                val map = HashMap<String, Any>()
                val key = postCoverRef.push().key.toString()
                map["post_id"]= key
                map["post_image"]=downloadurl
                map["publisher"]= firebaseUser.uid!!
                map["post_content"]=edit_content_coverchanging.text.toString()
                postCoverRef.child(key).setValue(map)

                // set image to Firebase User
                FirebaseDatabase.getInstance().reference.child("UserDetails")
                    .child(firebaseUser.uid!!)
                    .child("coverImage")
                    .setValue(downloadurl)
                val timeLineUserRef= FirebaseDatabase.getInstance().reference.child("Contents")
                    .child("ProfileTimeLine").child(firebaseUser.uid!!)
                val tlMap = HashMap<String, Any>()
                tlMap["post_type"]= "changecover"
                tlMap["id"]=key
                tlMap["active"]=true
                timeLineUserRef.child(key).setValue(tlMap)

                //set post to timeLine follow list
                for (user in lstFriend){
                    val timeLineFollowUserRef= FirebaseDatabase.getInstance().reference
                        .child("Contents").child("UserTimeLine")
                        .child(user)
                    val follMap = HashMap<String, Any>()
                    follMap["post_type"]="changecover"
                    follMap["id"]=key
                    follMap["active"]=true
                    timeLineFollowUserRef.child(key).setValue(follMap)
                }
                Toast.makeText(this, "Đã cập nhật ảnh bìa thành công", Toast.LENGTH_SHORT).show()
                finish()
                progressDialog.dismiss()
            }
            else{
                progressDialog.dismiss()
                Toast.makeText(this, "Xuất hiện lỗi, vui lòng thử lại sau !!!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFollowList() {
        val friendRef= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(firebaseUser.uid!!).child("followerList")
        friendRef.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    lstFriend.clear()
                    for (s in snapshot.children){
                        lstFriend.add(s.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}