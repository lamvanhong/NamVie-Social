package com.lamhong.viesocial.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

                            var dateFormat=SimpleDateFormat("hh:mm a")
                            val date = Date(time as Long)



                            holder.textMess.text = lastMess as String
                            holder.timeMess.text = dateFormat.format(date)
                        }
                        else {
                            holder.textMess.text = "Tap to chat"
                            holder.timeMess.text = ""

                        }
                    }
                })

        holder.textView.text = currentItem.getName()
        Picasso.get().load(currentItem.getAvatar()).into(holder.imageView)

    }

    class MessageUsersViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.profile_image_message
        var textView = itemView.user_name_message
        var textMess = itemView.text_message
        var timeMess = itemView.time_message
    }
}