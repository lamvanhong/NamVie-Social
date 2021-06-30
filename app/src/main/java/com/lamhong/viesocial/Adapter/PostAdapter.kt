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
import com.lamhong.viesocial.CommentActivity
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter (private val mcontext: Context, private val mPost : List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>(){

    private var firebaseUser : FirebaseUser?=null


    inner class ViewHolder(@NonNull itemVIew: View): RecyclerView.ViewHolder(itemVIew){

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
        init {
            postImage = itemView.findViewById(R.id.post_image_home)
            profileImage = itemView.findViewById(R.id.user_profile_image_search)

            userName = itemView.findViewById(R.id.user_name_search)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser

        val post= mPost[position]
        Picasso.get().load(post.getpost_image()).into(holder.postImage)

        publishInfo(holder.profileImage, holder.userName,  post.getpublisher())
        //describe import
        if(post.getpostContent().equals("")){
            holder.describe.visibility=View.GONE
        }else{
            holder.describe.visibility=View.VISIBLE
            holder.describe.text=post.getpostContent()
        }

        checkLikes(post.getpost_id(), holder.btnLike , holder.tvthich)
        setnumberLike(holder.numlikes,post.getpost_id())
        setComment(holder.numcomment, post.getpost_id())
        holder.btnLike.setOnClickListener{
            if(holder.btnLike.tag=="Like"){
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
        holder.btnComment.setOnClickListener{
            val commentIntent = Intent(mcontext, CommentActivity::class.java)
            commentIntent.putExtra("postID", post.getpost_id())
            commentIntent.putExtra("publisher", post.getpublisher())
            mcontext.startActivity(commentIntent)
        }
    }

    private fun setnumberLike(numlikes: TextView, getpostId: String) {
        val likeRef= FirebaseDatabase.getInstance().reference
            .child("Likes").child(getpostId)
        likeRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    numlikes.text = snapshot.childrenCount.toString() + " Yêu thích"
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
        return mPost.size
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