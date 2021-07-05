package com.lamhong.viesocial.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.AddShotVideoActivity
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.MyShotVideoActivity
import com.lamhong.viesocial.R
import com.lamhong.viesocial.ShotVideoActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.fragment_video.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_video, container, false)
        firebaseUser= FirebaseAuth.getInstance().currentUser


        view.btn_addVideo.setOnClickListener{
            startActivity(Intent(context, AddShotVideoActivity::class.java))
        }
        view.btn_videoHot.setOnClickListener{
            val intent = Intent(context, ShotVideoActivity::class.java)
            intent.putExtra("type", "videohot")
            startActivity(intent)
        }
        view.btn_videoFollow.setOnClickListener{
            val intent = Intent(context, ShotVideoActivity::class.java)
            intent.putExtra("type", "videofollow")
            startActivity(intent)
        }
        view.btn_videocuatoi.setOnClickListener{
            val intent = (Intent(context, MyShotVideoActivity::class.java))
            intent.putExtra("id", firebaseUser.uid)
            startActivity(intent)
        }
        showInfor()
        return view
    }

    private fun showInfor() {
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser.uid!!)
        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val curUser = snapshot.getValue(User::class.java)
                    curUser!!.setName(snapshot.child("fullname").value.toString())
                    if(user_name_shotprofile!=null && avatar_shotprofile!=null){
                        user_name_shotprofile.text=curUser!!.getName()
                        Picasso.get().load(curUser!!.getAvatar()).into(avatar_shotprofile)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        setNumberProfile()
    }

    private fun setNumberProfile() {
        val ref= FirebaseDatabase.getInstance().reference
            .child("Friends").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("followerList").exists()){
                        if(numFollower_shotprofile!=null)
                        numFollower_shotprofile.text=snapshot.child("followerList").childrenCount.toString()
                    }
                    else{
                        if(numFollower_shotprofile!=null)
                        numFollower_shotprofile.text="0"
                    }
                    if(snapshot.child("followingList").exists()){
                        if(numFollowinng_shotprofile!=null)
                        numFollowinng_shotprofile.text=snapshot.child("followingList").childrenCount.toString()
                    }
                    else{
                        if(numFollowinng_shotprofile!=null)
                        numFollowinng_shotprofile.text="0"
                    }
                }
                else{
                    numFollowinng_shotprofile.text="0"
                    numFollower_shotprofile.text="0"
                }
            }
        })
        val postRef= FirebaseDatabase.getInstance().reference
            .child("ShotVideos")
        postRef.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var ss: Int=0
                if(snapshot.exists()){
                    for(s in snapshot.children)
                    {
                        if(s.child("publisher").value==firebaseUser.uid){
                            ss+=1
                        }
                    }
                }
                if(numvideo_shotprofile!=null){
                    numvideo_shotprofile.text=ss.toString()

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
         * @return A new instance of fragment VideoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                VideoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}