    package com.lamhong.viesocial.Fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.UserAdapter
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import kotlinx.android.synthetic.main.fragment_home.view.*


    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment()  {

//    private var postAdapter : PostAdapter?=null
//    private var postList : MutableList<Post>?=null
//    private var followingList : MutableList<Post>?=null


    private var recyclerView : RecyclerView?=null
    private var userAdapter : UserAdapter ?=  null
    private var _users : MutableList<User> ?=null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnHome()
            }
        })
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    fun returnHome(){
        (context as FragmentActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, zHome()).commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView= view.findViewById(R.id.recycleviewSearch)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(context)

        _users= ArrayList()
        userAdapter= context?.let { UserAdapter(it, _users as ArrayList<User> , true) }
        recyclerView?.adapter= userAdapter

        view.edittext_search.requestFocusFromTouch()
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        view.btn_returnHome.setOnClickListener{
           returnHome()
        }

        view.edittext_search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(view.edittext_search.text.toString() == ""){
                    _users!!.clear()
                    userAdapter!!.notifyDataSetChanged()

                }else
                {
                   recyclerView?.visibility= View.VISIBLE
                   searchUser(s.toString().toLowerCase())

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

//        postList= ArrayList()
//        postAdapter= context?.let { PostAdapter(it, postList as ArrayList<Post>) }
//        recyclerView!!.adapter= postAdapter

       // checkFollowing()



        return view

    }
    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun searchUser(datainput : String){
            val getdata= FirebaseDatabase.getInstance().getReference().child("UserInformation").orderByChild("fullname")
            .startAt(datainput).endAt(datainput+ "\uf8ff")
        getdata.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _users?.clear()
                for (snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)

                    user?.setName(snap.child("fullname").value.toString())
                    user?.setEmail(snap.child("email").value.toString())

                    if (user != null) {
                        _users?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun userMatch(){
        val user_ref = FirebaseDatabase.getInstance().getReference().child("UserInformation")
        user_ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(view?.edittext_search?.text.toString()==""){
                    _users?.clear()
                    for (snap in snapshot.children){
                        val user= snap.getValue(User::class.java)
                        if(user!=null){
                            _users?.add(user)
                            print("honghonghong")
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}