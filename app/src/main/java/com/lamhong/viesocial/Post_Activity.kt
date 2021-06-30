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
import com.google.firebase.database.FirebaseDatabase
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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