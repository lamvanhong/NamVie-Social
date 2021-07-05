
package com.lamhong.viesocial.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.*
import com.lamhong.viesocial.Adapter.ImageProfileAdapter
import com.lamhong.viesocial.Adapter.PostAdapter
import com.lamhong.viesocial.Models.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileFragment : Fragment() {
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

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        //get friendlist
   /*     firebaseUser =FirebaseAuth.getInstance().currentUser

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null){
            this.profileId= pref.getString("profileId", "none").toString()

        }
        if(profileId==firebaseUser.uid){
            //view.txtSetting.text="Edit profile"
            checkOwner=true
            view.friendContainer.visibility=View.GONE
        }
        else {
            checkOwner=false
            view.profileEdit_container.visibility=View.GONE
            checkFriends()   //check if my wall || friend or not
        }


        view.btn_setting.setOnClickListener {
            startActivity(Intent(context, AccountSettingActivity::class.java))
        }
        view.btn_chinhsua.setOnClickListener{
            startActivity(Intent(context, ProfileEditting::class.java))
        }
        view.btnAddfriend.setOnClickListener{
            when(statusFriend){
                "friend"->{
                    firebaseUser?.uid.let{it->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it.toString())
                            .child("friendList").child(profileId)
                            .removeValue()
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(profileId)
                        .child("friendList").child(firebaseUser.uid)
                        .removeValue()
                }
                "nofriend"->{
                    firebaseUser?.uid.let{it1->
                        FirebaseDatabase.getInstance().reference
                            .child("Friends").child(it1.toString())
                            .child("friendList").child(profileId)
                            .setValue("pendinginvite")
                    }
                    FirebaseDatabase.getInstance().reference
                        .child("Friends").child(profileId)
                        .child("friendList").child(firebaseUser.uid)
                        .setValue("pendingconfirm")
                }
            }


        }
        view.btn_friendList.setOnClickListener{
            val friendListIntent= Intent(context, FriendListActivity::class.java)
            friendListIntent.putExtra("userID", profileId)
            context?.startActivity(friendListIntent)

        }
        view.btnxemthem.setOnClickListener{
            val moreIntent: Intent = Intent(context, PictureActivity::class.java)
            startActivity(moreIntent)

        }


       // ShowImagePost()
        var recycleView : RecyclerView
        recycleView = view.findViewById(R.id.recycleview_picture_bio)
        val linearLayoutManager: LinearLayoutManager= GridLayoutManager(context, 2)

        var recycleview1 : RecyclerView
        recycleview1= view.findViewById(R.id.recycleview_post_publish)
        val linearLayoutManager1 = LinearLayoutManager(context)
        linearLayoutManager1.stackFromEnd=true
        linearLayoutManager1.reverseLayout=true
        recycleview1.layoutManager= linearLayoutManager1


        ShowImagePost1()
        getUserDetailInfor()
      //  recycleView.suppressLayout(false)
       // recycleview1.suppressLayout(false)
        recycleView.setHasFixedSize(true)

        recycleView.layoutManager= linearLayoutManager

        postList= ArrayList()
         ImageAdapter= context?.let{ ImageProfileAdapter(it, postList as ArrayList<Post> , 300)}
        recycleView.adapter=ImageAdapter

        imagePostList = ArrayList()
        postAdapter= context?.let{ PostAdapter(it, imagePostList as ArrayList<Post> , lstIndex as ArrayList,
                lstTypeAdapter as ArrayList, shareList as ArrayList , avatarList as ArrayList,
            coverImageList as ArrayList) }
        recycleview1.adapter= postAdapter



        txtSetting

        getFriends()
        getPicture()
        getInfor()*/


        // some func

        return view;

    }
    private fun getUserDetailInfor(){
        val userDetailRef = FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(firebaseUser.uid!!)
        userDetailRef.addValueEventListener(object: ValueEventListener{
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
                            tv_descrip.visibility=View.GONE
                        }
                    }

                    // education info
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
                        container_2f.visibility=View.GONE
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
                        container_2f.visibility=View.GONE
                        container_3f.visibility=View.GONE

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


                    //education


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun ShowImagePost1(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents")
        postRef.addValueEventListener(object:  ValueEventListener{
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
                    for (s in snapshot.child("ProfileTimeLine")
                        .child(firebaseUser.uid).children) {
                        val tl = s.getValue(TimelineContent::class.java)
                        tl!!.setPostType(s.child("post_type").value.toString())
                        if (tl!!.getPostType() == "sharepost") {

                            var sharePost = snapshot.child("Share Posts").child(tl.getId()).getValue<SharePost>(SharePost::class.java)
                            shareList!!.add(sharePost!!)
                            (lstTypeAdapter as ArrayList).add(1)
                            (lstIndex as ArrayList).add(ind1)
                            ind1 += 1
                        } else if (tl!!.getPostType() == "post") {


                            val post = snapshot.child("Posts").child(tl.getId()).getValue(Post::class.java)
                            (imagePostList as ArrayList<Post>).add(post!!)
                            (lstTypeAdapter as ArrayList).add(0)
                            (lstIndex as ArrayList).add(ind2)
                            ind2 += 1
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
        postRef.addValueEventListener(object: ValueEventListener{


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
    private fun getPicture(){
        val postRef=FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (postList as ArrayList<Post>).clear()
                    for (s in snapshot.children){
                        val post= s.getValue(Post::class.java)
                        post!!.setpost_id(s.child("post_id").value.toString())
                        if((postList as ArrayList<Post>).size<4)
                        (postList as ArrayList<Post>).add(post!!)

                        //Collections.reverse(postList)
                        ImageAdapter!!.notifyDataSetChanged()
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
        nameRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user =snapshot.getValue<User>(User::class.java)
                print("test")
                user?.setEmail(snapshot.child("email").value.toString())
                user?.setName(snapshot.child("fullname").value.toString())
                txt_name_avatar.text=user?.getName()
                Picasso.get().load(user?.getAvatar()).placeholder(R.drawable.duongtu1).into(avatar)
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
            friendref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        view!!.btnAddfriend.text="Bạn bè"
                        statusFriend="friend"
                    }
                    else{
                        view!!.btnAddfriend.text="Kết bạn"
                        statusFriend="nofriend"
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

        friendsref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.tv_numberFriends?.text=snapshot.childrenCount.toString()
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

   /* override fun onStart() {
        super.onStart()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }*/
}