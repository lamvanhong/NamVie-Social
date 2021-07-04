package com.lamhong.viesocial.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.lamhong.viesocial.Models.Message
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_receive_message.view.*
import kotlinx.android.synthetic.main.item_sent_message.view.*
import kotlinx.android.synthetic.main.item_sent_message.view.message


class MessageAdapter(private val messageList: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    companion object {
        const val ITEM_SENT = 1
        const val ITEM_RECEIVE = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == ITEM_SENT) {
            return SentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message,parent,false))
        }
        else {
            return ReceiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_receive_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        /*val config = reactionConfig(context) {
            reactions {
                resId    { R.drawable.ic_fb_like }
                resId    { R.drawable.ic_fb_love }
                resId    { R.drawable.ic_fb_laugh }
                reaction { R.drawable.ic_fb_wow scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_fb_sad scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_fb_angry scale ImageView.ScaleType.FIT_XY }
            }
        }

        val popup = ReactionPopup(context, config) { position -> true.also {
            // position = -1 if no selection
        } }*/



        if (holder.itemViewType == ITEM_SENT) {
            (holder as SentViewHolder).bind(position)
        }
        else {
            (holder as ReceiveViewHolder).bind(position)
        }
    }



    override fun getItemViewType(position: Int): Int {
        if (FirebaseAuth.getInstance().uid.equals(messageList[position].getSenderID())){
            return ITEM_SENT
        }
        else {
            return ITEM_RECEIVE
        }
    }


    private inner class SentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var message = itemView.message
        var image = itemView.image_chat_sent


        fun bind(position: Int){
            val recyclerViewModel = messageList[position]

            if (recyclerViewModel.getMessage().equals("photo")) {
                image.visibility = View.VISIBLE
                message.visibility = View.GONE
                Picasso.get().load(recyclerViewModel.getImageUrl()).placeholder(R.drawable.loading_image).into(image)
            }

            message.text= recyclerViewModel.getMessage()
        }

    }

    private inner class ReceiveViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var message = itemView.message
        var image = itemView.image_chatlog
        var image_chat = itemView.image_chat_receive
        //var intent = Intent(itemView.context,NewMessageActivity::class.java)


        var intent = (itemView.getContext() as Activity).intent

        var uri:String?= intent.getStringExtra("image")


        fun bind(position: Int){
            val recyclerViewModel = messageList[position]

            if (recyclerViewModel.getMessage().equals("photo")) {
                image_chat.visibility = View.VISIBLE
                message.visibility = View.GONE
                Picasso.get().load(recyclerViewModel.getImageUrl()).placeholder(R.drawable.loading_image).into(image_chat)
            }

            message.text= recyclerViewModel.getMessage()

            Picasso.get().load(uri).into(image)
        }
    }



}