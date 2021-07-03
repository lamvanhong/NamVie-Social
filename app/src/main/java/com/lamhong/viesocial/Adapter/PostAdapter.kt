package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.*
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.Models.SharePost
import com.lamhong.viesocial.Models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter (private val mcontext: Context, private val mPost : List<Post> ,
            private val mLstIndex: List<Int> , private val mLstType: List<Int>,
            private val mShare: List<SharePost>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var firebaseUser : FirebaseUser?=null


    inner class ViewHolder0(@NonNull itemVIew: View): RecyclerView.ViewHolder(itemVIew){

        var postImage :ImageView
        var profileImage : CircleImageView
        var userName: TextView
        var numlikes: TextView= itemView.findViewById(R.id.numlikes)
        val numcomment: TextView= itemView.findViewById(R.id.comments)
        var describe: TextView = itemView.findViewById(R.id.describe)

        //new
        var btnLike: CircularProgressButton = itemView.findViewById(R.id.btn_yeuthich)
        var btnComment: CircularProgressButton = itemView.findViewById(R.id.btn_binhluan)
        var tvthich: TextView = itemView.findViewById(R.id.tv_thich)
        var btnShare : CircularProgressButton= itemView.findViewById(R.id.btn_share)
        init {
            postImage = itemView.findViewById(R.id.post_image_home)
            profileImage = itemView.findViewById(R.id.user_profile_image_search)

            userName = itemView.findViewById(R.id.user_name_search)
        }
    }
    inner class ViewHolder1(@NonNull itemVIew: View) : RecyclerView.ViewHolder(itemVIew){
        var postImage: CircleImageView
        var avatar_sharing : CircleImageView
        var avatar_shared: CircleImageView
        var name_sharing : TextView
        var name_shared: TextView
        var content_sharing: TextView
        var content_shared: TextView
        var numlike: TextView
        var numComment: TextView

        var btnLike : CircularProgressButton
        var btnComment: CircularProgressButton
        var btnShare: CircularProgressButton

        init {
            postImage= itemView.findViewById(R.id.image_content)
            avatar_sharing=itemView.findViewById(R.id.user_avata_sharing)
            avatar_shared= itemView.findViewById(R.id.user_avatar_shared)
            name_sharing= itemView.findViewById(R.id.user_name_sharing)
            name_shared= itemView.findViewById(R.id.user_name_shared)
            content_sharing=itemView.findViewById(R.id.describeShare)
            content_shared= itemView.findViewById(R.id.content_inshared)
            numlike=itemView.findViewById(R.id.numlikes)
            numComment=itemView.findViewById(R.id.comments)
            btnLike=itemView.findViewById(R.id.btn_yeuthich)
            btnComment=itemView.findViewById(R.id.btn_binhluan)
            btnShare=itemView.findViewById(R.id.btn_share)
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
        }
        val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder0(view)

    }

    override fun onBindViewHolder(holderc: RecyclerView.ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        when(holderc.getItemViewType()){
            0->{
                val holder1 : ViewHolder0 = holderc as ViewHolder0


                val post= mPost[mLstIndex[position]]
                Picasso.get().load(post.getpost_image()).into(holder1.postImage)

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
                        addNotifyLike(post.getpublisher(), post.getpost_id())
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .setValue(true)
                    }else
                    {
                        FirebaseDatabase.getInstance().reference
                            .child("Likes")
                            .child(post.getpost_id())
                            .child(firebaseUser!!.uid)
                            .removeValue()

                        //  val intent=Intent(mcontext,zHome::class.java)
                        // mcontext.startActivity(intent)

                    }
                }
                holder1.btnComment.setOnClickListener{
                    val commentIntent = Intent(mcontext, CommentActivity::class.java)
                    commentIntent.putExtra("postID", post.getpost_id())
                    commentIntent.putExtra("publisher", post.getpublisher())
                    mcontext.startActivity(commentIntent)
                }
                holder1.btnShare.setOnClickListener{
                    val shareIntent = Intent(mcontext, SharePostActivity::class.java)
                    shareIntent.putExtra("postID", post.getpost_id())
                    mcontext.startActivity(shareIntent)
                }
            }
            1->{
                val holder1= holderc as ViewHolder1
                val sharePost = mShare[mLstIndex[position]]

                //basic
                holder1.content_sharing.text=sharePost.getContent()
                publishInfo(holder1.avatar_sharing, holder1.name_sharing,sharePost.getPublisher())

                val postShared= getPost(sharePost.getPostID())
                Picasso.get().load(postShared.getpost_image()).into(holder1.postImage)
                publishInfo(holder1.avatar_shared, holder1.name_shared,  postShared.getpublisher())
                //describe import
                if(postShared.getpostContent().equals("")){
                    holder1.content_shared.visibility=View.GONE
                }else{
                    holder1.content_shared.visibility=View.VISIBLE
                    holder1.content_shared.text=postShared.getpostContent()
                }


            }
        }

    }
    private fun getPost(id: String): Post{
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts").child(id)
        var post : Post?=null
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(ss: DataSnapshot) {
                if(ss.exists()){
                    val post = ss.getValue(Post::class.java)
                    post!!.setpost_image(ss.child("post_image").value.toString())
                    post!!.setpostContent(ss.child("post_content").value.toString())
                    post.setpost_id(ss.child("post_id").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return post as Post
    }

    private fun setnumberLike(numlikes: TextView, getpostId: String) {
        val likeRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(getpostId)
        likeRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    numlikes.text = snapshot.childrenCount.toString() + " Yêu thích"
                }else{
                    numlikes.text="0 Yêu Thích"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun setComment(numcomment: TextView, postId: String){
        val commentRef= FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)
        commentRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    numcomment.text="(" + snapshot.childrenCount.toString() + ")"
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun checkLikes(postId: String, likeButton: CircularProgressButton, tvThich: TextView) {
        val currentUser= FirebaseAuth.getInstance().currentUser

        val likeRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likeRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(currentUser!!.uid).exists()){
                    likeButton.tag="Liked"
                    //likeButton.setTextAppearance(mcontext, R.style.likeButtonClicked) // image like
                    likeButton.setBackgroundResource(R.drawable.custombtn_liked)
                    tvThich.setTextColor(Color.parseColor("#FFFFFF"))

                }
                else{
                   likeButton.tag="Like"
                 //  likeButton.setTextAppearance(mcontext, R.style.likeButton) //image not like
                   likeButton.setBackgroundResource(R.drawable.custombtn_like)
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

    private fun addNotifyLike(publisherID: String, postId: String){
        val notiRef= FirebaseDatabase.getInstance().reference
            .child("Notify").child(publisherID)
        val notiMap = HashMap<String, Any>()
        notiMap["userID"]=firebaseUser!!.uid
        notiMap["notify"]="đã thích bài viết của bạn"
        notiMap["postID"]=postId
        notiMap["type"]="thichbaiviet"
        notiRef.push().setValue(notiMap)
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