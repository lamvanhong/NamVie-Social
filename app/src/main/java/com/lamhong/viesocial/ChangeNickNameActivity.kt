package com.lamhong.viesocial

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_change_nick_name.*
import kotlinx.android.synthetic.main.change_nickname_dialog.*

class ChangeNickNameActivity : AppCompatActivity() {

    var senderUid: String? = FirebaseAuth.getInstance().uid
    var receiverUid: String? = null
    private var senderRoom:String ?= null
    private var receiverRoom:String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_nick_name)

        setSupportActionBar(toolbar_message)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        receiverUid = intent.getStringExtra("receiverUid")
        senderRoom = intent.getStringExtra("senderRoom")
        receiverRoom = intent.getStringExtra("receiverRoom")

        Log.d("nickname",senderUid.toString())
        Log.d("nickname",receiverUid.toString())

        loadInfo()


        loadNickName()


        user1.setOnClickListener {
            OpenDialog(1)
        }

        user2.setOnClickListener {
            OpenDialog(2)
        }

    }

    private fun loadNickName() {
        FirebaseDatabase.getInstance().reference.child("chats")
            .child(senderRoom.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("sender_nickname").value != null)
                        name_group_add.text = snapshot.child("sender_nickname").value.toString()
                    if (snapshot.child("receiver_nickname").value != null)
                        name_group_add2.text = snapshot.child("receiver_nickname").value.toString()
                }

            })
    }

    private fun OpenDialog(tmp:Int) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.change_nickname_dialog)

        val window = dialog.window

        if (window==null) {
            return
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER

        window.attributes = windowAttributes

        dialog.setCancelable(true)

        val btn_cancel = dialog.cancel
        val btn_set = dialog.set

        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }

        btn_set.setOnClickListener {

            if (tmp == 1) {
                val hashMap1 = hashMapOf<String,Any>()
                val hashMap2 = hashMapOf<String,Any>()

                if (dialog.nickname.text.toString()=="") {
                    hashMap1["sender_nickname"] = statusTV.text
                    hashMap2["receiver_nickname"] = statusTV.text
                }

                else {
                    hashMap1["sender_nickname"] = dialog.nickname.text.toString()
                    hashMap2["receiver_nickname"] = dialog.nickname.text.toString()
                }



                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom.toString())
                    .updateChildren(hashMap1)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom.toString())
                            .updateChildren(hashMap2)
                            .addOnSuccessListener {

                            }
                    }
            }

            else if (tmp == 2) {
                val hashMap1 = hashMapOf<String,Any>()
                val hashMap2 = hashMapOf<String,Any>()

                if (dialog.nickname.text.toString()=="") {
                    hashMap1["sender_nickname"] = statusTV2.text
                    hashMap2["receiver_nickname"] = statusTV2.text
                }
                else {
                    hashMap1["sender_nickname"] = dialog.nickname.text.toString()
                    hashMap2["receiver_nickname"] = dialog.nickname.text.toString()
                }


                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom.toString())
                    .updateChildren(hashMap2)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom.toString())
                            .updateChildren(hashMap1)
                            .addOnSuccessListener {

                            }
                    }
            }



            dialog.dismiss()
        }

        dialog.show()


    }

    private fun loadInfo() {
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .child(senderUid.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        statusTV.text = snapshot.child("fullname").value.toString()
                        Picasso.get().load(snapshot.child("avatar").value.toString()).into(avatar_group_add)
                    }
                }

            })
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .child(receiverUid.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        statusTV2.text = snapshot.child("fullname").value.toString()
                        Picasso.get().load(snapshot.child("avatar").value.toString()).into(avatar_group_add2)
                    }

                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}