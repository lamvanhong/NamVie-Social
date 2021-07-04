package com.lamhong.viesocial

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.viesocial.Adapter.MessageAdapter
import com.lamhong.viesocial.Models.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*
import kotlin.collections.ArrayList


class ChatLogActivity : AppCompatActivity() {

    private var senderRoom:String ?= null
    private var receiveRoom:String ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)



        val messages = ArrayList<Message>()

        val adater:MessageAdapter = MessageAdapter(messages)

        rv_chat_log.layoutManager = LinearLayoutManager(this)
        rv_chat_log.adapter = adater

        var name:String? = intent.getStringExtra("name")
        var image:String? = intent.getStringExtra("image")
        var receiverUid:String?= intent.getStringExtra("uid")



        setSupportActionBar(toolbar_chatlog)

        user_name_chat.text = name
        Picasso.get().load(image).into(profile_image_chat)

        //supportActionBar?.title = name
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var senderUid: String? = FirebaseAuth.getInstance().uid

        senderRoom = senderUid + receiverUid
        receiveRoom = receiverUid + senderUid

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
                        if (message != null) {
                            messages.add(message)
                        }
                    }

                    adater.notifyDataSetChanged()
                    rv_chat_log.smoothScrollToPosition(adater.itemCount)
                }
            })



        btn_send_message.setOnClickListener {

            val date = Date()
            val messageTxt:String = messagebox.text.toString()

            val message:com.lamhong.viesocial.Models.Message = com.lamhong.viesocial.Models.Message(messageTxt,
                senderUid.toString(),date.time)

            messagebox.text.clear()

            val lastMess = hashMapOf<String, Any?>()

            lastMess.put("lastMess",message.getMessage())
            lastMess.put("lastTime",date.time)

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

                        lastMess.put("lastMess",message.getMessage())
                        lastMess.put("lastTime",date.time)

                        FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                        FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                        //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                }
        }

        attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var senderUid: String? = FirebaseAuth.getInstance().uid

        val dialog = ProgressDialog(this)
        dialog.setMessage("Uploading image...")
        dialog.setCancelable(false)

        if (requestCode == 25) {
            if (data!=null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = FirebaseStorage.getInstance().reference.child("chats").child(calendar.timeInMillis.toString() + "")
                    dialog.show()
                    reference.putFile(selectedImage as Uri).addOnCompleteListener {
                        dialog.dismiss()
                        if (it.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                val filePath = it.toString()

                                val date = Date()
                                val messageTxt:String = messagebox.text.toString()

                                val message:com.lamhong.viesocial.Models.Message = com.lamhong.viesocial.Models.Message(messageTxt,
                                        senderUid.toString(),date.time)

                                message.setMessage("photo")

                                message.setImageUrl(filePath)

                                messagebox.text.clear()

                                val lastMess = hashMapOf<String, Any?>()

                                lastMess.put("lastMess",message.getMessage())
                                lastMess.put("lastTime",date.time)

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

                                            lastMess.put("lastMess",message.getMessage())
                                            lastMess.put("lastTime",date.time)

                                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                                        }

                                //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu_chat,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.chat_color -> {}
            R.id.chat_call -> {}
            R.id.chat_videocall -> {}
            R.id.chat_see_profile -> {}
            R.id.chat_nickname -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}