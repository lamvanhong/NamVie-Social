package com.lamhong.viesocial

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.devlomi.record_view.OnBasketAnimationEnd
import com.devlomi.record_view.OnRecordListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.viesocial.Adapter.MessageAdapter
import com.lamhong.viesocial.Listeners.UsersListener
import com.lamhong.viesocial.Models.Message
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTextView
import kotlinx.android.synthetic.main.activity_chat_log.*
import petrov.kristiyan.colorpicker.ColorPicker
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ChatLogActivity : AppCompatActivity() {

    private var senderRoom:String ?= null
    private var receiveRoom:String ?= null

    var seenListener: ValueEventListener? = null
    var userRefForSeen: DatabaseReference? = null

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200

    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400
    private val RECORDING_REQUEST_CODE = 1

    var cameraPermissions:Array<String> = emptyArray()
    var storagePermissions:Array<String> = emptyArray()

    var recordingPermission:Array<String> = emptyArray()

    var imageUri:Uri? = null

    var senderUid: String? = FirebaseAuth.getInstance().uid
    var receiverUid: String? = null
    val receiInfor : User = User()
    val senderInfor :User = User()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        recordingPermission = arrayOf(android.Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)


        var name:String? = intent.getStringExtra("name")
        var image:String? = intent.getStringExtra("image")
        receiverUid= intent.getStringExtra("uid")

        setSupportActionBar(toolbar_chatlog)

        user_name_chat.text = name
        Picasso.get().load(image).into(profile_image_chat)

        //supportActionBar?.title = name
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val userRef = FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(receiverUid!!)
        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    receiInfor.setAvatar(snapshot.child("avatar").value.toString())
                    receiInfor.setName(snapshot.child("fullname").value.toString())
                    receiInfor.setEmail(snapshot.child("email").value.toString())
                    receiInfor.setUid(snapshot.child("uid").value.toString())
                    receiInfor.setToken(snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        val userRef_2 = FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(senderUid!!)
        userRef_2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    senderInfor.setAvatar(snapshot.child("avatar").value.toString())
                    senderInfor.setName(snapshot.child("fullname").value.toString())
                    senderInfor.setEmail(snapshot.child("email").value.toString())
                    senderInfor.setUid(snapshot.child("uid").value.toString())
                    senderInfor.setToken(snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


        senderRoom = senderUid + receiverUid
        receiveRoom = receiverUid + senderUid

        val messages = ArrayList<Message>()

        val adater:MessageAdapter = MessageAdapter(messages, senderRoom!!, receiveRoom!!)

        val linearLayout = LinearLayoutManager(this)
        linearLayout.stackFromEnd = true
        rv_chat_log.setHasFixedSize(true)
        rv_chat_log.layoutManager = linearLayout


        //rv_chat_log.adapter = adater


        FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(senderRoom.toString())
            .child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for(ss in snapshot.children) {
                        val message = ss.getValue(Message::class.java)
                        message!!.setisSeen(ss.child("seen").value as Boolean)
                        if (message != null) {
                            messages.add(message)
                        }
                    }

                    adater.notifyDataSetChanged()
                    rv_chat_log.adapter = adater
                    //rv_chat_log.smoothScrollToPosition(adater.itemCount)
                }
            })

        messagebox.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(messagebox.text.toString())) {
                    btn_send_message.visibility = View.GONE
                    attachment.visibility = View.VISIBLE
                    camera.visibility = View.VISIBLE
                    //btn_record.visibility = View.VISIBLE
                }
                else {
                    btn_send_message.visibility = View.VISIBLE
                    attachment.visibility = View.GONE
                    camera.visibility = View.GONE
                    //btn_record.visibility = View.GONE

                }
            }

        })

        // emoji

        val popup = EmojiPopup.Builder.fromRootView(
                findViewById(R.id.root_view)
        ).build(messagebox)

        btn_icon_chat.setOnClickListener{
            popup.toggle()
        }



        btn_send_message.setOnClickListener {

            val emojiTextVie = LayoutInflater.from(this).inflate(R.layout.emoji_text_view,linearLayoutEmoji,false) as EmojiTextView

            emojiTextVie.setText(messagebox.text.toString())

            linearLayoutEmoji.addView(emojiTextVie)



            val messageTxt: String = messagebox.text.toString()

            if (!TextUtils.isEmpty(messageTxt)) {


                val date = Date()


                val message: com.lamhong.viesocial.Models.Message = com.lamhong.viesocial.Models.Message(messageTxt,
                        senderUid.toString(), date.time.toString(), false,"text")

                messagebox.text.clear()

                val lastMess = hashMapOf<String, Any?>()

                lastMess.put("lastMess", message.getMessage())
                lastMess.put("lastTime", date.time.toString())

                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom.toString())
                        .child("message")
                        .push()
                        .setValue(message).addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(receiveRoom.toString())
                                    .child("message")
                                    .push()
                                    .setValue(message).addOnSuccessListener {

                                    }

                            val lastMess = hashMapOf<String, Any?>()

                            lastMess.put("lastMess", message.getMessage())
                            lastMess.put("lastTime", date.time.toString())

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                        }
            }
        }

//        attachment.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent,25)
//        }


        attachment.setOnClickListener{
            if (!checkStoragePermission()) {
                requestStoragePermission()
            }
            else {
                pickFromGallery()
            }
        }

        camera.setOnClickListener{
            if (!checkCameraPermission()) {
                requestCameraPermission()
            }
            else {
                pickFromCamera()
            }
            //pickFromCamera()
        }

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,recordingPermission,RECORDING_REQUEST_CODE);
//        } else {
//            StartRecording()
//        }



        //Seen message

        userRefForSeen = FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(receiveRoom.toString())
                .child("message")

        seenListener = userRefForSeen!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ss in snapshot.children) {
                    val chat = ss.getValue(Message::class.java)

                    if (chat!=null) {
                        val seen = hashMapOf<String, Any?>()
                        seen.put("seen",true)
                        ss.ref.updateChildren(seen)
                    }

                }
            }

        })


/*        FirebaseDatabase.getInstance().reference.child("presence").child(receiveRoom.toString()).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status != null) {
                        if (!status.isEmpty()) {
                            status_chat.text = status
                            status_chat.visibility = View.VISIBLE

                        }
                    }
                }
            }

        })*/


        loadColor(senderRoom.toString())


    }

    private fun loadColor(senderRoom:String) {
        FirebaseDatabase.getInstance().reference.child("chats")
            .child(senderRoom)
            .child("color")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!=null) {
                        if (snapshot.value.toString() == "#eb3a2a" || snapshot.value.toString() == "#EB3A2A") {
                            camera.setColorFilter(resources.getColor(R.color.eb3a2a))
                            attachment.setColorFilter(resources.getColor(R.color.eb3a2a))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.eb3a2a))
                            btn_send_message.setColorFilter(resources.getColor(R.color.eb3a2a))
                        }
                        else if (snapshot.value.toString() == "#a598eb" || snapshot.value.toString() == "#A598EB") {
                            camera.setColorFilter(resources.getColor(R.color.a598eb))
                            attachment.setColorFilter(resources.getColor(R.color.a598eb))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.a598eb))
                            btn_send_message.setColorFilter(resources.getColor(R.color.a598eb))
                        }
                        else if (snapshot.value.toString() == "#e84fcf" || snapshot.value.toString() == "#E84FCF") {
                            camera.setColorFilter(resources.getColor(R.color.e84fcf))
                            attachment.setColorFilter(resources.getColor(R.color.e84fcf))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.e84fcf))
                            btn_send_message.setColorFilter(resources.getColor(R.color.e84fcf))
                        }
                        else if (snapshot.value.toString() == "#0e92eb" || snapshot.value.toString() == "#0E92EB") {
                            camera.setColorFilter(resources.getColor(R.color.a0e92eb))
                            attachment.setColorFilter(resources.getColor(R.color.a0e92eb))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.a0e92eb))
                            btn_send_message.setColorFilter(resources.getColor(R.color.a0e92eb))
                        }
                        else if (snapshot.value.toString() == "#b53f3f" || snapshot.value.toString() == "#B53F3F") {
                            camera.setColorFilter(resources.getColor(R.color.b53f3f))
                            attachment.setColorFilter(resources.getColor(R.color.b53f3f))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.b53f3f))
                            btn_send_message.setColorFilter(resources.getColor(R.color.b53f3f))
                        }
                        else if (snapshot.value.toString() == "#de625b" || snapshot.value.toString() == "#DE625B") {
                            camera.setColorFilter(resources.getColor(R.color.de625b))
                            attachment.setColorFilter(resources.getColor(R.color.de625b))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.de625b))
                            btn_send_message.setColorFilter(resources.getColor(R.color.de625b))
                        }
                        else if (snapshot.value.toString() == "#e6a50e" || snapshot.value.toString() == "#E6A50E") {
                            camera.setColorFilter(resources.getColor(R.color.e6a50e))
                            attachment.setColorFilter(resources.getColor(R.color.e6a50e))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.e6a50e))
                            btn_send_message.setColorFilter(resources.getColor(R.color.e6a50e))
                        }
                        else if (snapshot.value.toString() == "#69c90c" || snapshot.value.toString() == "#69C90C") {
                            camera.setColorFilter(resources.getColor(R.color.a69c90c))
                            attachment.setColorFilter(resources.getColor(R.color.a69c90c))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.a69c90c))
                            btn_send_message.setColorFilter(resources.getColor(R.color.a69c90c))
                        }
                        else if (snapshot.value.toString() == "#4e42ad" || snapshot.value.toString() == "#4E42AD") {
                            camera.setColorFilter(resources.getColor(R.color.a4e42ad))
                            attachment.setColorFilter(resources.getColor(R.color.a4e42ad))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.a4e42ad))
                            btn_send_message.setColorFilter(resources.getColor(R.color.a4e42ad))
                        }
                        else if (snapshot.value.toString() == "#a80ddd" || snapshot.value.toString() == "#A80DDD") {
                            camera.setColorFilter(resources.getColor(R.color.a80ddd))
                            attachment.setColorFilter(resources.getColor(R.color.a80ddd))
                            btn_icon_chat.setColorFilter(resources.getColor(R.color.a80ddd))
                            btn_send_message.setColorFilter(resources.getColor(R.color.a80ddd))
                        }
                    }

                }

            })
    }

    private fun StartRecording() {
        btn_record.setRecordView(record_view)
        btn_record.isListenForRecord = false

        btn_record.setOnClickListener{

            Log.d("Record","true")

            if (!checkRecordingPermission()) {
                requestRecording()
            }
            else {
                btn_record.isListenForRecord = true
            }

            //btn_record.isListenForRecord =  true
        }

        record_view.setOnRecordListener(object : OnRecordListener {

            var mediaRecorder = MediaRecorder()

            var file = File(Environment.getExternalStorageDirectory().absolutePath,"MyBook/Media/Recording")

            var audioPath = file.absolutePath + File.separator + System.currentTimeMillis() + ".3gp"

            //val audioPath = Environment.getExternalStorageDirectory().absolutePath + "/AudioRecording.3gp"

            //val path_save = Environment.getExternalStorageDirectory().absolutePath + "/" + UUID.randomUUID().toString() + "audio_record.3gp"

            override fun onFinish(recordTime: Long) {


                //var dateFormat = SimpleDateFormat("hh:mm:ss")

                Log.d("RecordView", "onFinish");

                //Log.d("RecordTime", dateFormat.format(recordTime));


                mediaRecorder.stop()

                mediaRecorder.release()


                record_view.visibility = View.GONE
                messagebox.visibility = View.VISIBLE
                attachment.visibility = View.VISIBLE
                camera.visibility = View.VISIBLE

                sendRecordingMess(audioPath)
            }

            override fun onLessThanSecond() {
                Log.d("RecordView", "onLessThanSecond")

                mediaRecorder.reset()

                mediaRecorder.release()

                file = File(audioPath)

                if (file.exists())
                    file.delete()

                record_view.visibility = View.GONE
                messagebox.visibility = View.VISIBLE
                attachment.visibility = View.VISIBLE
                camera.visibility = View.VISIBLE
            }

            override fun onCancel() {
                Log.d("RecordView", "onCancel")

                mediaRecorder.reset()

                mediaRecorder.release()

                file = File(audioPath)

                if (file.exists())
                    file.delete()

                record_view.visibility = View.GONE
                messagebox.visibility = View.VISIBLE
                attachment.visibility = View.VISIBLE
                camera.visibility = View.VISIBLE


            }

            override fun onStart() {

                Log.d("RecordView", "onStart")


                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)

                if (!file.exists()) {
                    file.mkdir()
                }


                mediaRecorder.setOutputFile(audioPath)

                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }



                messagebox.visibility = View.GONE
                attachment.visibility = View.GONE
                camera.visibility = View.GONE
                record_view.visibility = View.VISIBLE


            }

        })

        record_view.setOnBasketAnimationEndListener(object : OnBasketAnimationEnd {
            override fun onAnimationEnd() {
                record_view.visibility = View.GONE
                messagebox.visibility = View.VISIBLE
                attachment.visibility = View.VISIBLE
                camera.visibility = View.VISIBLE
            }

        })


    }



    private fun sendRecordingMess(audioPath:String) {
        val timestamp = "" + System.currentTimeMillis()
        val fileNameAndPath = "ChatRecording/" + "mess_" + timestamp


        val storagereference = FirebaseStorage.getInstance().reference.child(fileNameAndPath)

        val audioFile = Uri.fromFile(File(audioPath))

        storagereference.putFile(audioFile).addOnSuccessListener {
            val audioUrl = it.storage.downloadUrl
            while (!audioUrl.isSuccessful){}


            val dowloadUri = audioUrl.result.toString()

            if (audioUrl.isSuccessful) {

                val message: com.lamhong.viesocial.Models.Message = com.lamhong.viesocial.Models.Message(dowloadUri,
                    senderUid.toString(), timestamp, false,"recording")


                val lastMess = hashMapOf<String, Any?>()

                lastMess["lastMess"] = "[Recording]"
                lastMess["lastTime"] = timestamp

                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)


                FirebaseDatabase.getInstance().reference
                    .child("chats")
                    .child(senderRoom.toString())
                    .child("message")
                    .push()
                    .setValue(message).addOnSuccessListener {
                        FirebaseDatabase.getInstance().reference
                            .child("chats")
                            .child(receiveRoom.toString())
                            .child("message")
                            .push()
                            .setValue(message).addOnSuccessListener {

                            }

                        val lastMess = hashMapOf<String, Any?>()

                        lastMess["lastMess"] = "[Recording]"
                        lastMess["lastTime"] = timestamp


                        FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                        FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                        //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                    }



            }
        }
    }



    private fun checkRecordingPermission() : Boolean {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecording() {
        ActivityCompat.requestPermissions(this,recordingPermission,RECORDING_REQUEST_CODE)
    }


    private fun checkStoragePermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
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
                        Toast.makeText(this,"Truy cập bị từ chối",Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this,"Truy cập bị từ chối",Toast.LENGTH_LONG).show()
                    }
                }
                else {

                }
            }
            RECORDING_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val recordingAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val toStoreAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (recordingAccepted && toStoreAccepted) {
                        StartRecording()
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this,"Truy cập bị từ chối",Toast.LENGTH_LONG).show()
                    }
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

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Đang gửi hình ảnh..")
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()
        val fileNameAndPath = "ChatImages/mess_$timestamp"


        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos)
        val data = baos.toByteArray()
        val ref = FirebaseStorage.getInstance().reference.child(fileNameAndPath)
        ref.putBytes(data)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val uriTask = it.storage.downloadUrl
                    while (!uriTask.isSuccessful){}
                    val dowloadUri = uriTask.result.toString()

                    if (uriTask.isSuccessful) {

                        val message: com.lamhong.viesocial.Models.Message = com.lamhong.viesocial.Models.Message(dowloadUri,
                                senderUid.toString(), timestamp, false,"image")


                        val lastMess = hashMapOf<String, Any?>()

                        lastMess["lastMess"] = "[Hình ảnh]"
                        lastMess["lastTime"] = timestamp

                        FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                        FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)


                        FirebaseDatabase.getInstance().reference
                                .child("chats")
                                .child(senderRoom.toString())
                                .child("message")
                                .push()
                                .setValue(message).addOnSuccessListener {
                                    FirebaseDatabase.getInstance().reference
                                            .child("chats")
                                            .child(receiveRoom.toString())
                                            .child("message")
                                            .push()
                                            .setValue(message).addOnSuccessListener {

                                            }

                                    val lastMess = hashMapOf<String, Any?>()

                                    lastMess["lastMess"] = "[Hình ảnh]"
                                    lastMess["lastTime"] = timestamp


                                    FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                                    FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                                    //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                                }




                    }
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                }


    }


/*    override fun onResume() {
        super.onResume()
        val currentuid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("presence").child(currentuid.toString()).setValue("Online")

    }

    override fun onStop() {

        val currentuid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("presence").child(currentuid.toString()).setValue("Offline")
        super.onStop()
    }*/

    override fun onPause() {
        super.onPause()

        seenListener?.let { userRefForSeen?.removeEventListener(it) }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        var senderUid: String? = FirebaseAuth.getInstance().uid
//
//        val dialog = ProgressDialog(this)
//        dialog.setMessage("Uploading image...")
//        dialog.setCancelable(false)
//
//        if (requestCode == 25) {
//            if (data!=null) {
//                if (data.data != null) {
//                    val selectedImage = data.data
//                    val calendar = Calendar.getInstance()
//                    val reference = FirebaseStorage.getInstance().reference.child("chats").child(calendar.timeInMillis.toString() + "")
//                    dialog.show()
//                    reference.putFile(selectedImage as Uri).addOnCompleteListener {
//                        dialog.dismiss()
//                        if (it.isSuccessful) {
//                            reference.downloadUrl.addOnSuccessListener {
//                                val filePath = it.toString()
//
//                                val date = Date()
//                                val messageTxt:String = messagebox.text.toString()
//
//                                val message:com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(messageTxt,
//                                        senderUid.toString(),date.time.toString(),false,"")
//
//                                message.setMessage("[Photo]")
//
//                                message.setImageUrl(filePath)
//
//                                messagebox.text.clear()
//
//                                val lastMess = hashMapOf<String, Any?>()
//
//                                lastMess.put("lastMess",message.getMessage())
//                                lastMess.put("lastTime",date.time)
//
//                                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
//                                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
//
//                                FirebaseDatabase.getInstance().reference
//                                        .child("chats")
//                                        .child(senderRoom.toString())
//                                        .child("message")
//                                        .push()
//                                        .setValue(message).addOnSuccessListener {
//                                            FirebaseDatabase.getInstance().reference
//                                                    .child("chats")
//                                                    .child(receiveRoom.toString())
//                                                    .child("message")
//                                                    .push()
//                                                    .setValue(message).addOnSuccessListener {
//
//                                                    }
//
//                                            val lastMess = hashMapOf<String, Any?>()
//
//                                            lastMess.put("lastMess",message.getMessage())
//                                            lastMess.put("lastTime",date.time)
//
//                                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
//                                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
//
//                                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)
//
//                                        }
//
//                                //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//
//                }
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu_chat,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.chat_color -> {
                openColorPicker()
            }
            R.id.chat_call -> {makeAudioCall()}
            R.id.chat_videocall -> {makeVideoCall()}
            R.id.chat_see_profile -> {}
            R.id.chat_nickname -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openColorPicker() {
        val colorPicker = ColorPicker(this)
        val colors = arrayListOf<String>()

        colors.add("#eb3a2a")
        colors.add("#a598eb")
        colors.add("#e84fcf")
        colors.add("#0e92eb")
        colors.add("#b53f3f")
        colors.add("#de625b")
        colors.add("#e6a50e")
        colors.add("#69c90c")
        colors.add("#4e42ad")
        colors.add("#a80ddd")



        colorPicker.setColors(colors)
            .setColumns(5)
            .setRoundColorButton(true)
            .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener{
                override fun onChooseColor(position: Int, color: Int) {

                    val hashMap = hashMapOf<String, Any?>()


                    val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and color)

                    hashMap["color"] = hexColor



                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom.toString())
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference
                                .child("chats")
                                .child(receiveRoom.toString())
                                .updateChildren(hashMap)
                                .addOnSuccessListener {

                                }
                        }
                }

                override fun onCancel() {
                }

            })
            .show()


    }

    private fun makeVideoCall() {

        //usersListener?.initiateVideoMetting();

        var intent  = Intent(applicationContext, OutgoingInvitationActivity::class.java)
        intent.putExtra("senderInfor", senderInfor)
        intent.putExtra("receiInfor",receiInfor)
        intent.putExtra("type", "video")
        startActivity(intent)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private val usersListener : UsersListener?= null

    private fun makeAudioCall(){
        var intent  = Intent(applicationContext, OutgoingInvitationActivity::class.java)
        intent.putExtra("senderInfor", senderInfor)
        intent.putExtra("receiInfor",receiInfor)
        intent.putExtra("type", "audio")
        startActivity(intent)
    }

}