package com.lamhong.viesocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.MessageAdapter
import com.lamhong.viesocial.Models.Message
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
        var receiverUid:String?= intent.getStringExtra("uid")



        setSupportActionBar(toolbar_chatlog)
        supportActionBar?.title = name
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

                }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}