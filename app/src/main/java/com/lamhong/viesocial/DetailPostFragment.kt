package com.lamhong.viesocial

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.PostAdapter
import com.lamhong.viesocial.Models.Post
import com.lamhong.viesocial.Models.SharePost

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailPostFragment : Fragment() {


    private var postAdapter : PostAdapter?=null
    private var postList: MutableList<Post>?=null
    private var idPost: String = ""
    private var lstIndex : List<Int> = ArrayList()
    private var lstType: List<Int> = ArrayList()
    private var shareList: List<SharePost>  = ArrayList()

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
        val view=  inflater.inflate(R.layout.fragment_detail_post, container, false)


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref!=null){
            idPost= pref.getString("postID","none").toString()
        }
        var recycleview : RecyclerView
        recycleview= view.findViewById(R.id.recycleview_detailpost)
        val linearLayoutManager= LinearLayoutManager(context)
        recycleview.layoutManager= linearLayoutManager
        recycleview?.visibility= View.VISIBLE

        postList= ArrayList()

        postAdapter= context?.let { PostAdapter(it, postList as ArrayList<Post>,lstIndex as ArrayList
                , lstType as ArrayList, shareList as ArrayList) }
        recycleview.adapter= postAdapter


        retrivePost()

        return view
    }

    private fun retrivePost(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts")
            .child(idPost)
        postRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList!!.clear()
                if (snapshot.exists()){
                    val post =snapshot.getValue(Post::class.java)
                    post!!.setpost_image(snapshot.child("post_image").value.toString())
                    post!!.setpostContent(snapshot.child("post_content").value.toString())
                    if (post != null) {
                        postList!!.add(post)
                        (lstIndex as ArrayList).add(0)
                        (lstType as ArrayList).add(0)
                    }

                }
                postAdapter!!.notifyDataSetChanged()
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
         * @return A new instance of fragment DetailPostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}