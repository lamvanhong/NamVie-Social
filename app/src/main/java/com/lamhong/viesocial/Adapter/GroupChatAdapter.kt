package com.lamhong.viesocial.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.GroupChatsLogActivity
import com.lamhong.viesocial.Models.GroupChat
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_groupchats_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class GroupChatAdapter(private val groupChatsList: ArrayList<GroupChat>) : RecyclerView.Adapter<GroupChatAdapter.GroupChatsViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_groupchats_list,parent,false)

        return GroupChatsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupChatsList.size
    }

    override fun onBindViewHolder(holder: GroupChatsViewHolder, position: Int) {
        val currentItem = groupChatsList[position]
        val groupID = currentItem.getGroupID()
        val groupIcon = currentItem.getGroupIcon()
        val groupTitle = currentItem.getGroupTitle()


        loadLastMessage(currentItem,holder)


        holder.groupTitleTv.text = groupTitle

        try {
            Picasso.get().load(groupIcon).into(holder.groupIconIv)
        }
        catch (e:Exception) {
            holder.groupIconIv.setImageResource(R.drawable.sontung)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,GroupChatsLogActivity::class.java)

            intent.putExtra("groupID",groupID)

            it.context.startActivity(intent)
        }

    }

    private fun loadLastMessage(currentItem: GroupChat, holder: GroupChatAdapter.GroupChatsViewHolder) {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(currentItem.getGroupID())
            .child("messages").limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        val message = "" + ss.child("messageG").value
                        val timestamp = "" + ss.child("timestampG").value
                        val sender = "" + ss.child("senderIDG").value
                        val messageType = "" +ss.child("typeG").value

                        var dateFormat= SimpleDateFormat("EEE hh:mm a")
                        var dateFormat2= SimpleDateFormat("hh:mm a")

                        val currenttime = System.currentTimeMillis()

                        val date = Date(timestamp.toLong())
                        val date2 = Date(currenttime)

                        if (date.day!=date2.day) {
                            holder.groupTimeTv.text = dateFormat.format(date)
                        }
                        else {
                            holder.groupTimeTv.text = dateFormat2.format(date)
                        }

                        if (messageType=="image") {
                            holder.groupMessageTv.text = "[Hình ảnh]"
                        }
                        else {
                            holder.groupMessageTv.text = message
                        }

                        FirebaseDatabase.getInstance().reference.child("UserInformation")
                            .orderByChild("uid")
                            .equalTo(sender)
                            .addValueEventListener(object : ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ss in snapshot.children) {
                                        val name = "" + ss.child("fullname").value + ":"
                                        holder.groupMemberTv.text = name
                                    }
                                }

                            })
                    }
                }

            })
    }


    class GroupChatsViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupIconIv = itemView.image_group_chats
        val groupTitleTv = itemView.title_group_chats
        val groupMemberTv = itemView.member_group_chats
        val groupTimeTv = itemView.time_group_chats
        val groupMessageTv = itemView.text_group_chats

    }

}