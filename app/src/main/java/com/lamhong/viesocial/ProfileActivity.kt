package com.lamhong.viesocial

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.ImageProfileAdapter
import com.lamhong.viesocial.Adapter.PostAdapter
import com.lamhong.viesocial.Models.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_my_shot_video.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.avatar
import kotlinx.android.synthetic.main.dialog_confirm_deletefriend.*
import kotlinx.android.synthetic.main.dialog_remove_mess.*
import kotlinx.android.synthetic.main.fragment_setting.*


class ProfileActivity : AppCompatActivity() {

    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser

    private var checkOwner : Boolean =true
    private var statusFriend: String =""
    private var postList : List<Post> ?=null
    private var ImageAdapter: ImageProfileAdapter ?=null

    private var imagePostList: List<Post> ?=null
    private var postAdapter: PostAdapter?=null

    private var shareList: MutableList<SharePost> = ArrayList()
    private var lstTypeAdapter : List<Int> = ArrayList()
    private var lstIndex : List<Int> = ArrayList()


    private var avatarList : ArrayList<Post> = ArrayList()
    private var coverImageList : ArrayList<Post> = ArrayList()

    private var postListID : List<String> = ArrayList()
    private var shareListID : List<String> = ArrayList()

    private var uri: String=""
    private var uriCover: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        btn_return_toolbar.setOnClickListener{
            this.finish()
        }


        firebaseUser = FirebaseAuth.getInstance().currentUser

        //get prifile ID
        this.profileId = intent.getStringExtra("profileID").toString()

        //
        if(profileId==firebaseUser.uid){
            //view.txtSetting.text="Edit profile"
            checkOwner=true
            friendContainer.visibility=View.GONE
        }
        else {
            checkOwner=false
            profileEdit_container.visibility=View.GONE
            checkFriends()   //check if my wall || friend or not
        }



        btn_chinhsua.setOnClickListener{
            startActivity(Intent(this, ProfileEditting::class.java))
        }
        avatar.setOnClickListener{
            if(uri=="")
                return@setOnClickListener
            val intentFull= Intent(this, FullScreenPictureActivity::class.java)
            intentFull.putExtra("imageuri", uri)
            this.startActivity(intentFull)
        }
        roundedImageView.setOnClickListener{
            if(uriCover=="")
                return@setOnClickListener
            val intentFull= Intent(this, FullScreenPictureActivity::class.java)
            intentFull.putExtra("imageuri", uriCover)
            this.startActivity(intentFull)
        }
        btnAddfriend.setOnClickListener{
            when(statusFriend) {
                "friend" -> {
                    openDialog(Gravity.BOTTOM, this, "deletefriend")

                }
                "dangcho" -> {
                    openDialog(Gravity.BOTTOM, this, "deletedangcho")


                }
                "nofriend" -> {
                    setNotify(profileId)
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Friends").child(it1.toString())
                                .child("friendList").child(profileId)
                                .setValue("pendinginvite")
                    }
                    FirebaseDatabase.getInstance().reference
                            .child("Friends").child(profileId)
                            .child("friendList").child(firebaseUser.uid)
                            .setValue("pendingconfirm")

                    firebaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it.toString())
                            .child("followingList").child(profileId)
                            .setValue(true)
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(profileId)
                        .child("followerList").child(firebaseUser.uid)
                        .setValue(true)
                }
                "xacnhan" -> {
                    deleteNotifyMyself()
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(profileId)
                            .setValue("friend")
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(profileId)
                        .child("friendList").child(firebaseUser.uid)
                        .setValue("friend")
                    firebaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it.toString())
                            .child("followingList").child(profileId)
                            .setValue(true)
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(profileId)
                        .child("followerList").child(firebaseUser.uid)
                        .setValue(true)

                }
            }
        }
        // more
        btn_more.setOnClickListener{
            val bottomSheetFragment = BottomSheetFragmentProfile(this, profileId)
            bottomSheetFragment.show((this as AppCompatActivity).supportFragmentManager, "")
        }
        btn_banbe.setOnClickListener{
            val friendListIntent= Intent(this, FriendListActivity::class.java)
            friendListIntent.putExtra("userID", profileId)
            this?.startActivity(friendListIntent)
        }
        btn_follower.setOnClickListener{
            val followIntent = Intent(this, FollowingListActivity::class.java)
            followIntent.putExtra("userID", profileId)
            followIntent.putExtra("type", "follower")
            this?.startActivity(followIntent)
        }
        btn_following.setOnClickListener{
            val followIntent = Intent(this, FollowingListActivity::class.java)
            followIntent.putExtra("userID", profileId)
            followIntent.putExtra("type", "following")
            this?.startActivity(followIntent)
        }

        btnxemthem.setOnClickListener{
            val moreIntent: Intent = Intent(this, PictureActivity::class.java)
            moreIntent.putExtra("userID", profileId)
            startActivity(moreIntent)

        }

        ShowImagePost1()
        getUserDetailInfor()
        postList= ArrayList()
        imagePostList = ArrayList()
        // ShowImagePost()

        var recycleView : RecyclerView
        recycleView = findViewById(R.id.recycleview_picture_bio)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(this, 2)
        recycleView.setHasFixedSize(true)
        recycleView.layoutManager= linearLayoutManager
        ImageAdapter= this?.let{ ImageProfileAdapter(it, postList as ArrayList<Post> , 350) }
        recycleView.adapter=ImageAdapter


        var recycleview1 : RecyclerView
        recycleview1= findViewById(R.id.recycleview_post_publish1)
        val linearLayoutManager1 = LinearLayoutManager(this)
        //linearLayoutManager1.stackFromEnd=true
        //linearLayoutManager1.reverseLayout=true
        recycleview1.layoutManager= linearLayoutManager1
//        postAdapter= this?.let{ PostAdapter(it, imagePostList as ArrayList<Post> , lstIndex as ArrayList,
//                lstTypeAdapter as ArrayList, shareList as ArrayList , avatarList as ArrayList,
//                coverImageList as ArrayList) }
//        recycleview1.adapter= postAdapter

//        postAdapter= this?.let{ PostProfileAdapter(it, imagePostList as ArrayList<Post> , lstIndex as ArrayList,
//            lstTypeAdapter as ArrayList, shareList as ArrayList) }

        postAdapter=  PostAdapter(this, imagePostList as ArrayList<Post> , lstIndex as ArrayList,
            lstTypeAdapter as ArrayList, shareList as ArrayList , avatarList as ArrayList,
            coverImageList as ArrayList)
        recycleview1.adapter= postAdapter

        //  recycleView.suppressLayout(false)
        // recycleview1.suppressLayout(false)

        getFriends()
        getPicture()
        getInfor()

    }
    fun loadCoverImage(){
        val userInforRef= FirebaseDatabase.getInstance().reference
            .child("UserDetails")
            .child(profileId)
        userInforRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user : UserInfor= UserInfor()
                    uriCover= snapshot.child("coverImage").value.toString()
                    Picasso.get().load(uriCover).into(roundedImageView)

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun deleteNotify(user: String){
       FirebaseDatabase.getInstance().reference
            .child("Notify").child(user).child(firebaseUser.uid).removeValue()
    }
    private fun deleteNotifyMyself(){
        FirebaseDatabase.getInstance().reference
            .child("Notify").child(firebaseUser.uid).child(profileId).removeValue()
    }
    private fun setNotify(userNotifyID : String){
        val notiRef= FirebaseDatabase.getInstance().reference
            .child("Notify").child(userNotifyID)
        val notiMap= HashMap<String, String>()
        //val idpush : String = notiRef.push().key.toString()
        notiMap["userID"]=firebaseUser!!.uid
        notiMap["notify"]="Đã gửi lời mời kết bạn"
        notiMap["postID"]="active"
        notiMap["type"]="loimoiketban"
        notiMap["notifyID"]=firebaseUser!!.uid

        notiRef.child(firebaseUser!!.uid).setValue(notiMap)
    }

    private fun openDialog(gravity: Int, v:Context, type: String) {

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
                removeDangCho()
                deleteNotify(profileId)
            }
            else if(type=="deletefriend"){
                removeFriend()

            }

            dialog.dismiss()
        }

        no.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }
    fun removeDangCho(){
        firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("friendList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("friendList").child(firebaseUser.uid)
            .removeValue()
        firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("followingList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followerList").child(firebaseUser.uid)
            .removeValue()
    }
    fun removeFriend(){
        firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("friendList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("friendList").child(firebaseUser.uid)
            .removeValue()

        firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Friends").child(it.toString())
                .child("followingList").child(profileId)
                .removeValue()
        }
        FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followerList").child(firebaseUser.uid)
            .removeValue()
    }

    private fun getUserDetailInfor(){
        val userDetailRef = FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(profileId)
        userDetailRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val userInfor : UserInfor = UserInfor()
                    userInfor!!.setEducation(snapshot.child("education").child("value").value.toString())
                    userInfor!!.setBio(snapshot.child("bio").value.toString())
                    userInfor!!.setHome(snapshot.child("home").value.toString())
                    userInfor!!.setHomeTown(snapshot.child("homeTown").value.toString())
                    userInfor!!.setJob(snapshot.child("job").value.toString())
                    userInfor!!.setRelationship(snapshot.child("relationship").value.toString())
                    userInfor!!.setWorkPlace(snapshot.child("workPlace").value.toString())

                    //bio
                    if(userInfor.getBio()!="null"){
                        tv_descrip.text=userInfor.getBio()
                    }
                    else{
                        if(profileId==firebaseUser.uid){
                            tv_descrip.text="Thêm giới thiệu cá nhân"
                        }
                        else{
                            tv_descrip.visibility= View.GONE
                        }
                    }

                    // education info
                    if(firebaseUser.uid == profileId){
                        if(userInfor.getEducation()!="null"){
                            if(snapshot.child("education").child("status").value.toString()!="dahoc"){
                                tv_education_profile1.text="Đã học tại " + userInfor.getEducation()
                            }
                            else{
                                tv_education_profile1.text="Đang học tại " + userInfor.getEducation()
                            }
                            ic_education_profile1.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_education_profile1.setImageResource(R.drawable.icon_home_light)
                            //c_education_profile1.setImageResource(R.drawable.btn_addpost)
                        }


                        if(snapshot.child("education1").child("value").value.toString()!="null"){
                            if(snapshot.child("education1").child("status").value.toString()!="dahoc"){
                                tv_education_profile2.text="Đã học tại " + snapshot.child("education1").child("value").value.toString()
                            }
                            else{
                                tv_education_profile2.text="Đang học tại " + snapshot.child("education1").child("value").value.toString()
                            }
                            ic_education_profile2.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_education_profile2.setImageResource(R.drawable.icon_home_light)
                            container_2f.visibility= View.GONE
                        }
                        if(snapshot.child("education2").child("value").value.toString()!="null"){
                            if(snapshot.child("education2").child("status").value.toString()!="dahoc"){
                                tv_education_profile3.text="Đã học tại " + snapshot.child("education2").child("value").value.toString()
                            }
                            else{
                                tv_education_profile3.text="Đang học tại " + snapshot.child("education2").child("value").value.toString()
                            }
                            ic_education_profile3.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_education_profile3.setImageResource(R.drawable.icon_home_light)
                            container_2f.visibility= View.GONE
                            container_3f.visibility= View.GONE

                        }
                        // another info
                        if(userInfor.getHome()!="null"){
                            tv_home_profile.text=userInfor.getHome()
                            ic_home_profile.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_home_profile.setImageResource(R.drawable.icon_home_light)
                        }
                        if(userInfor.getHomeTown()!="null"){
                            tv_hometown_profile.text=userInfor.getHomeTown()
                            ic_hometown_profile.setImageResource(R.drawable.icon_hometown_dart)

                        }
                        else{
                            ic_hometown_profile.setImageResource(R.drawable.icon_hometown_light)
                        }
                        if(userInfor.getRelationship().toString()!="null"){
                            tv_relationship_profile.text=userInfor.getRelationship().toString()
                            ic_relationship_profile.setImageResource(R.drawable.icon_relationship_dart)
                        }
                        else{
                            ic_relationship_profile.setImageResource(R.drawable.icon_relationship_light)
                        }
                        if(userInfor.getJob().toString()!="null"){
                            tv_job_profile.text=userInfor.getJob()
                            ic_job_profile.setImageResource(R.drawable.icon_job)
                        }
                        else{
                            ic_job_profile.setImageResource(R.drawable.icon_job_light)
                        }
                        if(userInfor.getWorkPlace()!="null"){
                            tv_workplace_profile.text=userInfor.getWorkPlace()
                            ic_workplace_profile.setImageResource(R.drawable.icon_workplace_dart)
                        }
                        else{
                            ic_workplace_profile.setImageResource(R.drawable.icon_wordplace_light)
                        }
                        notify.visibility=View.GONE

                        //education
                    }
                    else{
                        var ss= 0
                        if(userInfor.getEducation()!="null" ){
                            ss+=1
                            if(snapshot.child("education").child("status").value.toString()!="dahoc"){
                                tv_education_profile1.text="Đã học tại " + userInfor.getEducation()
                            }
                            else{
                                tv_education_profile1.text="Đang học tại " + userInfor.getEducation()
                            }
                            ic_education_profile1.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            tv_education_profile1.visibility= View.GONE
                            ic_education_profile1.visibility= View.GONE
                        }


                        if(snapshot.child("education1").child("value").value.toString()!="null"){
                            ss+=1

                            if(snapshot.child("education1").child("status").value.toString()!="dahoc"){
                                tv_education_profile2.text="Đã học tại " + snapshot.child("education1").child("value").value.toString()
                            }
                            else{
                                tv_education_profile2.text="Đang học tại " + snapshot.child("education1").child("value").value.toString()
                            }
                            ic_education_profile2.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_education_profile2.visibility= View.GONE
                            tv_education_profile2.visibility= View.GONE
                            container_2f.visibility= View.GONE
                        }
                        if(snapshot.child("education2").child("value").value.toString()!="null"){
                            ss+=1

                            if(snapshot.child("education2").child("status").value.toString()!="dahoc"){
                                tv_education_profile3.text="Đã học tại " + snapshot.child("education2").child("value").value.toString()
                            }
                            else{
                                tv_education_profile3.text="Đang học tại " + snapshot.child("education2").child("value").value.toString()
                            }
                            ic_education_profile3.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            tv_education_profile3.visibility= View.GONE
                            ic_education_profile3.visibility= View.GONE
                            container_2f.visibility= View.GONE
                            container_3f.visibility= View.GONE

                        }
                        // another info
                        if(userInfor.getHome()!="null"){
                            ss+=1

                            tv_home_profile.text=userInfor.getHome()
                            ic_home_profile.setImageResource(R.drawable.icon_home_dart)
                        }
                        else{
                            ic_home_profile.visibility= View.GONE
                            tv_home_profile.visibility= View.GONE
                        }
                        if(userInfor.getHomeTown()!="null"){
                            ss+=1

                            tv_hometown_profile.text=userInfor.getHomeTown()
                            ic_hometown_profile.setImageResource(R.drawable.icon_hometown_dart)

                        }
                        else{
                            ic_hometown_profile.visibility= View.GONE
                            tv_hometown_profile.visibility= View.GONE
                        }
                        if(userInfor.getRelationship().toString()!="null"){
                            ss+=1

                            tv_relationship_profile.text=userInfor.getRelationship().toString()
                            ic_relationship_profile.setImageResource(R.drawable.icon_relationship_dart)
                        }
                        else{
                            ic_relationship_profile.visibility= View.GONE
                            tv_relationship_profile.visibility= View.GONE
                        }
                        if(userInfor.getJob().toString()!="null"){
                            ss+=1

                            tv_job_profile.text=userInfor.getJob()
                            ic_job_profile.setImageResource(R.drawable.icon_job)
                        }
                        else{
                            tv_job_profile.visibility= View.GONE
                            ic_job_profile.visibility= View.GONE
                        }
                        if(userInfor.getWorkPlace()!="null"){
                            ss+=1

                            tv_workplace_profile.text=userInfor.getWorkPlace()
                            ic_workplace_profile.setImageResource(R.drawable.icon_workplace_dart)
                        }
                        else{
                            tv_workplace_profile.visibility= View.GONE
                            ic_workplace_profile.visibility= View.GONE
                        }
                        if(ss==0){
                            notify.visibility=View.VISIBLE
                        }else{
                            notify.visibility=View.GONE
                        }

                        //education
                    }



                }
                else{
                    tv_workplace_profile.visibility= View.GONE
                    ic_workplace_profile.visibility= View.GONE
                    tv_job_profile.visibility= View.GONE
                    ic_job_profile.visibility= View.GONE
                    ic_relationship_profile.visibility= View.GONE
                    tv_relationship_profile.visibility= View.GONE
                    ic_hometown_profile.visibility= View.GONE
                    tv_hometown_profile.visibility= View.GONE
                    ic_home_profile.visibility= View.GONE
                    tv_home_profile.visibility= View.GONE
                    tv_education_profile3.visibility= View.GONE
                    ic_education_profile3.visibility= View.GONE
                    container_2f.visibility= View.GONE
                    container_3f.visibility= View.GONE
                    ic_education_profile2.visibility= View.GONE
                    tv_education_profile2.visibility= View.GONE
                    container_2f.visibility= View.GONE
                    tv_education_profile1.visibility= View.GONE
                    ic_education_profile1.visibility= View.GONE
                    notify.visibility=View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun ShowImagePost1(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    shareList!!.clear()
                    (imagePostList as ArrayList<Post>).clear()
                    (lstIndex as ArrayList).clear()
                    (lstTypeAdapter as ArrayList).clear()
                    var ind1 = 0
                    var ind2 = 0
                    var ind3=0
                    var ind4=0
                    for (s in snapshot.child("ProfileTimeLine")
                            .child(profileId).children) {
                        val tl = s.getValue(TimelineContent::class.java)
                        tl!!.setPostType(s.child("post_type").value.toString())
                        if (tl!!.getPostType() == "sharepost") {
                            if(snapshot.child("Share Posts").child(tl.getId()).exists()){
                                var sharePost = snapshot.child("Share Posts").child(tl.getId()).getValue<SharePost>(SharePost::class.java)
                                shareList!!.add(sharePost!!)
                                (lstTypeAdapter as ArrayList).add(1)
                                (lstIndex as ArrayList).add(ind1)
                                ind1 += 1
                            }

                        } else if (tl!!.getPostType() == "post") {


                            val post = snapshot.child("Posts").child(tl.getId()).getValue(Post::class.java)
                            (imagePostList as ArrayList<Post>).add(post!!)
                            (lstTypeAdapter as ArrayList).add(0)
                            (lstIndex as ArrayList).add(ind2)
                            ind2 += 1
                        }
                        else if (tl!!.getPostType()=="changeavatar"){
                            val avatarPost = snapshot.child("AvatarPost").child(tl.getId()).getValue(Post::class.java)
                            avatarPost!!.setpostContent(snapshot.child("AvatarPost").child(tl.getId()).child("post_content")
                                .value.toString())


                            avatarList!!.add(avatarPost!!)
                            (lstTypeAdapter as ArrayList).add(2)
                            (lstIndex as ArrayList).add(ind3)
                            ind3+=1
                        }
                        else if(tl!!.getPostType()=="changecover"){
                            val coverImagePost = snapshot.child("CoverPost").child(tl.getId()).getValue(Post::class.java)
                            coverImagePost!!.setpostContent(snapshot.child("CoverPost").child(tl.getId()).child("post_content").value.toString())
                            coverImageList!!.add(coverImagePost!!)
                            (lstTypeAdapter as ArrayList).add(3)
                            (lstIndex as ArrayList).add(ind4)
                            ind4+=1
                        }
                        //  getPostAndShare()
                        postAdapter!!.notifyDataSetChanged()
                    }
                }

            }
        })

    }
    private fun showImagePost() {
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object: ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                (imagePostList as ArrayList<Post>).clear()
                for(ss in snapshot.children){
                    if(ss.exists())
                    {
                        val post = ss.getValue(Post::class.java)
                        //    val post = Post()
                        post!!.setpost_image(ss.child("post_image").value.toString())
                        post!!.setpostContent(ss.child("post_content").value.toString())
                        post.setpost_id(ss.child("post_id").value.toString())

                        if(post!!.getpublisher()==firebaseUser.uid){
                            (imagePostList as ArrayList<Post>).add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun getPicture() {
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents").child("Posts")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (postList as ArrayList<Post>).clear()
                    for (s in snapshot.children){
                        val post= s.getValue(Post::class.java)
                        post!!.setpost_id(s.child("post_id").value.toString())
                        post!!.setpublisher(s.child("publisher").value.toString())
                        if(post.getpublisher()== profileId){
                            if((postList as ArrayList<Post>).size<4)
                            (postList as ArrayList<Post>).add(post!!)
                        }
                        //Collections.reverse(postList)
                        ImageAdapter!!.notifyDataSetChanged()
                    }
                    if((postList as ArrayList<Post>).size==4)
                    {
                        container_x1.visibility=View.VISIBLE
                    }
                    else{
                        container_x1.visibility=View.GONE
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getInfor(){
        // get name methods 1
        val nameRef= FirebaseDatabase.getInstance().reference
                .child("UserInformation").child(profileId)
        nameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user =snapshot.getValue<User>(User::class.java)
                print("test")
                user?.setEmail(snapshot.child("email").value.toString())
                user?.setName(snapshot.child("fullname").value.toString())
                txt_name_avatar.text=user?.getName()
                username_toolbar.text=user?.getName()
                uri= user!!.getAvatar()
                Picasso.get().load(user?.getAvatar()).into(avatar)
                Picasso.get().load(user?.getAvatar()).into(avatar_intoolbar)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        // get name methods 2

    }
    private fun checkFriends() {
        val friendref= FirebaseDatabase.getInstance().reference
                .child("Friends").child(firebaseUser.uid)
                .child("friendList")

        if(friendref!=null){
            friendref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        if(snapshot.child(profileId).value=="friend")
                        {
                            btnAddfriend.text="Bạn bè"
                            statusFriend="friend"
                            btnAddfriend.setBackgroundResource(R.drawable.custom_button_whi)
                            btnAddfriend.setTextColor(Color.parseColor("#03A9F4"))

                        }
                        else if(snapshot.child(profileId).value=="pendingconfirm"){
                            btnAddfriend.text="Xác nhận"
                            statusFriend="xacnhan"
                            //btnAddfriend.setTextColor(Color.parseColor("#00BCD4"))
                            btnAddfriend.setBackgroundResource(R.drawable.custom_btn_whi_blue)
                            btnAddfriend.setTextColor(Color.parseColor("#FFFFFF"))

                        }
                        else if(snapshot.child(profileId).value=="pendinginvite"){
                            btnAddfriend.text="Đang chờ"
                            btnAddfriend.setTextColor(Color.parseColor("#858585"))
                            statusFriend="dangcho"
                            btnAddfriend.setBackgroundResource(R.drawable.custom_button_whi)
                            btnAddfriend.setTextColor(Color.parseColor("#03A9F4"))
                        }

                    }
                    else{
                        btnAddfriend.text="Kết bạn"
                        statusFriend="nofriend"
                        btnAddfriend.setBackgroundResource(R.drawable.custom_btn_whi_blue)
                        btnAddfriend.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    private fun getFriends(){
        val friendsref= FirebaseDatabase.getInstance().reference
                .child("Friends").child(profileId)
                .child("friendList")

        friendsref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var sum=0
                    for (s in snapshot.children){
                        if(s.value.toString()=="friend"){
                            sum+=1
                        }
                    }

                    numbanbe?.text=sum.toString()

                }
                else {
                    numbanbe?.text="0"
                }
            }
        })
        val followList= FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followerList")

        followList.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){


                    numfollower?.text=snapshot.childrenCount.toString()

                }
                else {
                    numfollower?.text="0"
                }
            }
        })

        val followinglist= FirebaseDatabase.getInstance().reference
            .child("Friends").child(profileId)
            .child("followingList")

        followinglist.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){


                    numfollowing?.text=snapshot.childrenCount.toString()

                }
                else {
                    numfollowing?.text="0"
                }
            }
        })
    }
}