package com.lamhong.mybook.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lamhong.mybook.CommentActivity
import com.lamhong.mybook.DetailPostFragment
import com.lamhong.mybook.Fragment.ProfileFragment
import com.lamhong.mybook.Models.Notify
import com.lamhong.mybook.Models.Post
import com.lamhong.mybook.Models.User
import com.lamhong.mybook.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.notification_add_friend_item.view.*

class NotifyAdapter (private val mContext : Context, private val mLstNotify: List<Notify>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    lateinit var fireabaseUser: FirebaseUser
    inner class ViewHolder0(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var avatar_image: CircleImageView
        var contentNotify: TextView
        var uname: TextView
        var postImage: ImageView
        init {
            avatar_image= itemView.findViewById(R.id.avatar_innotifi)
            contentNotify= itemView.findViewById(R.id.content_innotifi)
            uname=itemView.findViewById(R.id.userName_innotifi)
            postImage= itemView.findViewById(R.id.post_imageview)
        }
    }
    inner class ViewHolder1(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var avatar_image: CircleImageView
        var uname: TextView
        var btnChapNhan: Button
        var btnXoa: Button
        var confirmContainer : LinearLayout
        var statusConfirm: TextView
        init {
            avatar_image= itemView.findViewById(R.id.avatar_innotifi)
            uname=itemView.findViewById(R.id.userName_innotifi)
            btnChapNhan= itemView.findViewById(R.id.btn_chapNhan)
            btnXoa= itemView.findViewById(R.id.btn_tuChoi)
            confirmContainer= itemView.findViewById(R.id.confirmContainer)
            statusConfirm = itemView.findViewById(R.id.statusConfirm)
        }
    }
    inner class ViewHolder2(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var avatar_image: CircleImageView
        var contentNotify: TextView
        var uname: TextView
        var avatarNotify: CircleImageView
        init {
            avatar_image= itemView.findViewById(R.id.avatar_innotifi)
            contentNotify= itemView.findViewById(R.id.content_innotifi)
            uname=itemView.findViewById(R.id.userName_innotifi)
            avatarNotify= itemView.findViewById(R.id.avatar_noti)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(mLstNotify[position].getType()=="loimoiketban"){
            return 1
        }
        else if(mLstNotify[position].getType()=="changeavatar"){
            return 2
        }
        else return 0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            0->{
                val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent, false)
                return ViewHolder0(view)
            }
            1->{
                val view = LayoutInflater.from(mContext).inflate(R.layout.notification_add_friend_item, parent, false)
                return ViewHolder1(view)
            }
            2->{
                val view = LayoutInflater.from(mContext).inflate(R.layout.notification_changeavatar_item, parent, false)
                return ViewHolder2(view)
            }
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent, false)
        return ViewHolder0(view)
    }

    override fun onBindViewHolder(holderx: RecyclerView.ViewHolder, position: Int) {
        fireabaseUser=FirebaseAuth.getInstance().currentUser
        when(holderx.itemViewType){
            0->{
                val holder: ViewHolder0= holderx as ViewHolder0
                val notify  = mLstNotify[position]
                if(notify.getType()=="thichbaiviet" || notify.getType()==""){
                    holder.postImage.visibility=View.VISIBLE
                    showImagePost(holder.postImage,notify.gePostID())
                    // navigate to post detail

                    holder.contentNotify.text="Đã thích bài viết của bạn"
                }
                else if (notify.getType()=="binhluan"){
                    holder.postImage.visibility=View.VISIBLE
                    showImagePost(holder.postImage,notify.gePostID())
                    holder.contentNotify.text="Đã bình luận trên bài viết của bạn"
                }
                else if(notify.getType()=="loimoiketban"){
                    holder.postImage.visibility=View.GONE
                    holder.contentNotify.text="Đã gửi lời mời kết bạn"
                }
                showUserInfor(holder.avatar_image, holder.uname, notify.getUserID())
                holder.itemView.setOnClickListener{
                    if(notify.getType()=="loimoiketban"){
                        val pref= mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        pref.putString("profileId", notify.getUserID())
                        pref.apply()
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, ProfileFragment()).commit()

                    }
                    else if(notify.getType()=="thichbaiviet") {
                        val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                        pref.putString("postID",notify.gePostID())
                        pref.apply()
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, DetailPostFragment()).commit()


                    }
                    else if(notify.getType()=="binhluan"){
                        val commentIntent= Intent(mContext, CommentActivity::class.java)
                        commentIntent.putExtra("postID", notify.gePostID())
                        commentIntent.putExtra("publisher", notify.getUserID())
                        mContext.startActivity(commentIntent)


                    }
                }
            }
            1->{
                val holder: ViewHolder1= holderx as ViewHolder1
                val notify  = mLstNotify[position]

                showUserInfor(holder.avatar_image, holder.uname, notify.getUserID())
                holder.itemView.setOnClickListener{
                    if(notify.getType()=="loimoiketban"){
                        val pref= mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                        pref.putString("profileId", notify.getUserID())
                        pref.apply()
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, ProfileFragment()).commit()

                    }
                }
                holder.statusConfirm.visibility=View.GONE
                holder.itemView.btn_chapNhan.setOnClickListener{
                    setConfirmToNotify(notify.getnotifyID(), "daxacnhan")
                    holder.confirmContainer.visibility=View.GONE
                    holder.statusConfirm.text="Đã xác nhận"
                    holder.statusConfirm.visibility=View.VISIBLE
                    // action
                    fireabaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(notify.getUserID())
                            .setValue("friend").addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                  //  setNotify(user.getUid())
                                    fireabaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Friends").child(notify.getUserID())
                                            .child("friendList").child(it1.toString())
                                            .setValue("friend").addOnCompleteListener { task ->
                                                if (task.isSuccessful) {

                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
                holder.itemView.btn_tuChoi.setOnClickListener{
                    setConfirmToNotify(notify.getnotifyID(), "daxoa")
                    holder.confirmContainer.visibility=View.GONE
                    holder.statusConfirm.text="Đã xóa lời mời"
                    holder.statusConfirm.visibility=View.VISIBLE
                    //action
                    fireabaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(notify.getUserID())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    fireabaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Friends").child(notify.getUserID())
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
                when(notify.gePostID()){
                    "daxoa"->{
                        Interactable("daxoa", holder.confirmContainer, holder.statusConfirm)
                    }
                    "daxacnhan"->{
                        Interactable("daxacnhan", holder.confirmContainer, holder.statusConfirm)

                    }
                    "active"->{
                        checkStatusFriends(holder.confirmContainer, holder.statusConfirm, notify.gePostID(), notify.getUserID())
                    }
                }




            }
            2->{
                val holder: ViewHolder2= holderx as ViewHolder2
                val notify  = mLstNotify[position]
                showAvatar(holder.avatar_image,notify.gePostID())

                showUserInfor(holder.avatar_image, holder.uname, notify.getUserID())
                holder.itemView.setOnClickListener{
                    val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    pref.putString("postID",notify.gePostID())
                    pref.apply()
                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, DetailPostFragment()).commit()

                }
            }
        }


    }
    private fun setConfirmToNotify(notifyID : String, confirmValue : String){
        FirebaseDatabase.getInstance().reference
            .child("Notify").child(fireabaseUser.uid!!).child(notifyID)
            .child("postID").setValue(confirmValue)
    }

    private fun Interactable(status: String,confirmContainer: LinearLayout, statusConfirm: TextView){
        if(status=="daxoa"){
            confirmContainer.visibility=View.GONE
            statusConfirm.visibility=View.VISIBLE
            statusConfirm.text="Đã từ chối"
        }
        else{
            confirmContainer.visibility=View.GONE
            statusConfirm.visibility=View.VISIBLE
            statusConfirm.text="Đã xác nhận"
        }

    }

    private fun checkStatusFriends(confirmContainer: LinearLayout, statusConfirm: TextView, gePostID: String, userID: String) {

        val ref= FirebaseDatabase.getInstance().reference
            .child("Friends").child(fireabaseUser.uid).child("friendList").child(userID)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.value=="pendingconfirm"){
                        confirmContainer.visibility=View.VISIBLE
                        statusConfirm.visibility=View.GONE
                    }
                    else if(snapshot.value=="friend"){
                        confirmContainer.visibility=View.GONE
                        statusConfirm.visibility=View.VISIBLE
                        statusConfirm.text="Đã xác nhận"
                    }
                }else{
                    confirmContainer.visibility=View.GONE
                    statusConfirm.visibility=View.VISIBLE
                    statusConfirm.text="Đã từ chối"
                }
            }
        })

    }

    private fun showUserInfor(avatar: CircleImageView, username: TextView, publisher: String){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(publisher)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    user!!.setAvatar(snapshot.child("avatar").value.toString())
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.cty).into(avatar)
                    username.text=user.getName()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun showImagePost(postImage: ImageView, postID: String  ){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents")
            .child("Posts").child(postID)
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val post = snapshot.getValue(Post::class.java)
                    post!!.setpost_image(snapshot.child("post_image").value.toString())
                    Picasso.get().load(post!!.getpost_image()).placeholder(R.drawable.duongtu).into(postImage)

                }
            }
        })
    }
    private fun showAvatar(avatar : CircleImageView, postID: String){
        val avatarRef = FirebaseDatabase.getInstance().reference
            .child("Contents").child("AvatarPost")
            .child(postID)
        avatarRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val imageUri = snapshot.child("post_image").getValue().toString()
                    Picasso.get().load(imageUri).into(avatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemCount(): Int {
        return mLstNotify.size
    }
}