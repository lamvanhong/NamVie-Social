package com.lamhong.viesocial.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendAdapter (private val mContext: Context, private val mLstFriend : List<String> ,
                     private val mLstStatus : List<String> , private val userID : String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var fireBaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser

    inner class ViewHolder0(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var imageAvatar : CircleImageView
        var tv_username: TextView
        var btnKetBan: Button
        init {
            imageAvatar = itemView.findViewById(R.id.image_avatar_friendlayout)
            tv_username= itemView.findViewById(R.id.tv_username)
            btnKetBan = itemView.findViewById(R.id.btn_ketban)
        }
    }
    inner class ViewHolder1(@NonNull itemview: View) : RecyclerView.ViewHolder(itemview){
        var imageAvatar: CircleImageView
        var tv_username: TextView
        var btnFunction: ImageView
        init {
            imageAvatar = itemview.findViewById(R.id.image_avatar_friendlayout)
            tv_username = itemview.findViewById(R.id.tv_username)
            btnFunction = itemview.findViewById(R.id.btn_Function)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val status : String= mLstStatus[position]  //
        when(status){
            "pendingconfirm"->{
               return 0
            }
            "pendinginvite"->{
                return 0
            }
            "friend"->{
                return 1
            }
            else ->{
                return 0
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

       when(viewType){
           0->{
               val view = LayoutInflater.from(mContext).inflate(R.layout.friend_nofriend_layout, parent, false)
               return ViewHolder0(view)
           }
           1->{
               val view = LayoutInflater.from(mContext).inflate(R.layout.friend_friend_layout, parent, false)
               return ViewHolder1(view)
           }
       }
        val view = LayoutInflater.from(mContext).inflate(R.layout.friend_nofriend_layout, parent, false)
        return ViewHolder0(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val status : String= mLstStatus[position]
        when(holder.itemViewType){
           0->{
               val holder0 : ViewHolder0= holder as ViewHolder0
               val user = mLstFriend[position]
               setInfor(user, holder0.imageAvatar, holder0.tv_username)
               when(status){
                   "pendingconfirm"->{
                       holder0.btnKetBan.text="Chấp nhận"
                   }
                   "pendinginvite"->{
                       holder0.btnKetBan.text="Đã gửi lời mời"
                   }
                   else ->{
                   }
               }
           }
            1->{
                val holder1 : ViewHolder1 = holder as ViewHolder1
                val user = mLstFriend[position]
                setInfor(user, holder1.imageAvatar, holder1.tv_username)

            }

        }
    }

    private fun setInfor(uid: String, imageAvatar: CircleImageView, tvUsername: TextView) {
        val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation").child(uid)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.duongtu).into(imageAvatar)

                    tvUsername.setText(user!!.getName())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
       return mLstFriend.size
    }

}