package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.ProfileActivity
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private var _context : Context,private var _user :List<User>,private var isFragment :Boolean=false):
        RecyclerView.Adapter<UserAdapter.viewHolder>() {
        private var fireabaseUser: FirebaseUser?= FirebaseAuth.getInstance().currentUser
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.viewHolder {
        var view = LayoutInflater.from(_context).inflate(R.layout.user_item, parent , false)
        return UserAdapter.viewHolder(view)
    }

    override fun getItemCount(): Int {
        return _user.size
    }

    override fun onBindViewHolder(holder: UserAdapter.viewHolder, position: Int) {

        var user = _user[position]
        print(user.getName())
       holder.tv_name.text=user.getName()
        Picasso.get().load(user?.getAvatar()).placeholder(R.drawable.duongtu).into(holder.userImage)
    //    holder.tv_descript.text=user.getEmail()
//        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.duongtu1).into(holder.userImage)
        checkFriendStatus(user.getUid(), holder.btn_add)

        holder.itemView.setOnClickListener(View.OnClickListener {
//            val pref= _context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
//            pref.putString("profileId", user.getUid())
//            pref.apply()
//            (_context as FragmentActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.frameLayout, ProfileFragment()).commit()

            // new abstract
            val userIntent = Intent(_context, ProfileActivity::class.java)
            userIntent.putExtra("profileID", user.getUid())
            _context.startActivity(userIntent)

        })


        holder.btn_add.setOnClickListener {
            if(holder.btn_add.text.toString()=="Kết bạn"){

                fireabaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(user.getUid())
                            .setValue("pendinginvite").addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    setNotify(user.getUid())
                                    fireabaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Friends").child(user.getUid())
                                                .child("friendList").child(it1.toString())
                                                .setValue("pendingconfirm").addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }
            else{
                fireabaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(user.getUid())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    fireabaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Friends").child(user.getUid())
                                                .child("friendList").child(it1.toString())
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }
        }
    }
    private fun setNotify(userNotifyID : String){
        val notiRef= FirebaseDatabase.getInstance().reference
            .child("Notify").child(userNotifyID)
        val notiMap= HashMap<String, String>()
        val idpush : String = notiRef.push().key.toString()
        notiMap["userID"]=fireabaseUser!!.uid
        notiMap["notify"]="Đã gửi lời mời kết bạn"
        notiMap["postID"]="active"
        notiMap["type"]="loimoiketban"
        notiMap["notifyID"]=idpush

        notiRef.child(idpush).setValue(notiMap)
    }
    private fun checkFriendStatus(uid: String, btnAdd: AppCompatButton) {
        val friendref= fireabaseUser?.uid.let{it->
            FirebaseDatabase.getInstance().reference
                    .child("Friends").child(it.toString())
                    .child("friendList")
        }
        friendref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(uid).exists()){
                    if(snapshot.child(uid).value=="friend"){
                        btnAdd.text="Bạn bè"
                        btnAdd.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        btnAdd.setTextColor(Color.parseColor("#00BCD4"))

                    }
                    else if(snapshot.child(uid).value=="pendingconfirm"){
                        btnAdd.text="Xác nhận"
                        btnAdd.setBackgroundColor(Color.parseColor("#42C648"))
                    }
                    else if(snapshot.child(uid).value=="pendinginvite"){
                        btnAdd.text="Đã gửi lời mời"
                        btnAdd.setBackgroundColor(Color.parseColor("#C3B2B7"))
                    }

                }else{
                    btnAdd.text="Kết bạn"
                    btnAdd.setBackgroundColor(Color.parseColor("#00BCD4"))
                    btnAdd.setTextColor(Color.parseColor("#FFFFFF"))
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

    }

    class viewHolder (@NonNull itemview: View) : RecyclerView.ViewHolder(itemview) {
        var tv_name: TextView = itemview.findViewById(R.id.tv_name)
        var tv_descript : TextView = itemview.findViewById(R.id.tv_shortInfor_user)
        var userImage : CircleImageView = itemview.findViewById(R.id.image_avatar)
         var btn_add: AppCompatButton = itemview.findViewById(R.id.btn_addFriend)

    }


}