package com.lamhong.viesocial.Adapter

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.Message
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_remove_mess.*
import kotlinx.android.synthetic.main.item_receive_message.view.*
import kotlinx.android.synthetic.main.item_sent_message.view.*
import kotlinx.android.synthetic.main.item_sent_message.view.message
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(private val messageList: ArrayList<Message>, private val senderRoom:String, private val receiveRoom:String) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

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
        var timestamp = itemView.time_chat_sent
        var seen = itemView.isSeen
        var seenimage = itemView.isSeen2
        var timestampimage = itemView.time_chat_sent2



        fun bind(position: Int){
            val recyclerViewModel = messageList[position]

            var dateFormat= SimpleDateFormat("EEE hh:mm a")
            var dateFormat2=SimpleDateFormat("hh:mm a")

            val currenttime = System.currentTimeMillis()

            val date = Date(recyclerViewModel.getTimestamp().toLong())
            val date2 = Date(currenttime)

            val type = recyclerViewModel.getType()


            if (type == "image") {
                image.visibility = View.VISIBLE
                message.visibility = View.GONE
                seen.visibility = View.GONE
                timestamp.visibility = View.GONE
                seenimage.visibility = View.VISIBLE
                timestampimage.visibility = View.VISIBLE

                Picasso.get().load(recyclerViewModel.getMessage()).placeholder(R.drawable.loading_image).into(image)
            }
            else if (type == "text") {
                message.text= recyclerViewModel.getMessage()

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
                                    message.setBackgroundResource(R.drawable.sent_eb3a2a)
                                }
                                else if (snapshot.value.toString() == "#a598eb" || snapshot.value.toString() == "#A598EB") {
                                    message.setBackgroundResource(R.drawable.sent_a598eb)
                                }
                                else if (snapshot.value.toString() == "#e84fcf" || snapshot.value.toString() == "#E84FCF") {
                                    message.setBackgroundResource(R.drawable.sent_e84fcf)
                                }
                                else if (snapshot.value.toString() == "#0e92eb" || snapshot.value.toString() == "#0E92EB") {
                                    message.setBackgroundResource(R.drawable.sent_0e92eb)
                                }
                                else if (snapshot.value.toString() == "#b53f3f" || snapshot.value.toString() == "#B53F3F") {
                                    message.setBackgroundResource(R.drawable.sent_b53f3f)
                                }
                                else if (snapshot.value.toString() == "#de625b" || snapshot.value.toString() == "#DE625B") {
                                    message.setBackgroundResource(R.drawable.sent_de625b)
                                }
                                else if (snapshot.value.toString() == "#e6a50e" || snapshot.value.toString() == "#E6A50E") {
                                    message.setBackgroundResource(R.drawable.sent_e6a50e)
                                }
                                else if (snapshot.value.toString() == "#69c90c" || snapshot.value.toString() == "#69C90C") {
                                    message.setBackgroundResource(R.drawable.sent_69c90c)
                                }
                                else if (snapshot.value.toString() == "#4e42ad" || snapshot.value.toString() == "#4E42AD") {
                                    message.setBackgroundResource(R.drawable.sent_4e42ad)
                                }
                                else if (snapshot.value.toString() == "#a80ddd" || snapshot.value.toString() == "#A80DDD") {
                                    message.setBackgroundResource(R.drawable.sent_a80ddd)
                                }
                            }

                        }

                    })

            }

//            if (recyclerViewModel.getMessage() == "[Photo]") {
//                image.visibility = View.VISIBLE
//                message.visibility = View.GONE
//                seen.visibility = View.GONE
//                timestamp.visibility = View.GONE
//                seenimage.visibility = View.VISIBLE
//                timestampimage.visibility = View.VISIBLE
//
//
//                Picasso.get().load(recyclerViewModel.getImageUrl()).placeholder(R.drawable.loading_image).into(image)
//            }



//            if (recyclerViewModel.getMessage() == "You unsent a message" || recyclerViewModel.getMessage() == "You deleted a message") {
//                message.setTypeface(null,Typeface.ITALIC)
//            }



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


            if (position==(messageList.size-1)) {
                if (recyclerViewModel.isSeen()) {
                    seen.text  = "Đã xem"
                    seenimage.text = "Đã xem"
                }
                else {
                    seen.text = "Đã nhận"
                    seenimage.text = "Đã nhận"
                }
            }
            else {
                seen.visibility = View.GONE
                seenimage.visibility = View.GONE
            }
//            message.isLongClickable = false
//            message.isClickable = false

            message.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
//                    val builder = AlertDialog.Builder(v?.context)
//
//                    builder.setTitle("Delete")
//                    builder.setMessage("Are you sure to delete this message")
//                    builder.setPositiveButton("Delete") { dialog, which -> deleteMessage(position) }
//
//                    builder.setNegativeButton("Cancel") { dialog, which -> dialog?.dismiss() }
//
//                    builder.create().show()


                    v?.let { openDialog(Gravity.BOTTOM, it, position) }


                    return true

                }

            })

            image.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    v?.let { openDialog(Gravity.BOTTOM, it, position) }


                    return true
                }

            })



        }

    }

    private fun openDialog(gravity: Int, v:View, position: Int) {
        val dialog = Dialog(v.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_remove_mess)


        val window = dialog.window

        if (window==null) {
            return
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window.attributes
        windowAttributes.gravity = gravity

        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true)
        }

        val delete1 = dialog.btn_delete1
        val delete2 = dialog.btn_delete2


        delete1.setOnClickListener {
            deleteMessage1(position)
            dialog.dismiss()
        }

        delete2.setOnClickListener {
            deleteMessage2(position)
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun deleteMessage1(position: Int) {
        val msgTimeStamp = messageList[position].getTimestamp()

        val myUid = FirebaseAuth.getInstance().uid


        val query = FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(senderRoom)
                .child("message")
                .orderByChild("timestamp")
                .equalTo(msgTimeStamp)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("CheckFireBase",snapshot.childrenCount.toString())

                for (ss in snapshot.children) {

//                    val message = ss.getValue(Message::class.java)
//
//                    Log.e("CheckFireBase",ss.key.toString())
//                    message!!.setTimestamp(ss.child("timestamp").value as Long)


                    //if (ss!=null) Log.e("CheckFireBase","true")

/*                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {


                        //Toast.makeText(View.context, "message", Toast.LENGTH_SHORT).show()
                    }*/



                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {


                        //ss.ref.removeValue()
                        val hashMap = hashMapOf<String, Any?>()
                        hashMap["message"] = "Bạn đã gỡ một tin nhắn"
                        hashMap["type"] = "text"
                        ss.ref.updateChildren(hashMap)

                        val lastMess = hashMapOf<String, Any?>()

                        if (position==(messageList.size-1)) {

                            lastMess["lastMess"] = "Bạn đã gỡ một tin nhắn"
                            //lastMess.put("lastTime", date.time)

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            //FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
                        }
                    }


                }
            }

        })



        val query1 = FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(receiveRoom)
                .child("message")
                .orderByChild("timestamp")
                .equalTo(msgTimeStamp)

        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {


                for (ss in snapshot.children) {

//                    val message = ss.getValue(Message::class.java)
//
//                    Log.e("CheckFireBase",ss.key.toString())
//                    message!!.setTimestamp(ss.child("timestamp").value as Long)


                    //if (ss!=null) Log.e("CheckFireBase","true")

/*                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {


                        //Toast.makeText(View.context, "message", Toast.LENGTH_SHORT).show()
                    }*/


                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {

                        //ss.ref.removeValue()
                        val hashMap = hashMapOf<String, Any?>()
                        hashMap["message"] = "Tin nhắn này đã được gỡ"
                        hashMap["type"] = "text"
                        ss.ref.updateChildren(hashMap)

                        if (position==(messageList.size-1)) {

                            val lastMess = hashMapOf<String, Any?>()

                            lastMess["lastMess"] = "Tin nhắn này đã được gỡ"
                            //lastMess.put("lastTime", date.time)

                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
                            //FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
                        }
                    }


                }
            }

        })

    }

    private fun deleteMessage2(position: Int) {

        //val myUid = FirebaseAuth.getInstance().uid

        val msgTimeStamp = messageList[position].getTimestamp()

        val myUid = FirebaseAuth.getInstance().uid

        Log.e("msgTimeStamp",msgTimeStamp.toString())

        val query = FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(senderRoom)
                .child("message")
                .orderByChild("timestamp")
                .equalTo(msgTimeStamp)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("CheckFireBase",snapshot.childrenCount.toString())

                for (ss in snapshot.children) {

//                    val message = ss.getValue(Message::class.java)
//
//                    Log.e("CheckFireBase",ss.key.toString())
//                    message!!.setTimestamp(ss.child("timestamp").value as Long)


                    //if (ss!=null) Log.e("CheckFireBase","true")

/*                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {


                        //Toast.makeText(View.context, "message", Toast.LENGTH_SHORT).show()
                    }*/

                    if (ss.child("senderID").getValue(String::class.java).equals(myUid)) {

                        //ss.ref.removeValue()
                        val hashMap = hashMapOf<String, Any?>()
                        hashMap["message"] = "Bạn đã xóa một tin nhắn"
                        hashMap["type"] = "text"
                        ss.ref.updateChildren(hashMap)

                        if (position==(messageList.size-1)) {

                            val lastMess = hashMapOf<String, Any?>()

                            lastMess["lastMess"] = "Bạn đã xóa một tin nhắn"
                            //lastMess.put("lastTime", date.time)

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            //FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
                        }
                    }

                    else {
                        //ss.ref.removeValue()
                        val hashMap = hashMapOf<String, Any?>()
                        hashMap["message"] = "Tin nhắn này đã được xóa"
                        ss.ref.updateChildren(hashMap)

                        if (position==(messageList.size-1)) {

                            val lastMess = hashMapOf<String, Any?>()

                            lastMess["lastMess"] = "Tin nhắn này đã được xóa"
                            //lastMess.put("lastTime", date.time)

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            //FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
                        }
                    }




                }
            }

        })




    }

    private inner class ReceiveViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var message = itemView.message
        var image = itemView.image_chatlog
        var image_chat = itemView.image_chat_receive
        var timestamp = itemView.time_chat_receive
        var timestamp2 = itemView.time_chat_receive2
        var imageimage = itemView.image_chatlog2
        //var intent = Intent(itemView.context,NewMessageActivity::class.java)


        var intent = (itemView.getContext() as Activity).intent

        var uri:String?= intent.getStringExtra("image")


        fun bind(position: Int){
            val recyclerViewModel = messageList[position]

            var dateFormat= SimpleDateFormat("EEE hh:mm a")
            var dateFormat2=SimpleDateFormat("hh:mm a")
            val date = Date(recyclerViewModel.getTimestamp().toLong())
            val currenttime = System.currentTimeMillis()

            val date2 = Date(currenttime)

            val type = recyclerViewModel.getType()


            if (type == "image") {
                image_chat.visibility = View.VISIBLE
                message.visibility = View.GONE
                timestamp.visibility = View.GONE
                timestamp2.visibility = View.VISIBLE
                image.visibility = View.GONE
                imageimage.visibility = View.VISIBLE

                Picasso.get().load(recyclerViewModel.getMessage()).placeholder(R.drawable.loading_image).into(image_chat)
                Picasso.get().load(uri).into(imageimage)
            }
            else if (type == "text") {
                message.text= recyclerViewModel.getMessage()
                Picasso.get().load(uri).into(image)
            }


//            if (recyclerViewModel.getMessage() == "[Photo]") {
//                image_chat.visibility = View.VISIBLE
//                message.visibility = View.GONE
//                timestamp.visibility = View.GONE
//                timestamp2.visibility = View.VISIBLE
//                image.visibility = View.GONE
//                imageimage.visibility = View.VISIBLE
//
//                Picasso.get().load(recyclerViewModel.getImageUrl()).placeholder(R.drawable.loading_image).into(image_chat)
//                Picasso.get().load(uri).into(imageimage)
//            }



//            if (recyclerViewModel.getMessage() == "This message has been unsent") {
//                message.setTypeface(null,Typeface.ITALIC)
//            }

            if (date.date!=date2.date) {
                if (date.date==(date2.date-1)) {
                    timestamp.text = "Hôm qua"
                    timestamp2.text = "Hôm qua"
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


            message.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
//                    val builder = AlertDialog.Builder(v?.context)
//
//                    builder.setTitle("Delete")
//                    builder.setMessage("Are you sure to delete this message")
//                    builder.setPositiveButton("Delete") { dialog, which -> deleteMessage(position) }
//
//                    builder.setNegativeButton("Cancel") { dialog, which -> dialog?.dismiss() }
//
//                    builder.create().show()


                    v?.let { openDialog(Gravity.BOTTOM, it, position) }


                    return true

                }

            })

        }




    }



}