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
import com.lamhong.viesocial.Models.GroupMessage
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_group_chat_left.view.*
import kotlinx.android.synthetic.main.row_group_chat_right.view.*
import kotlinx.android.synthetic.main.row_group_chat_right.view.message
import java.text.SimpleDateFormat
import java.util.*

class GroupMessageAdapter(private val groupMessageList: ArrayList<GroupMessage>) :RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    companion object {
        const val ITEM_RIGHT = 1
        const val ITEM_LEFT = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == GroupMessageAdapter.ITEM_RIGHT) {
            return RightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_group_chat_right,parent,false))
        }
        else {
            return LeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_group_chat_left, parent, false))
        }
    }



    override fun getItemCount(): Int {
        return groupMessageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == GroupMessageAdapter.ITEM_RIGHT) {
            (holder as GroupMessageAdapter.RightViewHolder).bind(position)
        }
        else {
            (holder as GroupMessageAdapter.LeftViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (FirebaseAuth.getInstance().uid.equals(groupMessageList[position].getsenderIDG())){
            return GroupMessageAdapter.ITEM_RIGHT
        }
        else {
            return GroupMessageAdapter.ITEM_LEFT
        }
    }

    private inner class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var message = itemView.message
        var timestamp = itemView.time_chat_sent
        var image = itemView.image_chat_sent
        var timestampimage = itemView.time_chat_sent2_group





        fun bind (position: Int) {

            val recyclerViewModel = groupMessageList[position]

            var dateFormat= SimpleDateFormat("EEE hh:mm a")
            var dateFormat2= SimpleDateFormat("hh:mm a")
            val date = Date(recyclerViewModel.gettimestampG().toLong())
            val currenttime = System.currentTimeMillis()
            val date2 = Date(currenttime)


            val senderID = recyclerViewModel.getsenderIDG()

            if (recyclerViewModel.getTypeG()=="text") {
                message.text= recyclerViewModel.getMessageG()
            }
            else if (recyclerViewModel.getTypeG()=="image") {
                image.visibility = View.VISIBLE
                message.visibility = View.GONE
                timestamp.visibility = View.GONE
                timestampimage.visibility = View.VISIBLE

                Picasso.get().load(recyclerViewModel.getMessageG()).placeholder(R.drawable.loading_image).into(image)
            }

            if (date.date!=date2.date) {
                if (date.date==(date2.date-1)) {
                    timestamp.text = "Hôm qua"
                    timestampimage.text = "Hôm qua"
                }
                else {
                    timestamp.text = dateFormat.format(date)
                    timestampimage.text = dateFormat.format(date)
                }
            }
            else {
                timestamp.text = dateFormat2.format(date)
                timestampimage.text = dateFormat2.format(date)
            }


        }

    }

    private inner class LeftViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var message = itemView.message_group
        var image = itemView.image_chat_log_group
        var timestamp = itemView.time_chat_receive_group
        var name = itemView.name_group
        var imageimage = itemView.image_chatlog2_group
        var image_chat = itemView.image_chat_receive_group
        var timestamp2 = itemView.time_chat_receive2_group
        var name2 = itemView.name_group2

        fun bind (position: Int) {
            val recyclerViewModel = groupMessageList[position]

            var dateFormat= SimpleDateFormat("EEE hh:mm a")
            var dateFormat2= SimpleDateFormat("hh:mm a")
            val date = Date(recyclerViewModel.gettimestampG().toLong())
            val currenttime = System.currentTimeMillis()
            val date2 = Date(currenttime)

            val senderID = recyclerViewModel.getsenderIDG()

            if (recyclerViewModel.getTypeG()=="text") {
                message.text= recyclerViewModel.getMessageG()
                //Picasso.get().load(uri).into(image)
            }
            else if (recyclerViewModel.getTypeG()=="image") {
                image_chat.visibility = View.VISIBLE
                message.visibility = View.GONE
                timestamp.visibility = View.GONE
                timestamp2.visibility = View.VISIBLE
                image.visibility = View.GONE
                imageimage.visibility = View.VISIBLE
                name.visibility = View.GONE
                name2.visibility = View.VISIBLE


                Picasso.get().load(recyclerViewModel.getMessageG()).placeholder(R.drawable.loading_image).into(image_chat)
                //Picasso.get().load().into(imageimage)
            }

            if (date.date!=date2.date) {

                if (date.date==(date2.date-1)) {
                    timestamp.text = "Hôm qua"
                    //timestamp2.text = "Hôm qua"
                }
                else {
                    timestamp.text = dateFormat.format(date)
                    timestamp2.text = dateFormat.format(date)
                }
            }

            else {
                timestamp.text = dateFormat2.format(date)
                timestamp2.text = dateFormat2.format(date)
            }

            FirebaseDatabase.getInstance().reference.child("UserInformation")
                .orderByChild("uid").equalTo(recyclerViewModel.getsenderIDG())
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            val namee = "" + ds.child("fullname").value
                            name.text = namee
                            name2.text = namee
                        }
                    }

                })

        }
    }


}