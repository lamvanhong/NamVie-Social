package com.lamhong.mybook.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lamhong.mybook.Adapter.NotifyAdapter
import com.lamhong.mybook.Models.Notify
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.fragment_notify.view.*
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotifyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotifyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    final lateinit var firebaseUser: FirebaseUser
    private var notifyList : List<Notify>?=null
    private var notifyAdapter : NotifyAdapter?=null

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
        var view=  inflater.inflate(R.layout.fragment_notify, container, false)

        var recyclerView : RecyclerView= view.findViewById(R.id.recycleview_notify)
        recyclerView.visibility=View.VISIBLE
        recyclerView.layoutManager= LinearLayoutManager( context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        notifyList= ArrayList()
        notifyAdapter= context?.let { NotifyAdapter(it, notifyList as ArrayList<Notify>)}
        recyclerView.adapter= notifyAdapter
        showNotify()

        view.searchbtn_inNotify.setOnClickListener{
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, HomeFragment()).commit()
        }
        return view
    }

    private fun showNotify(){
        val notiRef= FirebaseDatabase.getInstance().reference
            .child("Notify").child(firebaseUser.uid)
        notiRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (notifyList as ArrayList<Notify>).clear()
                    for(s in snapshot.children){
                        val notify = s.getValue(Notify::class.java)
                        notify?.setPostID(s.child("postID").value.toString())
                        (notifyList as ArrayList<Notify>).add(notify!!)
                    }
                    Collections.reverse(notifyList)
                    notifyAdapter!!.notifyDataSetChanged()
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
         * @return A new instance of fragment NotifyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotifyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}