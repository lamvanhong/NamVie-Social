 package com.lamhong.viesocial.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.PostAdapter
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.R

 // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [zHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class zHome : Fragment() {


    private var postAdapter : PostAdapter?=null
    private var postList : MutableList<Post>?=null
    private var followingList : MutableList<Post>?=null


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
        val view= inflater.inflate(R.layout.fragment_z_home, container, false)
        var recycleView: RecyclerView ?=null
        recycleView= view.findViewById(R.id.recycleviewzHome)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recycleView.layoutManager= linearLayoutManager


        postList= ArrayList()
        postAdapter= context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recycleView.adapter= postAdapter
        recycleView?.visibility= View.VISIBLE
         checkFollowing()

        return view;
    }

        private fun checkFollowing() {
            followingList=ArrayList()

        val followRef = FirebaseDatabase.getInstance().reference
            .child("Friends").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("friendList")
        followRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (followingList as ArrayList<String>).clear()
                    for (s in snapshot.children){
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrievePosts() {
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object: ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for(ss in snapshot.children){
                    if(ss.exists())
                    {
                       val post = ss.getValue(Post::class.java)
                   //    val post = Post()
                    post!!.setpost_image(ss.child("post_image").value.toString())
                        post!!.setpostContent(ss.child("post_content").value.toString())
                    for (idUser in (followingList as ArrayList<String>)) {
                        if(post!!.getpublisher() == idUser){
                        postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

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
         * @return A new instance of fragment zHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            zHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}