package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lamhong.viesocial.*
import com.lamhong.viesocial.Models.*
import com.lamhong.viesocial.Network.MyFirebaseMessagingService
import com.lamhong.viesocial.Utilities.Constants
import com.lamhong.viesocial.Utilities.Constants.Companion.doSendNotify
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

val TOPICC ="/topics/myTopic"
class PostAdapter (private val mcontext: Context, private val mPost : List<Post> ,
                   private val mLstIndex: List<Int> , private val mLstType: List<Int>,
                   private val mShare: List<SharePost>, private val mAvatarList : List<Post>,
                    private val mCoverImageList : List<Post>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var firebaseUser : FirebaseUser?=null
    var uri: String=""
    inner class ViewHolder0(@NonNull itemVIew: View): RecyclerView.ViewHolder(itemVIew){
        var btn_option : ImageView = itemView.findViewById(R.id.btn_option)
        var postImage :ImageView
        var profileImage : CircleImageView
        var userName: TextView
        var numlikes: TextView= itemView.findViewById(R.id.numlikes)
        val numcomment: TextView= itemView.findViewById(R.id.numbinhluan)
        val numshare: TextView= itemView.findViewById(R.id.numshare)
        var describe: TextView = itemView.findViewById(R.id.describe)


        //new
        var btnLike: ImageButton = itemView.findViewById(R.id.btn_yeuthich)
        var btnComment: ImageButton = itemView.findViewById(R.id.btn_binhluan)
        var tvthich: TextView = itemView.findViewById(R.id.tv_thich)
        var btnShare : ImageButton= itemView.findViewById(R.id.btn_share)
        var tv_typePost: TextView = itemView.findViewById(R.id.tv_typePost)
        init {
            postImage = itemView.findViewById(R.id.post_image_home)
            profileImage = itemView.findViewById(R.id.user_profile_image_search)

            userName = itemView.findViewById(R.id.user_name_search)
        }
    }

    inner class ViewHolder1(@NonNull itemVIew: View) : RecyclerView.ViewHolder(itemVIew){
        val numshare: TextView= itemView.findViewById(R.id.numshare)
        val btn_option : ImageView = itemView.findViewById(R.id.btn_option)
        var postImage: ImageView
        var avatar_sharing : CircleImageView
        var avatar_shared: CircleImageView
        var name_sharing : TextView
        var name_shared: TextView
        var content_sharing: TextView
        var content_shared: TextView
        var numlike: TextView
        var numComment: TextView
        var tvthich: TextView


        var btnLike : ImageButton
        var btnComment: ImageButton
        var btnShare: ImageButton

        var avatar_sharelayout: CircleImageView
        var container_avatar_sharelayout: RelativeLayout
        var tv_displaytype: TextView
        var tv_displaytypesharer: TextView

        init {
            postImage= itemView.findViewById(R.id.image_content)
            avatar_sharing=itemView.findViewById(R.id.user_avata_sharing)
            avatar_shared= itemView.findViewById(R.id.user_avatar_shared)
            name_sharing= itemView.findViewById(R.id.user_name_sharing)
            name_shared= itemView.findViewById(R.id.user_name_shared)
            content_sharing=itemView.findViewById(R.id.describeShare)
            content_shared= itemView.findViewById(R.id.content_inshared)
            numlike=itemView.findViewById(R.id.numlikes)
            numComment=itemView.findViewById(R.id.numbinhluan)
            btnLike=itemView.findViewById(R.id.btn_yeuthich)
            btnComment=itemView.findViewById(R.id.btn_binhluan)
            btnShare=itemView.findViewById(R.id.btn_share)
            tvthich= itemView.findViewById(R.id.tv_thich)

            avatar_sharelayout=itemView.findViewById(R.id.avatar_sharelayout)
            container_avatar_sharelayout=itemView.findViewById(R.id.container_avatar_sharelayout)
            tv_displaytype=itemView.findViewById(R.id.tv_displaytype)
            tv_displaytypesharer=itemView.findViewById(R.id.tv_showtype)
        }
    }

    inner class ViewHolder2(@NonNull itemVIew: View): RecyclerView.ViewHolder(itemVIew){
        // this vh for avatar post
        val numshare: TextView= itemView.findViewById(R.id.numshare)
        val btn_option : ImageView = itemView.findViewById(R.id.btn_option)
        var avatarImage :CircleImageView
        var profileImage : CircleImageView
        var userName: TextView
        var numlikes: TextView= itemView.findViewById(R.id.numlikes)
        val numcomment: TextView= itemView.findViewById(R.id.numbinhluan)
        var describe: TextView = itemView.findViewById(R.id.describe)


        //new
        var btnLike: ImageButton = itemView.findViewById(R.id.btn_yeuthich)
        var btnComment: ImageButton = itemView.findViewById(R.id.btn_binhluan)
        var tvthich: TextView = itemView.findViewById(R.id.tv_thich)
        var btnShare : ImageButton= itemView.findViewById(R.id.btn_share)
        var tv_typePost: TextView = itemView.findViewById(R.id.tv_typePost)
        init {
            avatarImage = itemView.findViewById(R.id.avatar_item)
            profileImage = itemView.findViewById(R.id.user_profile_image_search)

            userName = itemView.findViewById(R.id.user_name_search)
        }
    }

    override fun getItemViewType(position: Int): Int {
       // return super.getItemViewType(position)
        return mLstType[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){
            0 ->{
                val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
                return ViewHolder0(view)
            }
            1->{
                val view = LayoutInflater.from(mcontext).inflate(R.layout.post_share_layout, parent, false)
                return ViewHolder1(view)
            }
            2->{
                val view = LayoutInflater.from(mcontext).inflate(R.layout.avatar_layout, parent, false)
                return ViewHolder2(view)
            }
            3->{
                val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
                return ViewHolder0(view)
            }
        }
        val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder0(view)

    }

    override fun onBindViewHolder(holderc: RecyclerView.ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser


        //getPostAndShare()
        when(holderc.getItemViewType()){
            0->{


                val holder1 : ViewHolder0 = holderc as ViewHolder0

                holder1.tv_typePost.visibility= View.GONE
                val post= mPost[mLstIndex[position]]



                //Notification
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if(it.isComplete){
                        val fbToken = it.result.toString()
                        MyFirebaseMessagingService.token =fbToken
                    }
                }
                var nameuser =""
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
                userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            nameuser= snapshot.child("fullname").value.toString()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                var token =""
                val userRef2 = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(post.getpublisher())
                userRef2.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            token = (snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                //End notification
                //navigator to user

                //nav to profile
                holder1.profileImage.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.userName.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                // end nav to profile

                holder1.btn_option.setOnClickListener{
                    val bottomSheetFragment = BottomSheetFragment(mcontext,"post", post.getpost_id(), post.getpublisher())
                    bottomSheetFragment.show((mcontext as AppCompatActivity).supportFragmentManager, "")
                }
                Picasso.get().load(post.getpost_image()).into(holder1.postImage)

                publishInfo(holder1.profileImage, holder1.userName,  post.getpublisher())
                //describe import
                if(post.getpostContent()=="" || post.getpostContent()=="null" || post.getpostContent()==null){
                    holder1.describe.visibility=View.GONE
                }else{
                    holder1.describe.visibility=View.VISIBLE
                    holder1.describe.text=post.getpostContent()
                }

                checkLikes(post.getpost_id(), holder1.btnLike , holder1.tvthich)
                setnumberLike(holder1.numlikes,post.getpost_id())
                setComment(holder1.numcomment, post.getpost_id())
                setShareNumber(holder1.numshare, post.getpost_id())
                holder1.numlikes.setOnClickListener{
                    val intent = Intent(mcontext, UserReacted::class.java)
                    intent.putExtra("postID", post.getpost_id())
                    mcontext.startActivity(intent)
                }
                holder1.btnLike.setOnClickListener{
                    //Toast.makeText(mcontext, "honghong", Toast.LENGTH_LONG).show()
                   // Toast.makeText(mcontext.applicationContext, "Đã lưu bài viết" , Toast.LENGTH_LONG).show()

                    if(holder1.btnLike.tag=="Like"){
                        doSendNotify(nameuser, token, "đã thích bài viết của bạn")
                        addNotifyLike(post.getpublisher(), post.getpost_id() , "thichbaiviet")
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .setValue(true)

                        // set to activities
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="post"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="like"
                        pMap["timestamp"]=System.currentTimeMillis()
                        activityRef.push().setValue(pMap)

                    }else
                    {
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                        //  val intent=Intent(mcontext,zHome::class.java)
                        // mcontext.startActivity(intent)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="post"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="unlike"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)

                    }
                }
                holder1.btnComment.setOnClickListener{
                    val commentIntent = Intent(mcontext, CommentActivity::class.java)
                    commentIntent.putExtra("postID", post.getpost_id())
                    commentIntent.putExtra("publisher", post.getpublisher())
                    commentIntent.putExtra("type", "post")
                    mcontext.startActivity(commentIntent)
                }
                holder1.btnShare.setOnClickListener{
                    val shareIntent = Intent(mcontext, SharePostActivity::class.java)
                    shareIntent.putExtra("postID", post.getpost_id())
                    shareIntent.putExtra("publisher", post.getpublisher())
                    shareIntent.putExtra("type", "post")
                    mcontext.startActivity(shareIntent)
                }
                holder1.postImage.setOnClickListener{
                    val intentFull= Intent(mcontext, FullScreenPictureActivity::class.java)
                    intentFull.putExtra("imageuri", post.getpost_image())
                    mcontext.startActivity(intentFull)
                }
                holder1.postImage.setOnClickListener{

                    val intentFull= Intent(mcontext, FullScreenPictureActivity::class.java)
                    intentFull.putExtra("imageuri", post.getpost_image())
                    mcontext.startActivity(intentFull)
                }
            }
            1->{

                // share option
                val holder1= holderc as ViewHolder1
                val sharePost = mShare[mLstIndex[position]]

                //Notification
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if(it.isComplete){
                        val fbToken = it.result.toString()
                        MyFirebaseMessagingService.token =fbToken
                    }
                }
                var nameuser =""
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
                userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            nameuser= snapshot.child("fullname").value.toString()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                var token =""
                val userRef2 = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(sharePost.getPublisher())
                userRef2.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            token = (snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                //End notification
                //nav to profile
                holder1.avatar_sharing.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.name_sharing.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.avatar_shared.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPostOwner())
                    mcontext.startActivity(userIntent)
                }
                holder1.name_shared.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPostOwner())
                    mcontext.startActivity(userIntent)
                }
                // end nav to profile

                //navigator to
                holder1.avatar_sharing.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPublisher())
                     mcontext.startActivity(userIntent)
                }
                holder1.name_sharing.setOnClickListener {
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", sharePost.getPublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.btn_option.setOnClickListener{
                    val bottomSheetFragment = BottomSheetFragment(mcontext,"sharepost", sharePost.getShareID() , sharePost.getPublisher())
                    bottomSheetFragment.show((mcontext as AppCompatActivity).supportFragmentManager, "")
                }

                //basic

                holder1.content_sharing.text=sharePost.getContent()
                publishInfo(holder1.avatar_sharing, holder1.name_sharing,sharePost.getPublisher())

                getPost(sharePost.getPostID(), holder1.postImage, holder1.avatar_shared,
                    holder1.content_shared, holder1.name_shared , sharePost.getType()
                , holder1.container_avatar_sharelayout, holder1.avatar_sharelayout, holder1.tv_displaytype)



                // like share
                checkLikes(sharePost.getShareID(), holder1.btnLike , holder1.tvthich)
                setnumberLike(holder1.numlike, sharePost.getShareID())
                setComment(holder1.numComment, sharePost.getShareID())
                holder1.numlike.setOnClickListener{
                    val intent = Intent(mcontext, UserReacted::class.java)
                    intent.putExtra("postID", sharePost.getShareID())
                    mcontext.startActivity(intent)
                }

                holder1.btnLike.setOnClickListener{
                    if(holder1.btnLike.tag=="Like"){
                        doSendNotify(nameuser, token, "đã thích bài chia sẻ của bạn")
                        when(sharePost.getType()){
                            "cover" ->{
                                addNotifyLike(sharePost.getPublisher(), sharePost.getShareID() , "thichbaishare_cover")
                            }
                            "avatar"->{
                                addNotifyLike(sharePost.getPublisher(), sharePost.getShareID() , "thichbaishare_avatar")
                            }
                            else->{
                                addNotifyLike(sharePost.getPublisher(), sharePost.getShareID() , "thichbaishare")
                            }
                        }
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(sharePost.getShareID())
                            .child(firebaseUser!!.uid)
                            .setValue(true)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="sharepost"
                        pMap["id"]=sharePost.getShareID()
                        pMap["active"]=true
                        pMap["type"]="likeshare"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)

                    }else
                    {
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(sharePost.getShareID())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                        //  val intent=Intent(mcontext,zHome::class.java)
                        // mcontext.startActivity(intent)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="sharepost"
                        pMap["id"]=sharePost.getShareID()
                        pMap["active"]=true
                        pMap["type"]="unlikeshare"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)

                    }
                }
                holder1.btnShare.setOnClickListener{
                    val shareIntent = Intent(mcontext, SharePostActivity::class.java)
                    shareIntent.putExtra("postID", sharePost.getPostID())
                    shareIntent.putExtra("publisher", sharePost.getPostOwner())
                    shareIntent.putExtra("type", sharePost.getType())
                    mcontext.startActivity(shareIntent)
                }
                holder1.btnComment.setOnClickListener{
                    val commentIntent = Intent(mcontext, CommentShareActivity::class.java)
                    commentIntent.putExtra("shareID", sharePost.getShareID())
                    commentIntent.putExtra("postID", sharePost.getPostID())
                    commentIntent.putExtra("publisher", sharePost.getPublisher())
                    commentIntent.putExtra("postowner", sharePost.getPostOwner())// fix
                    commentIntent.putExtra("content", sharePost.getContent())
                    commentIntent.putExtra("type", sharePost.getType())
                    mcontext.startActivity(commentIntent)
                }



            }
            2->{
                val holder1 : ViewHolder2 = holderc as ViewHolder2


                val post= mAvatarList[mLstIndex[position]]

                //Notification
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if(it.isComplete){
                        val fbToken = it.result.toString()
                        MyFirebaseMessagingService.token =fbToken
                    }
                }
                var nameuser =""
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
                userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            nameuser= snapshot.child("fullname").value.toString()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                var token =""
                val userRef2 = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(post.getpublisher())
                userRef2.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            token = (snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                //End notification

                //nav to profile
                holder1.profileImage.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.userName.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                // end nav to profile


                holder1.btn_option.setOnClickListener{
                    val bottomSheetFragment = BottomSheetFragment(mcontext,"changeavatar", post.getpost_id(), post.getpublisher())
                    bottomSheetFragment.show((mcontext as AppCompatActivity).supportFragmentManager, "")
                }

                Picasso.get().load(post.getpost_image()).into(holder1.avatarImage)

                publishInfo(holder1.profileImage, holder1.userName,  post.getpublisher())
                //describe import
                if(post.getpostContent().equals("")){
                    holder1.describe.visibility=View.GONE
                }else{
                    holder1.describe.visibility=View.VISIBLE
                    holder1.describe.text=post.getpostContent()
                }

                checkLikes(post.getpost_id(), holder1.btnLike , holder1.tvthich)
                setnumberLike(holder1.numlikes,post.getpost_id())
                setComment(holder1.numcomment, post.getpost_id())
                holder1.numlikes.setOnClickListener{
                    val intent = Intent(mcontext, UserReacted::class.java)
                    intent.putExtra("postID", post.getpost_id())
                    mcontext.startActivity(intent)
                }
                holder1.btnLike.setOnClickListener{
                    if(holder1.btnLike.tag=="Like"){
                        doSendNotify(nameuser, token, "đã thích ảnh đại diện của bạn")
                        addNotifyLike(post.getpublisher(), post.getpost_id() , "changeavatar")
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .setValue(true)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="changeavatar"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="likeavatar"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)
                    }else
                    {
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                        //  val intent=Intent(mcontext,zHome::class.java)
                        // mcontext.startActivity(intent)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="changeavatar"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="unlikeavatar"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)

                    }
                }
                holder1.btnComment.setOnClickListener{
                    val commentIntent = Intent(mcontext, CommentActivity::class.java)
                    commentIntent.putExtra("postID", post.getpost_id())
                    commentIntent.putExtra("publisher", post.getpublisher())
                    commentIntent.putExtra("type", "avatar")
                    mcontext.startActivity(commentIntent)
                }
                holder1.btnShare.setOnClickListener{
                    val shareIntent = Intent(mcontext, SharePostActivity::class.java)
                    shareIntent.putExtra("postID", post.getpost_id())
                    shareIntent.putExtra("publisher", post.getpublisher())
                    shareIntent.putExtra("type", "avatar")
                    mcontext.startActivity(shareIntent)
                }
                holder1.avatarImage.setOnClickListener{
                    val intentFull= Intent(mcontext, FullScreenPictureActivity::class.java)
                    intentFull.putExtra("imageuri", post.getpost_image())
                    mcontext.startActivity(intentFull)
                }
            }
            3->{
                val holder1 : ViewHolder0 = holderc as ViewHolder0

                holder1.tv_typePost.visibility= View.VISIBLE
                val post= mCoverImageList[mLstIndex[position]]

                //Notification
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if(it.isComplete){
                        val fbToken = it.result.toString()
                        MyFirebaseMessagingService.token =fbToken
                    }
                }
                var nameuser =""
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
                userRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            nameuser= snapshot.child("fullname").value.toString()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                var token =""
                val userRef2 = FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(post.getpublisher())
                userRef2.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            token = (snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                //End notification

                //nav to profile
                holder1.profileImage.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                holder1.userName.setOnClickListener{
                    val userIntent = Intent(mcontext, ProfileActivity::class.java)
                    userIntent.putExtra("profileID", post.getpublisher())
                    mcontext.startActivity(userIntent)
                }
                // end nav to profile

                Picasso.get().load(post.getpost_image()).into(holder1.postImage)

                publishInfo(holder1.profileImage, holder1.userName,  post.getpublisher())
                //describe import
                if(post.getpostContent()==""){
                    holder1.describe.visibility=View.GONE
                }else{
                    holder1.describe.visibility=View.VISIBLE
                    holder1.describe.text=post.getpostContent()
                }

                checkLikes(post.getpost_id(), holder1.btnLike , holder1.tvthich)
                setnumberLike(holder1.numlikes,post.getpost_id())
                setComment(holder1.numcomment, post.getpost_id())
                holder1.numlikes.setOnClickListener{
                    val intent = Intent(mcontext, UserReacted::class.java)
                    intent.putExtra("postID", post.getpost_id())
                    mcontext.startActivity(intent)
                }
                holder1.btnLike.setOnClickListener{
                    if(holder1.btnLike.tag=="Like"){
                        doSendNotify(nameuser, token, "đã thích ảnh bìa của bạn")
                        addNotifyLike(post.getpublisher(), post.getpost_id() , "changecover")
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .setValue(true)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="changecover"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="likecover"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)
                    }else
                    {
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                        //  val intent=Intent(mcontext,zHome::class.java)
                        // mcontext.startActivity(intent)

                        // set to activity
                        val activityRef= FirebaseDatabase.getInstance().reference.child("Contents")
                            .child("Activities").child(FirebaseAuth.getInstance().currentUser.uid)
                        val pMap = HashMap<String, Any>()
                        pMap["post_type"]="changecover"
                        pMap["id"]=post.getpost_id()
                        pMap["active"]=true
                        pMap["type"]="unlikecover"
                        pMap["timestamp"]=System.currentTimeMillis().toString()
                        activityRef.push().setValue(pMap)

                    }
                }
                holder1.btn_option.setOnClickListener{
                    val bottomSheetFragment = BottomSheetFragment(mcontext,"changecover", post.getpost_id(), post.getpublisher())
                    bottomSheetFragment.show((mcontext as AppCompatActivity).supportFragmentManager, "")
                }
                holder1.btnComment.setOnClickListener{
                    val commentIntent = Intent(mcontext, CommentActivity::class.java)
                    commentIntent.putExtra("postID", post.getpost_id())
                    commentIntent.putExtra("publisher", post.getpublisher())
                    commentIntent.putExtra("type", "cover")
                    mcontext.startActivity(commentIntent)
                }
                holder1.btnShare.setOnClickListener{
                    val shareIntent = Intent(mcontext, SharePostActivity::class.java)
                    shareIntent.putExtra("postID", post.getpost_id())
                    shareIntent.putExtra("publisher", post.getpublisher())
                    shareIntent.putExtra("type", "cover")
                    mcontext.startActivity(shareIntent)
                }
                holder1.postImage.setOnClickListener{
                    val intentFull= Intent(mcontext, FullScreenPictureActivity::class.java)
                    intentFull.putExtra("imageuri", post.getpost_image())
                    mcontext.startActivity(intentFull)
                }
            }
        }

    }

    private fun setShareNumber(numshare: TextView, getpostId: String) {
         val ref= FirebaseDatabase.getInstance().reference.child("Contents").child("Share Posts")
        ref.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var xx=0
                    for (s in snapshot.children){
                        if(s.child("postID").value.toString()== getpostId){
                            xx+=1
                        }
                    }
                    if(xx>0){
                        numshare.text=xx.toString()+ " chia sẻ"
                        numshare.visibility= View.VISIBLE
                    }
                    else{
                        numshare.visibility= View.GONE
                    }
                }
                else {
                    numshare.visibility= View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getPost(id: String, postImage: ImageView, avatar_shared: CircleImageView,
            content_shared: TextView , name_shared : TextView , type: String,
            container : RelativeLayout, avatar: CircleImageView, tv_displaytype: TextView){
        var path=""
        if(type=="avatar"){
            path="AvatarPost"
            tv_displaytype.text="đã cập nhật ảnh đại diện"

        }
        else if(type=="cover"){
            path="CoverPost"
            tv_displaytype.text="đã thay đổi ảnh bìa"
        }
        else{
            path="Posts"
            tv_displaytype.text="đã đăng bài viết"

        }
        val postRef= FirebaseDatabase.getInstance().reference.child("Contents").child(path).child(id)
        var post: Post ?=null
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(ss: DataSnapshot) {
                if (ss.exists()) {
                    post = ss.getValue(Post::class.java) as Post
                    post!!.setpost_image(ss.child("post_image").value.toString())
                    post!!.setpostContent(ss.child("post_content").value.toString())
                    post!!.setpost_id(ss.child("post_id").value.toString())

                    if(type=="avatar"){
                        Picasso.get().load(post!!.getpost_image()).into(avatar)
                        postImage.visibility= View.GONE
                        container.visibility=View.VISIBLE

                    }
                    else{
                        Picasso.get().load(post!!.getpost_image()).into(postImage)
                        container.visibility=View.GONE
                        postImage.visibility= View.VISIBLE

                    }
                    uri=post!!.getpost_image()

                    publishInfo(avatar_shared, name_shared,  post!!.getpublisher())
                    //describe import
                    if(post!!.getpostContent()=="" ){
                        content_shared.visibility=View.GONE

                    }else{
                        content_shared.visibility=View.VISIBLE
                        content_shared.text=post!!.getpostContent()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }
    private fun getPostAndShare(){
//        for(p in postListID){
//            (mPost as ArrayList).add(getPost(p))
//        }
//        for (s in shareListID){
//            (mShare as ArrayList).add(getPostShared(s))
//
//        }

    }
    private fun getPostShared(id: String): SharePost{
        val shareRef= FirebaseDatabase.getInstance().reference.child("Share Posts").child(id)
        var sharePost: SharePost?=null
        shareRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    sharePost = snapshot.getValue<SharePost>(SharePost::class.java)
                }
            }
        })
        return sharePost as SharePost
    }
    private fun setnumberLike(numlikes: TextView, getpostId: String) {
        val likeRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(getpostId)
        likeRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    numlikes.text = snapshot.childrenCount.toString()
                }else{
                    numlikes.text="0"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setComment(numcomment: TextView, postId: String){
        val commentRef= FirebaseDatabase.getInstance().reference.child("AllComment")
           // .child("Comments").child(postId)
        commentRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    var ss: Int = 0
                    for(s in snapshot.child("Comments").child(postId).children){
                        ss+=snapshot.child("CommentReplays").child(s.key.toString()).childrenCount.toString().toInt()
                        for(s1 in snapshot.child("CommentReplays").child(s.key.toString()).children){
                            ss+=snapshot.child("CommentReplays").child(s1.key.toString()).childrenCount.toString().toInt()
                            for(s2 in snapshot.child("CommentReplays").child(s1.key.toString()).children){
                                ss+=snapshot.child("CommentReplays").child(s2.key.toString()).childrenCount.toString().toInt()
                                for(s3 in snapshot.child("CommentReplays").child(s2.key.toString()).children){
                                    ss+=snapshot.child("CommentReplays").child(s3.key.toString()).childrenCount.toString().toInt()
                                    for(s4 in snapshot.child("CommentReplays").child(s3.key.toString()).children){
                                        ss+=snapshot.child("CommentReplays").child(s4.key.toString()).childrenCount.toString().toInt()
                                        for(s5 in snapshot.child("CommentReplays").child(s4.key.toString()).children){
                                            ss+=snapshot.child("CommentReplays").child(s5.key.toString()).childrenCount.toString().toInt()
                                        }
                                    }
                                }
                            }
                        }


                    }
                    ss+=snapshot.child("Comments").child(postId).childrenCount.toString().toInt()
                    if(ss>0){
                        numcomment.text=ss.toString() + " bình luận"
                        numcomment.visibility=View.VISIBLE
                    }
                    else{
                        numcomment.visibility=View.GONE
                    }
                }
                else {
                    numcomment.visibility=View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun checkLikes(postId: String, likeButton: ImageButton, tvThich: TextView) {
        val currentUser= FirebaseAuth.getInstance().currentUser

        val likeRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likeRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(currentUser!!.uid).exists()){
                    likeButton.tag="Liked"
                    //likeButton.setTextAppearance(mcontext, R.style.likeButtonClicked) // image like
                    likeButton.setImageResource(R.drawable.custombtn_liked)
                    tvThich.setTextColor(Color.parseColor("#FFFFFF"))

                }
                else{
                   likeButton.tag="Like"
                 //  likeButton.setTextAppearance(mcontext, R.style.likeButton) //image not like
                   likeButton.setImageResource(R.drawable.custombtn_like)
                    tvThich.setTextColor(Color.parseColor("#2FBBF0"))
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


    override fun getItemCount(): Int {
        return mLstType.size
    }

    private fun addNotifyLike(publisherID: String, postId: String, type : String){

        val ref= FirebaseDatabase.getInstance().reference.child("Notify").child(publisherID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var check : Boolean=false
                for(s in snapshot.children){
                    if(s.child("postID").value.toString()==postId){
                        check=true
                    }
                }
                if(!check){
                    val notiRef= FirebaseDatabase.getInstance().reference
                        .child("Notify").child(publisherID)
                    val notiMap = HashMap<String, Any>()
                    notiMap["userID"]=firebaseUser!!.uid
                    val idpush : String = notiRef.push().key.toString()
                    if (type=="thichbaiviet"){
                        notiMap["notify"]="đã thích bài viết của bạn"
                    } else if (type=="thichbaishare"){
                        notiMap["notify"] ="đã thích bài chia sẻ của bạn"
                    } else if(type=="changeavatar"){
                        notiMap["notify"] ="đã thích hình đại diện của bạn"

                    }
                    notiMap["postID"]=postId
                    notiMap["type"]=type
                    notiMap["notifyID"]=idpush
                    notiRef.child(idpush).setValue(notiMap)
                }
            }
        })


    }


    private fun publishInfo(profileImage: CircleImageView, userName: TextView,   publiser: String) {
        val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation").child(publiser)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.duongtu).into(profileImage)

                    userName.setText(user!!.getName())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}