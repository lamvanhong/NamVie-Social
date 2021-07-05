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
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.viesocial.Adapter.GroupMessageAdapter
import com.lamhong.viesocial.Models.GroupMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_chats_log.*
import java.util.*

class GroupChatsLogActivity : AppCompatActivity() {


    var groupID:String? = null

    var myGroupRole:String? = null

    val senderID = FirebaseAuth.getInstance().uid

    val groupMessageList = ArrayList<GroupMessage>()

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200

    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400

    var cameraPermissions:Array<String> = emptyArray()
    var storagePermissions:Array<String> = emptyArray()

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chats_log)
        

        setSupportActionBar(toolbar_chat_log_group)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val intent = intent
        groupID = intent.getStringExtra("groupID")

        loadGroupInfo()

        loadGroupMessage()

        loadMyGroupRole()

        messagebox_group.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(messagebox_group.text.toString())) {
                    btn_send_message_group.visibility = View.GONE
                    attachment_group.visibility = View.VISIBLE
                    camera_group.visibility = View.VISIBLE
                    //btn_record.visibility = View.VISIBLE
                }
                else {
                    btn_send_message_group.visibility = View.VISIBLE
                    attachment_group.visibility = View.GONE
                    camera_group.visibility = View.GONE
                    //btn_record.visibility = View.GONE

                }
            }

        })

        btn_send_message_group.setOnClickListener {
            val message = messagebox_group.text.toString().trim()

            if (TextUtils.isEmpty(message)) {

            }
            else {
                sendMessage(message)
            }
        }

        attachment_group.setOnClickListener{
            if (!checkStoragePermission()) {
                requestStoragePermission()
            }
            else {
                pickFromGallery()
            }
        }

        camera_group.setOnClickListener{
            if (!checkCameraPermission()) {
                requestCameraPermission()
            }
            else {
                pickFromCamera()
            }
            //pickFromCamera()
        }
    }

    private fun loadMyGroupRole() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("members")
            .orderByChild("uid")
            .equalTo(senderID)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        myGroupRole = "" + ss.child("role").value
                        invalidateOptionsMenu()
                    }
                }

            })
    }

    private fun loadGroupMessage() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    groupMessageList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(GroupMessage::class.java)
                        if (model != null) {
                            groupMessageList.add(model)
                        }
                    }

                    val adapter = GroupMessageAdapter(groupMessageList)

                    val linearLayout = LinearLayoutManager(this@GroupChatsLogActivity)
                    linearLayout.stackFromEnd = true
                    rv_chat_log_group.layoutManager = linearLayout
                    rv_chat_log_group.adapter = adapter
                    adapter.notifyDataSetChanged()


                }

            })

    }

    private fun sendMessage(message: String) {

        val timestamp = "" + System.currentTimeMillis()

        val groupchats = GroupMessage(message,senderID.toString(),timestamp,"text")

        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("messages")
            .child(timestamp)
            .setValue(groupchats)
            .addOnSuccessListener {
                messagebox_group.text.clear()




            }
            .addOnFailureListener {
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun loadGroupInfo() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .orderByChild("groupID").equalTo(groupID)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val groupTitle = "" + ds.child("groupTitle").value
                        val groupDescription = "" + ds.child("groupDescription").value
                        val groupIcon = "" + ds.child("groupIcon").value
                        val timestamp = "" + ds.child("timestamp").value
                        val createBy = "" + ds.child("createBy").value

                        title_chat_group.text = groupTitle
                        try {
                            Picasso.get().load(groupIcon).into(image_chat_group)
                        }
                        catch (e:Exception) {
                            image_chat_group.setImageResource(R.drawable.sontung)
                        }



                    }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.top_menu_group_chatlog,menu)

        menu?.findItem(R.id.group_add_mem)?.isVisible =
            (myGroupRole == "admin" || myGroupRole == "creator")

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.group_add_mem -> {
                val intent = Intent(this,GroupAddMemberActivity::class.java)
                intent.putExtra("groupID",groupID)
                startActivity(intent)
            }
            R.id.group_info -> {
                val intent = Intent(this,GroupInfoActivity::class.java)
                intent.putExtra("groupID",groupID)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
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

    private fun pickFromCamera() {
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick")
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE)

    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE)

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
                sendImageMess(imageUri)
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImageMess(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendImageMess(imageUri: Uri?) {
        val pd = ProgressDialog(this)
        pd.setTitle("Please wait")
        pd.setMessage("Sending Image...")
        pd.setCanceledOnTouchOutside(false)
        pd.show()

        val timestamp = "" + System.currentTimeMillis()
        val fileNameAndPath = "ChatImages/mess_$timestamp"

        if (imageUri != null) {
            FirebaseStorage.getInstance().reference.child(fileNameAndPath)
                .putFile(imageUri)
                .addOnSuccessListener {
                    pd.dismiss()
                    val uriTask = it.storage.downloadUrl
                    while (!uriTask.isSuccessful){}
                    val dowloadUri = uriTask.result.toString()

                    if (uriTask.isSuccessful) {
                        val groupchats = GroupMessage(dowloadUri,senderID.toString(),timestamp,"image")

                        FirebaseDatabase.getInstance().reference.child("groups")
                            .child(groupID.toString())
                            .child("messages")
                            .child(timestamp)
                            .setValue(groupchats)
                            .addOnSuccessListener {
                                messagebox_group.text.clear()
                                pd.dismiss()

                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()
                                pd.dismiss()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,""+it.message,Toast.LENGTH_SHORT).show()
                    pd.dismiss()
                }
        }
    }
}