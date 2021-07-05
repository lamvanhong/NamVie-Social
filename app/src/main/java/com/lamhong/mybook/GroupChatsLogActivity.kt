package com.lamhong.mybook

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Adapter.GroupMessageAdapter
import com.lamhong.mybook.Models.GroupMessage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_chats_log.*
import java.util.*

class GroupChatsLogActivity : AppCompatActivity() {

    var groupID:String? = null

    val senderID = FirebaseAuth.getInstance().uid

    val groupMessageList = ArrayList<GroupMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chats_log)

        setSupportActionBar(toolbar_chat_log_group)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        groupID = intent.getStringExtra("groupID")

        loadGroupInfo()

        loadGroupMessage()

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
                            image_chat_group.setImageResource(R.drawable.ic_group)
                        }



                    }
                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}