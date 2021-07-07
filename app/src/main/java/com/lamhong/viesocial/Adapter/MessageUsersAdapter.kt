package com.lamhong.viesocial.Adapter

import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.ChatLogActivity
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_users_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageUsersAdapter(private val messageUsersList: ArrayList<User>) : RecyclerView.Adapter<MessageUsersAdapter.MessageUsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_users_item,parent,false)

        return MessageUsersViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messageUsersList.size
    }

    override fun onBindViewHolder(holder: MessageUsersViewHolder, position: Int) {
        val currentItem = messageUsersList[position]

        val senderId = FirebaseAuth.getInstance().uid
        val senderRoom = senderId + currentItem.getUid()
        val receiveRoon = currentItem.getUid() + senderId

                FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val lastMess = snapshot.child("lastMess").value
                            val time = snapshot.child("lastTime").value


                            var dateFormat=SimpleDateFormat("EEE hh:mm a")
                            val date = Date(time.toString().toLong())



                            holder.textMess.text = lastMess as String
                            holder.timeMess.text = dateFormat.format(date)
                        }
                        else {
                            holder.textMess.text = "Tap to chat"
                            holder.timeMess.text = ""

                        }
                    }
                })





        FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(senderRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.d("Tag",snapshot.value.toString())
                    if (snapshot.exists()) {
                        val time = snapshot.child("lastTime").value
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .orderByChild("timestamp")
                            .equalTo(time.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    for (ss in snapshot.children)
                                    {
                                        if (ss.exists()) {
                                            if (ss.child("seen").value==true) {
                                                holder.tick.visibility = View.VISIBLE
                                                Log.d("Tag",senderRoom)
                                            }
                                            else {
                                                holder.tick.visibility = View.INVISIBLE
                                            }
                                        }
                                    }


                                }

                            })
                    }
                }

            })


        FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(receiveRoon)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.d("Tag",snapshot.value.toString())
                    if (snapshot.exists()) {
                        val time = snapshot.child("lastTime").value
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiveRoon)
                            .child("message")
                            .orderByChild("timestamp")
                            .equalTo(time.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    for (ss in snapshot.children)
                                    {
                                        if (ss.exists()) {
                                            Log.d("Tag",ss.value.toString())

                                            if (ss.child("seen").value==false) {
                                                holder.textMess.setTypeface(null,Typeface.BOLD)
                                                holder.textView.setTypeface(null,Typeface.BOLD)
                                                holder.timeMess.setTypeface(null,Typeface.BOLD)
                                            }
                                            else {
                                                holder.textMess.setTypeface(null,Typeface.NORMAL)
                                                holder.textView.setTypeface(null,Typeface.NORMAL)
                                                holder.timeMess.setTypeface(null,Typeface.NORMAL)
                                            }
                                        }
                                    }


                                }

                            })
                    }
                }

            })


        holder.textView.text = currentItem.getName()
        Picasso.get().load(currentItem.getAvatar()).into(holder.imageView)


        holder.itemView.setOnClickListener{v: View ->

            val intent = Intent(v.context, ChatLogActivity::class.java)

            intent.putExtra("name",currentItem.getName())
            intent.putExtra("image",currentItem.getAvatar())
            intent.putExtra("uid",currentItem.getUid())
            intent.putExtra("image",currentItem.getAvatar())

            v.context.startActivity(intent)
        }

        FirebaseDatabase.getInstance().reference.child("chats")
            .child(senderRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("receiver_nickname").value != null)
                        holder.textView.text = snapshot.child("receiver_nickname").value.toString()
                }

            })

    }

    class MessageUsersViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.profile_image_message
        var textView = itemView.user_name_message
        var textMess = itemView.text_message
        var timeMess = itemView.time_message
        var tick = itemView.tick
    }


}