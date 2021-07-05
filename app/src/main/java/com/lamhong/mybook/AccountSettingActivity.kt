package com.lamhong.mybook

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.storage.StorageManager
import android.text.TextUtils
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
import com.lamhong.mybook.Models.User
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    lateinit var firebaseUser :FirebaseUser
    private var myUrl=""
    private var imageUir : Uri?=null
    private var checker=""
    private var storageAvatarRef : StorageReference ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

            finish()
            startActivity(intent)
        }
        firebaseUser=FirebaseAuth.getInstance().currentUser
        storageAvatarRef =FirebaseStorage.getInstance().reference.child("Avatar")

        userInfor()
        btn_save.setOnClickListener {
            if(checker=="includeImage"){
                updateAvatarInfor()
            }
            else{
                updateUserInfor()
            }
        }

        btn_change_avatar.setOnClickListener {
            checker="includeImage"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this)

        }


    }

    private fun updateAvatarInfor() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Account update")
        progressDialog.setMessage("Please wait, updating...")
        progressDialog.show()
        when{
            TextUtils.isEmpty(edit_name.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập tên ",Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(edit_mota.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập mota ",Toast.LENGTH_LONG).show()
            }
            imageUir ==null -> Toast.makeText(this, "Vui lòng chọn ảnh" , Toast.LENGTH_LONG).show()
            else ->{
                val file = storageAvatarRef?.child(firebaseUser.uid +".jpg")

                var uploadTask : StorageTask<*>
                uploadTask= file!!.putFile(imageUir!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{
                    task ->
                    if (!task.isSuccessful){
                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation file.downloadUrl

                }).addOnCompleteListener(OnCompleteListener<Uri>{task ->
                    if(task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl=downloadUrl.toString()

                        val ref=FirebaseDatabase.getInstance().reference.child("UserInformation")

//                        val userMap = HashMap<String, Any>()
//                        userMap["fullname"]= edit_name.text.toString()
//                        userMap["email"]= my  Url
//
//                        ref.child(firebaseUser.uid).updateChildren(userMap)
                        val inMap = HashMap<String, Any>()
                        inMap["fullname"]= edit_name.text.toString()
                        inMap["avatar"]= myUrl


                        ref.child(firebaseUser.uid).updateChildren(inMap)
                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode== Activity.RESULT_OK && data!=null){
            val result =CropImage.getActivityResult(data)
            imageUir=result.uri
            image_avatar.setImageURI(imageUir)
        }

    }
    private fun updateUserInfor() {
        // check data
        when {
            TextUtils.isEmpty(edit_name.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập tên ",Toast.LENGTH_LONG)
            }
            TextUtils.isEmpty(edit_mota.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập mota ",Toast.LENGTH_LONG)
            }
            else -> {
                val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation")


                val userMap = HashMap<String, Any>()
                userMap["fullname"]= edit_name.text.toString()
                userMap["email"]= edit_mota.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)


                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                Toast.makeText(this, "Đã cập nhật thông tin !!", Toast.LENGTH_LONG)
                startActivity(intent)
                finish()

            }
        }



    }

    private fun userInfor(){
        val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation").child(firebaseUser.uid)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user= snapshot.getValue(User::class.java)
                    user?.setName(snapshot.child("fullname").value.toString())
                    edit_name.setText(user?.getName())
                    edit_mota.setText(user?.getEmail())
                    Picasso.get().load(user?.getAvatar()).placeholder(R.drawable.duongtu).into(image_avatar)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}
