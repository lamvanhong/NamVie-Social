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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_create.*
import kotlinx.android.synthetic.main.activity_group_edit.*
import java.text.SimpleDateFormat
import java.util.*

class GroupEditActivity : AppCompatActivity() {

    private var groupID:String?=null

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
        setContentView(R.layout.activity_group_edit)

        setSupportActionBar(toolbar_edit_group)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        groupID = intent.getStringExtra("groupID")



        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        loadGroupInfo()

        groupIconIv_edit.setOnClickListener{
            showImagePickDialog()
        }

        updateGroupBtn.setOnClickListener {
            startUpdateGroup()
        }
    }

    private fun loadGroupInfo() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .orderByChild("groupID").equalTo(groupID)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        val groupID = "" + ss.child("groupID").value
                        val groupTitle = "" + ss.child("groupTitle").value
                        val groupDescription = "" + ss.child("groupDescription").value
                        val groupIcon = "" + ss.child("groupIcon").value
                        val timestamp = "" + ss.child("timestamp").value
                        val createBy = "" + ss.child("createBy").value


                        var dateFormat= SimpleDateFormat("dd/mm/yyyy hh:mm a")
                        val date = Date(timestamp.toLong())
                        val dateTime = dateFormat.format(date)

                        groupTitleEt_edit.setText(groupTitle)
                        groupDescriptionEt_edit.setText(groupDescription)

                        try {
                            Picasso.get().load(groupIcon).into(groupIconIv)
                        }
                        catch (e:Exception) {
                            groupIconIv_edit.setImageResource(R.drawable.sontung)
                        }
                    }

                }

            })
    }


    private fun startUpdateGroup() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setMessage("Updating Group Info..")
        progressDialog.show()


        val groupTitle = groupTitleEt_edit.text.toString().trim()
        val groupDescription = groupDescriptionEt_edit.text.toString().trim()


        if (TextUtils.isEmpty(groupTitle)) {
            Toast.makeText(this,"Group title is required",Toast.LENGTH_SHORT).show()
            return
        }


        if (imageUri == null) {
            val hashMap = hashMapOf<String,Any?>()

            hashMap["groupTitle"] = groupTitle
            hashMap["groupDescription"] = groupDescription

            FirebaseDatabase.getInstance().reference.child("groups")
                .child(groupID.toString())
                .updateChildren(hashMap)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this,"Group info Updated",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,""+ it.message,Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        }
        else {

            val timestamp = "" + System.currentTimeMillis()
            val fileNameAndPath = "Group_Imgs/" + "image" + timestamp

            val storageReference = FirebaseStorage.getInstance().reference.child(fileNameAndPath)
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    val p_uriTask = it.storage.downloadUrl
                    while (!p_uriTask.isSuccessful) {}

                    val p_downloadUri = p_uriTask.result

                    if (p_uriTask.isSuccessful) {
                        val hashMap = hashMapOf<String,Any?>()

                        hashMap["groupTitle"] = groupTitle
                        hashMap["groupDescription"] = groupDescription
                        hashMap["groupIcon"] = "" + p_downloadUri

                        FirebaseDatabase.getInstance().reference.child("groups")
                            .child(groupID.toString())
                            .updateChildren(hashMap)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(this,"Group info Updated",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,""+ it.message,Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            }
                    }
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(this,""+it.message,Toast.LENGTH_LONG).show()
                }
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
                        Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show()
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
                groupIconIv_edit.setImageURI(imageUri)
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                groupIconIv_edit.setImageURI(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}