package com.lamhong.viesocial.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
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
import com.lamhong.viesocial.Models.UserInfor
import com.lamhong.viesocial.ProfileActivity
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_confirm_deletefriend.*

class UserAdapter(private var _context : Context,private var _user :List<User>,private var isFragment :Boolean=false
                , private var filter : String):
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
        var statusFriend: String=""
        print(user.getName())
       holder.tv_name.text=user.getName()
        Picasso.get().load(user?.getAvatar()).placeholder(R.drawable.duongtu).into(holder.userImage)
    //    holder.tv_descript.text=user.getEmail()
//        Picasso.get().load(user.getImageurl()).placeholder(R.drawable.duongtu1).into(holder.userImage)
        checkFriendsx(user.getUid(), holder.btn_add)

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
        getShortInfor(holder.tv_descript , user.getUid())

        holder.btn_add.setOnClickListener {
            /*
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

             */
            when(holder.btn_add.tag) {
                "friend" -> {
                    openDialog(Gravity.BOTTOM, _context, "deletefriend", user.getUid())

                }
                "dangcho" -> {
                    openDialog(Gravity.BOTTOM, _context, "deletedangcho", user.getUid())


                }
                "nofriend" -> {
                    setNotify(user.getUid())
                    fireabaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(user.getUid())
                            .setValue("pendinginvite")
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(user.getUid())
                        .child("friendList").child(fireabaseUser!!.uid)
                        .setValue("pendingconfirm")

                    fireabaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it.toString())
                            .child("followingList").child(user.getUid())
                            .setValue(true)
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(user.getUid())
                        .child("followerList").child(fireabaseUser!!.uid)
                        .setValue(true)
                }
                "xacnhan" -> {
                    deleteNotifyMyself(user.getUid())
                    fireabaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(user.getUid())
                            .setValue("friend")
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(user.getUid())
                        .child("friendList").child(fireabaseUser!!.uid)
                        .setValue("friend")

                    fireabaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it.toString())
                            .child("followingList").child(user.getUid())
                            .setValue(true)
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(user.getUid())
                        .child("followerList").child(fireabaseUser!!.uid)
                        .setValue(true)

                }
            }
        }
    }

    private fun getShortInfor(tvDescript: TextView, userID: String) {
        val userDetailRef = FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(userID)
        userDetailRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val userInfor: UserInfor = UserInfor()
                    userInfor!!.setEducation(snapshot.child("education").child("value").value.toString())
                    userInfor!!.setBio(snapshot.child("bio").value.toString())
                    userInfor!!.setHome(snapshot.child("home").value.toString())
                    userInfor!!.setHomeTown(snapshot.child("homeTown").value.toString())
                    userInfor!!.setJob(snapshot.child("job").value.toString())
                    userInfor!!.setRelationship(snapshot.child("relationship").value.toString())
                    userInfor!!.setWorkPlace(snapshot.child("workPlace").value.toString())
                    when(filter){
                        "hometown"->{
                            if(userInfor.getHomeTown()!=null && userInfor.getHomeTown()!="" &&
                                userInfor.getHomeTown()!="null"){
                                tvDescript.text=userInfor.getHomeTown()
                            }
                            else{
                                tvDescript.text=""
                            }
                        }
                        "home"->{
                            if(userInfor.getHome()!=null && userInfor.getHome()!="" &&
                                userInfor.getHome()!="null"){
                                tvDescript.text=userInfor.getHome()
                            }
                            else{
                                tvDescript.text=""
                            }
                        }
                        "school" ->{
                            if(userInfor.getEducation()!=null && userInfor.getEducation()!="" &&
                                userInfor.getEducation()!="null"){
                                tvDescript.text=userInfor.getEducation()
                            }
                            else{
                                tvDescript.text=""
                            }
                        }
                        "workplace"->{
                            if(userInfor.getWorkPlace()!=null && userInfor.getWorkPlace()!="" &&
                                userInfor.getWorkPlace()!="null"){
                                tvDescript.text=userInfor.getWorkPlace()
                            }
                            else{
                                tvDescript.text=""
                            }
                        }
                        "work"->{
                            if(userInfor.getJob()!=null && userInfor.getJob()!="" &&
                                userInfor.getJob()!="null"){
                                tvDescript.text=userInfor.getJob()
                            }
                            else{
                                tvDescript.text=""
                            }
                        }
                    }


                }
                else{
                    tvDescript.text=""
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun openDialog(gravity: Int, v:Context, type: String , profileId: String) {

        val dialog = Dialog(v)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_deletefriend)

        val window = dialog.window

        if (window==null) {
            return
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window.attributes
        windowAttributes.gravity = gravity

        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true)
        }
        if(type=="deletedangcho"){
            dialog.name.text="Chắc chắn hủy việc gửi lời mời"
        }
        else if(type=="deletefriend"){
            dialog.name.text="Chắc chắn xóa kết bạn"

        }
        val yes = dialog.btn_yes
        val no = dialog.btn_no


        yes.setOnClickListener {
            if(type=="deletedangcho"){
                removeDangCho(profileId)
                deleteNotify(profileId)
            }
            else if(type=="deletefriend"){
                removeFriend(profileId)

            }

            dialog.dismiss()
        }

        no.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }
    fun removeDangCho(profileId:String){
        fireabaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("friendList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("friendList").child(fireabaseUser!!.uid)
            .removeValue()
        fireabaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("followingList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followerList").child(fireabaseUser!!.uid)
            .removeValue()
    }
    fun removeFriend(profileId : String){
        fireabaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("friendList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("friendList").child(fireabaseUser!!.uid)
            .removeValue()

        fireabaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("followingList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followerList").child(fireabaseUser!!.uid)
            .removeValue()
    }
    private fun deleteNotify(user: String){
        FirebaseDatabase.getInstance().reference
            .child("Notify").child(user).child(fireabaseUser!!.uid).removeValue()
    }
    private fun deleteNotifyMyself(user : String){
        FirebaseDatabase.getInstance().reference
            .child("Notify").child(fireabaseUser!!.uid).child(user).removeValue()
    }
    private fun setNotify(userNotifyID : String){
        val notiRef= FirebaseDatabase.getInstance().reference
            .child("Notify").child(userNotifyID)
        val notiMap= HashMap<String, String>()
        //val idpush : String = notiRef.push().key.toString()
        notiMap["userID"]=fireabaseUser!!.uid
        notiMap["notify"]="Đã gửi lời mời kết bạn"
        notiMap["postID"]="active"
        notiMap["type"]="loimoiketban"
        notiMap["notifyID"]=fireabaseUser!!.uid

        notiRef.child(fireabaseUser!!.uid).setValue(notiMap)
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
    private fun checkFriendsx(profileId: String,btnAddfriend: AppCompatButton ) {
        var statusFriend: String=""
        val friendref= FirebaseDatabase.getInstance().reference
            .child("Friends").child(fireabaseUser!!.uid)
            .child("friendList")

        if(friendref!=null){
            friendref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        if(snapshot.child(profileId).value=="friend")
                        {
                            btnAddfriend.text="Bạn bè"
                            btnAddfriend.tag="friend"
                            btnAddfriend.setBackgroundResource(R.drawable.custom_button_whi)
                            btnAddfriend.setTextColor(Color.parseColor("#03A9F4"))

                        }
                        else if(snapshot.child(profileId).value=="pendingconfirm"){
                            btnAddfriend.text="Xác nhận"
                            btnAddfriend.tag="xacnhan"
                            //btnAddfriend.setTextColor(Color.parseColor("#00BCD4"))
                            btnAddfriend.setBackgroundResource(R.drawable.custom_btn_whi_blue)
                            btnAddfriend.setTextColor(Color.parseColor("#FFFFFF"))

                        }
                        else if(snapshot.child(profileId).value=="pendinginvite"){
                            btnAddfriend.text="Đang chờ"
                            btnAddfriend.setTextColor(Color.parseColor("#858585"))
                            btnAddfriend.tag="dangcho"
                            btnAddfriend.setBackgroundColor(Color.parseColor("#C3B2B7"))
                            btnAddfriend.setTextColor(Color.parseColor("#FFFFFF"))
                        }

                    }
                    else{
                        btnAddfriend.text="Kết bạn"
                        btnAddfriend.tag="nofriend"
                        btnAddfriend.setBackgroundResource(R.drawable.custom_btn_whi_blue)
                        btnAddfriend.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    class viewHolder (@NonNull itemview: View) : RecyclerView.ViewHolder(itemview) {
        var tv_name: TextView = itemview.findViewById(R.id.tv_name)
        var tv_descript : TextView = itemview.findViewById(R.id.tv_shortInfor_user)
        var userImage : CircleImageView = itemview.findViewById(R.id.image_avatar)
        var btn_add: AppCompatButton = itemview.findViewById(R.id.btn_addFriend)

    }

}