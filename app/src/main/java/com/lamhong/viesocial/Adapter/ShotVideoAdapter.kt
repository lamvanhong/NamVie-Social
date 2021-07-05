package com.lamhong.viesocial.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lamhong.viesocial.Models.NotificationData
import com.lamhong.viesocial.Models.PushNotification
import com.lamhong.viesocial.Models.ShortVideo
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.MyShotVideoActivity
import com.lamhong.viesocial.Network.MyFirebaseMessagingService
import com.lamhong.viesocial.Network.RetrofitInstance
import com.lamhong.viesocial.R
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

const val TOPIC ="/topics/myTopic"

class ShotVideoAdapter (private val mcontext : Context, private val listShot: ArrayList<ShortVideo?>)
    : RecyclerView.Adapter<ShotVideoAdapter.ViewHolder>(){
    lateinit var firebaseUser : FirebaseUser
    private var videoList : ArrayList<Uri> = ArrayList()
    private var video : MediaStore.Video?=null
    var xpos: Int =0
    var insView =0

    var userInfor: User = User()
    var objOwnInfor :User = User()

    inner class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        fun setData(obj : ShortVideo){
            //Notification
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(it.isComplete){
                    val fbToken = it.result.toString()
                    MyFirebaseMessagingService.token =fbToken
                }
            }
            val userRef = FirebaseDatabase.getInstance().reference
                .child("UserInformation").child(FirebaseAuth.getInstance().uid!!)
            userRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userInfor.setAvatar(snapshot.child("avatar").value.toString())
                        userInfor.setName(snapshot.child("fullname").value.toString())
                        userInfor.setEmail(snapshot.child("email").value.toString())
                        userInfor.setUid(snapshot.child("uid").value.toString())
                        userInfor.setToken(snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            val userRef2 = FirebaseDatabase.getInstance().reference
                .child("UserInformation").child(obj.getPublisher())
            userRef2.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        objOwnInfor.setAvatar(snapshot.child("avatar").value.toString())
                        objOwnInfor.setName(snapshot.child("fullname").value.toString())
                        objOwnInfor.setEmail(snapshot.child("email").value.toString())
                        objOwnInfor.setUid(snapshot.child("uid").value.toString())
                        objOwnInfor.setToken(snapshot.child(Constants.KEY_FCM_TOKEN).value.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
            //End notification

            videoView.tag="true"
            firebaseUser= FirebaseAuth.getInstance().currentUser!!


            //content display
            content.text=obj.getContent().toString()
            if(obj.getContent()==""){
                content.visibility=View.GONE
            }
            else{
                content.visibility=View.VISIBLE

            }
            //video display
            val vv : VideoView
            videoView.setVideoPath(obj.getVideo())
            videoView.setOnPreparedListener {
                mp: MediaPlayer? ->
                progressBar.visibility=View.GONE
                mp?.start()
                insView= obj.getViews().toInt()+1
                FirebaseDatabase.getInstance().reference.child("ShotVideos").child(obj.getID())
                        .child("views").setValue(insView)

            }

            videoView.setOnCompletionListener {
                mp ->mp.start()


            }
            // watch task

            countUptimeWatching(obj.getID() , obj.getViews().toString().toInt())



            // click task
            videoView.setOnClickListener{
                if(videoView.tag=="true"){
                    xpos = videoView.currentPosition
                    videoView.pause()
                    videoView.tag="false"
                    xpos = videoView.currentPosition
                    // Toast.makeText(mcontext,"stop", Toast.LENGTH_SHORT).show()
                }
                else{
                    videoView.seekTo(xpos)
                    videoView.start()
                    videoView.tag="true"
                    //Toast.makeText(mcontext,"play", Toast.LENGTH_SHORT).show()

                }
            }
            username.setOnClickListener{
                videoView.start()
            }
            //user publiser infor
            getpublisherInfor(obj.getPublisher(), avatar , username)
//
//            //like func
            checkLikes(obj.getID(), btn_like, tvthich)
            setnumberLike(tv_numlike, obj.getID())


            btn_like.setOnClickListener{
                if(btn_like.tag=="Like"){
                    //addNotifyLike(post.getpublisher(), post.getpost_id() , "thichbaiviet")
                    FirebaseDatabase.getInstance().reference
                            .child("ShotVideoReact").child("Likes")
                            .child(obj.getID())
                            .child(firebaseUser!!.uid)
                            .setValue(true)
                    setNotify(obj)
                }else
                {
                    FirebaseDatabase.getInstance().reference
                            .child("ShotVideoReact").child("Likes")
                            .child(obj.getID())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                    //  val intent=Intent(mcontext,zHome::class.java)
                    // mcontext.startActivity(intent)
                }
            }
            if(firebaseUser.uid==obj.getPublisher()){
                btn_follow.visibility=View.GONE
                btn_followed.visibility=View.GONE
            }
            else{
                checkFollow(btn_follow, btn_followed, obj.getPublisher())
            }
            btn_follow.visibility=View.GONE
            btn_followed.visibility=View.GONE
            // FOllow space

            btn_follow.setOnClickListener{
                FirebaseDatabase.getInstance().reference.child("Friends")
                        .child(firebaseUser.uid).child("followingList")
                        .child(obj.getPublisher()).setValue("true")
                btn_follow.visibility=View.GONE
                btn_followed.visibility=View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    btn_followed.visibility=View.GONE
                }, 5000)

            }
            avatar.setOnClickListener{
                val intent = (Intent(mcontext, MyShotVideoActivity::class.java))
                intent.putExtra("id", obj.getPublisher())
                mcontext.startActivity(intent)
            }
            btn_finish.setOnClickListener{
                (mcontext as Activity).finish()
            }

        }

        //Send notification to Local
        private var fireabaseUser: FirebaseUser?= FirebaseAuth.getInstance().currentUser
        private fun setNotify(obj : ShortVideo){
            val notiRef= FirebaseDatabase.getInstance().reference
                .child("Notify").child(obj.getPublisher())
            val notiMap= HashMap<String, String>()
            val idpush : String = notiRef.push().key.toString()
            notiMap["userID"]=fireabaseUser!!.uid
            notiMap["notify"]="đã thích video của bạn"
            notiMap["postID"]= obj.getID()
            notiMap["type"]="thichvideo"
            notiMap["notifyID"]=idpush
            notiRef.child(idpush).setValue(notiMap)
            doSendNotify(notiMap)
        }

        private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if(response.isSuccessful){
                   // Log.d("TAG","Response: ${Gson().toJson(response)}")
                }else{
                   // Log.e("TAG",response.errorBody().toString())
                }
            }catch (e: Exception){
                Log.e("TAG", e.toString())
            }
        }

        private fun doSendNotify(notiInfor: HashMap<String,String>) {
            val title:String = "Thông báo"
            val message:String = userInfor.getName() +" "+notiInfor["notify"]!!
            val recipientToken = objOwnInfor.getToken()
            PushNotification(
                NotificationData(title,message),
                recipientToken
            ).also {
                sendNotification(it)
            }
        }

        //End send notification

        fun setnextData(obj: ShortVideo){
            videoView.setVideoPath(obj.getVideo())
        }
        lateinit var videoView : VideoView
        lateinit var username : TextView
        lateinit var avatar : ImageView
        lateinit var content: TextView
        lateinit var tv_numlike: TextView
        lateinit var tvthich: TextView
        lateinit var progressBar : ProgressBar
        var btn_like: ImageButton
        var btn_follow: ImageView
        var btn_followed: ImageView
        var btn_finish: ImageView


        init {
            videoView= itemview.findViewById(R.id.videoView)
            username= itemview.findViewById(R.id.username_layout)
            avatar= itemview.findViewById(R.id.avatar_item_shot)
            content= itemview.findViewById(R.id.content)
            progressBar= itemview.findViewById(R.id.videoProgressBar)
            btn_like= itemview.findViewById(R.id.btn_yeuthich)
            tv_numlike=itemview.findViewById(R.id.numlikes_shot)
            tvthich= itemview.findViewById(R.id.tv_thich)
            btn_follow= itemview.findViewById(R.id.btn_follow)
            btn_followed= itemview.findViewById(R.id.btn_followed)
            btn_finish= itemview.findViewById(R.id.btn_finish)

        }
    }

    private fun countUptimeWatching(id: String , view: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_video_row,
                parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listShot.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = listShot[position]
        for(i in listShot){
            if(position==2) break
            val uri = Uri.parse(i!!.getVideo())
            videoList.add(uri)
        }
        holder.setData(model!!)
    }
    private fun checkFollow( btn_follow : ImageView, btn_followed: ImageView , id: String){
        val usref= FirebaseDatabase.getInstance().reference
                .child("Friends").child(firebaseUser.uid).child("followingList").child(id)
        usref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    btn_follow.visibility=View.GONE


                }else{
                    btn_follow.visibility=View.VISIBLE
                    btn_followed.visibility=View.GONE
                }
            }
        })
    }
    private fun getpublisherInfor(publisher: String, avatar: ImageView, username: TextView) {
        val ref= FirebaseDatabase.getInstance().reference
                .child("UserInformation").child(publisher)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    user!!.setName(snapshot.child("fullname").value.toString())
                    user!!.setAvatar(snapshot.child("avatar").value.toString())
                    Picasso.get().load(user.getAvatar()).into(avatar)
                    username.text=user.getName()
                }
            }
        })
    }
    private fun setnumberLike(numlikes: TextView, postID: String) {
        val likeRef= FirebaseDatabase.getInstance().reference
                .child("ShotVideoReact").child("Likes").child(postID)
        likeRef.addValueEventListener(object: ValueEventListener {
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
    private fun checkLikes(postId: String, likeButton: ImageButton, tvThich: TextView) {
        val currentUser= FirebaseAuth.getInstance().currentUser

        val likeRef= FirebaseDatabase.getInstance().reference
                .child("ShotVideoReact").child("Likes").child(postId)

        likeRef.addValueEventListener(object: ValueEventListener {
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
}