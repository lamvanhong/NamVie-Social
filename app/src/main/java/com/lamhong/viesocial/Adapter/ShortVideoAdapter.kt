package com.lamhong.viesocial.Adapter

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.ShortVideo
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso

class ShortVideoAdapter(private val mcontext : Context, private val options: FirebaseRecyclerOptions<ShortVideo?>)
    : FirebaseRecyclerAdapter<ShortVideo?, ShortVideoAdapter.ViewHolder>(options)
{
    lateinit var firebaseUser : FirebaseUser
    var xpos: Int =0
    inner class ViewHolder(itemview : View): RecyclerView.ViewHolder(itemview){
        fun setData(obj : ShortVideo){
            videoView.tag="true"
            firebaseUser= FirebaseAuth.getInstance().currentUser
            videoView.setVideoPath(obj.getVideo())
//            val mc = MediaController(mcontext)
//            videoView.setMediaController(mc)
           // title.text=obj.title
            content.text=obj.getContent().toString()
            if(obj.getContent()==""){
                content.visibility=View.GONE
            }
            else{
                content.visibility=View.VISIBLE

            }
            videoView.setOnPreparedListener { 
                mp: MediaPlayer? ->  
                progressBar.visibility=View.GONE
                mp?.start()


            }

            videoView.setOnCompletionListener {
                mp ->mp.start()
            }

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

            //like func
            checkLikes(obj.getID(), btn_like, tvthich)
            setnumberLike(tv_numlike, obj.getID())
            btn_like.setOnClickListener{
                if(btn_like.tag=="Like"){
                   // addNotifyLike(post.getpublisher(), post.getpost_id() , "thichbaiviet")
                    FirebaseDatabase.getInstance().reference
                            .child("ShotVideoReact").child("Likes")
                            .child(obj.getID())
                            .child(firebaseUser!!.uid)
                            .setValue(true)
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

        }

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


        init {
            videoView= itemview.findViewById(R.id.videoView)
            username= itemview.findViewById(R.id.username_layout)
            avatar= itemview.findViewById(R.id.avatar_item_shot)
            content= itemview.findViewById(R.id.content)
            progressBar= itemview.findViewById(R.id.videoProgressBar)
            btn_like= itemview.findViewById(R.id.btn_yeuthich)
            tv_numlike=itemview.findViewById(R.id.numlikes_shot)
            tvthich= itemview.findViewById(R.id.tv_thich)

        }
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_video_row,
            parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ShortVideo) {
        holder.setData(model)



    }

    private fun setnumberLike(numlikes: TextView, postID: String) {
        val likeRef= FirebaseDatabase.getInstance().reference
                .child("ShotVideoReact").child("Likes").child(postID)
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