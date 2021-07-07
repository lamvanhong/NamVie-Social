package com.lamhong.viesocial

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.viesocial.Models.GroupChat
import kotlinx.android.synthetic.main.activity_group_create.*

class GroupCreateActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200

    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400

    private var cameraPermissions:Array<String> = emptyArray()
    private var storagePermissions:Array<String> = emptyArray()

    private var imageUri: Uri? = null



    private val currentUid = FirebaseAuth.getInstance().uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_create)

        setSupportActionBar(toolbar_create_group)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setDisplayShowHomeEnabled(true)

        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)



        groupIconIv.setOnClickListener{
            showImagePickDialog()
        }



        createGroupBtn.setOnClickListener{
            startCreatingGroup()
        }
    }

    private fun startCreatingGroup() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Creating Group")

        val groupTitle = groupTitleEt.text.toString().trim()
        val groupDescription = groupDescriptionEt.text.toString().trim()

        if (TextUtils.isEmpty(groupTitle)) {
            Toast.makeText(this,"Please Enter Group Title",Toast.LENGTH_LONG).show()
            return
        }

        progressDialog.show()

        val g_timestamp = "" + System.currentTimeMillis()

        if (imageUri == null) {
            createGroup(""+ g_timestamp,"" + groupTitle,"" + groupDescription,"",progressDialog)
        }
        else {
            val fileNameAndPath = "Group_Imgs/" + "image" + g_timestamp

            val storageReference = FirebaseStorage.getInstance().reference.child(fileNameAndPath)
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    val p_uriTask = it.storage.downloadUrl
                    while (!p_uriTask.isSuccessful) {}

                    val p_downloadUri = p_uriTask.result

                    if (p_uriTask.isSuccessful) {
                        createGroup(""+ g_timestamp,"" + groupTitle,"" + groupDescription,"" + p_downloadUri,progressDialog)
                    }
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(this,""+it.message,Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun createGroup(g_timestamp:String,groupTitle:String,groupDescription:String,groupIcon:String,progressDialog:ProgressDialog) {


        val groupChat = GroupChat(g_timestamp,groupTitle,groupDescription,groupIcon,g_timestamp,currentUid.toString())

        FirebaseDatabase.getInstance().reference.child("groups")
            .child(g_timestamp)
            .setValue(groupChat)
            .addOnSuccessListener {


                val hashMap = hashMapOf<String,String>()
                hashMap["uid"] = currentUid.toString()
                hashMap["role"] = "creator"
                hashMap["timestamp"] = g_timestamp

                FirebaseDatabase.getInstance().reference.child("groups")
                    .child(g_timestamp)
                    .child("members")
                    .child(currentUid.toString())
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Group created successfully",Toast.LENGTH_LONG).show()

                        finish()
//                        val intent = Intent(this,GroupChatsLogActivity::class.java)
//                        intent.putExtra("groupID",g_timestamp)
//                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(this,"" + it.message,Toast.LENGTH_LONG).show()
                    }


            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,""+it.message,Toast.LENGTH_LONG).show()
            }

    }

    private fun showImagePickDialog() {
        val options = arrayOf("Camera","Gallery")

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Pick Image:")
            .setItems(options) { dialog, which ->
                if (which==0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission()
                    }
                    else {
                        pickFromCamera()
                    }
                }
                else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission()
                    }
                    else {
                        pickFromGallery()
                    }

                }
            }.show()


    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.setType("image/*")

        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE)
    }

    private fun pickFromCamera() {
        val cv = ContentValues()

        cv.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title")
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE)

    }

    private fun checkStoragePermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result && result1
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
                    }
                }
                else {

                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickFromGallery()
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
                    }
                }
                else {

                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data?.data
                groupIconIv.setImageURI(imageUri)
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                groupIconIv.setImageURI(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}