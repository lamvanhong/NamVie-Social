package com.lamhong.viesocial.Fragment

import android.content.Context
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
import com.lamhong.viesocial.AccountSettingActivity
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileFragment : Fragment() {
    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser

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
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        //get friendlist
        firebaseUser =FirebaseAuth.getInstance().currentUser
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(pref != null){
            this.profileId= pref.getString("profileId", "none").toString()

        }
        if(profileId==firebaseUser.uid){
            view.txtSetting.text="Edit profile"
        }
        else {
            checkFriends()   //check if my wall || friend or not
        }


        view.btnSetting.setOnClickListener {
            val tempText= view.txtSetting.text.toString()
            when (tempText){
                "Edit profile"-> startActivity(Intent(context, AccountSettingActivity::class.java))
                "Add friend" ->{
                    firebaseUser?.uid.let{it1->
                        FirebaseDatabase.getInstance().reference
                                .child("Friends").child(it1.toString())
                                .child("friendList").child(profileId)
                                .setValue(true)
                    }
                        FirebaseDatabase.getInstance().reference
                                .child("Friends").child(profileId)
                                .child("friendList").child(firebaseUser.uid)
                                .setValue(true)
                }
                "Friend" ->{
                    firebaseUser?.uid.let{it->
                        FirebaseDatabase.getInstance().reference
                                .child("Friends").child(it.toString())
                                .child("friendList").child(profileId)
                                .removeValue()
                    }
                    FirebaseDatabase.getInstance().reference
                            .child("Friends").child(profileId)
                            .child("friendList").child(firebaseUser.uid)
                            .removeValue()
                }
            }
        }
        txtSetting

        getFriends()
        getInfor()
        return view;

    }
    private fun getInfor(){
        // get name methods 1
        val nameRef= FirebaseDatabase.getInstance().reference
                    .child("UserInformation").child(profileId)
        nameRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user =snapshot.getValue<User>(User::class.java)
                print("test")
                user?.setEmail(snapshot.child("email").value.toString())
                user?.setName(snapshot.child("fullname").value.toString())
                txt_name_avatar.text=user?.getName()
                tv_descrip.text=user?.getEmail()
                Picasso.get().load(user?.getAvatar()).placeholder(R.drawable.duongtu1).into(avatar)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        // get name methods 2

    }
    private fun checkFriends() {
        val friendref= FirebaseDatabase.getInstance().reference
                    .child("Friends").child(firebaseUser.uid)
                    .child("friendList")

        if(friendref!=null){
            friendref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        view?.txtSetting?.text="Friend"
                    }
                    else{
                        view?.txtSetting?.text="Add friend"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    private fun getFriends(){
        val friendsref= FirebaseDatabase.getInstance().reference
                    .child("Friends").child(profileId)
                    .child("friendList")

        friendsref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    view?.tv_numberFriends?.text=snapshot.childrenCount.toString()
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}