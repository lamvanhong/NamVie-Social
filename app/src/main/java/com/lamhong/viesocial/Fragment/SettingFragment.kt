package com.lamhong.viesocial.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.*
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Models.UserInfor
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.avatar
import kotlinx.android.synthetic.main.fragment_setting.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {

    lateinit var firebaseUser: FirebaseUser
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
    // intent transfer
    var userAvatar1 =""
    var userName1=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        view.btn_movetoFriendList.setOnClickListener{
            val friendListIntent= Intent(context, FriendListActivity::class.java)
            friendListIntent.putExtra("userID", firebaseUser.uid)
            this?.startActivity(friendListIntent)
        }
        view.btn_logout.setOnClickListener {
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle("Xác nhận")
                alert.setIcon(R.drawable.ic_exit)
                alert.setMessage("Bạn muốn đăng xuất khỏi ứng dụng ?")
                alert.setCancelable(false)
                alert.setNegativeButton("Đăng xuất") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(context, LoginActivity::class.java)) }
                alert.setPositiveButton("Không") { dialog, which -> }
                val alertDialog = alert.create()
                alertDialog.show()
        }
        view.btn_Private.setOnClickListener{
            startActivity(Intent(context, PrivateActivity::class.java))
        }
        view.btn_movetoUserActivity.setOnClickListener{
            val acIntent = Intent(context, UserActiviesActivity::class.java)
            acIntent.putExtra("userAvatar", userAvatar1  )
            acIntent.putExtra("userName", userName1)
            startActivity(acIntent)
        }
        view.btn_movetoSavePost.setOnClickListener{
            startActivity(Intent(context, UserSavePostActivity::class.java))
        }

        view.btn_movetoSetting.setOnClickListener{
            startActivity(Intent(context, SettingActivity::class.java))
        }
        view.btn_movetoProfile.setOnClickListener{
//            (context as FragmentActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.frameLayout, ProfileFragment()).commit()

            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("profileID", firebaseUser.uid)
            startActivity(intent)
        }

        showInfor()
        showInforDetail()



        return view
    }



    private fun showInforDetail(){
        val userDetailRef = FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
        userDetailRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val userInfor: UserInfor = UserInfor()
                    userInfor!!.setBio(snapshot.child("bio").value.toString())
                   // describe.text=userInfor!!.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun showInfor() {
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser.uid!!)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val curUser = snapshot.getValue(User::class.java)
                    curUser!!.setName(snapshot.child("fullname").value.toString())
                    if(userName!=null)
                    userName.text=curUser!!.getName()

                    if(avatar!=null)
                    Picasso.get().load(curUser!!.getAvatar()).into(avatar)

                    //prepare value for intent
                    userName1= curUser!!.getName()
                    userAvatar1=curUser!!.getAvatar()


                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val userInforRef= FirebaseDatabase.getInstance().reference
            .child("UserDetails")
            .child(firebaseUser.uid!!)
        userInforRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user : UserInfor= UserInfor()
                    user!!.setBio(snapshot.child("bio").value.toString())

                    if(coverBlur!=null)
                    Picasso.get().load(snapshot.child("coverImage").value.toString()).placeholder(R.color.white)
                        .into(coverBlur)

                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        setNumberProfile()
    }

    private fun setNumberProfile() {
        val followList= FirebaseDatabase.getInstance().reference
            .child("Friends").child(firebaseUser.uid)
            .child("followerList")

        followList.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){


                    numFollow?.text=snapshot.childrenCount.toString()

                }
                else {
                    numFollow?.text="0"
                }
            }
        })
        val ref= FirebaseDatabase.getInstance().reference
            .child("Friends").child(firebaseUser.uid).child("friendList")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(NumFriends!=null)
                    {
                        var sum=0
                        for (s in snapshot.children){
                            if(s.value.toString()=="friend"){
                                sum+=1
                            }
                        }
                        NumFriends?.text=sum.toString()
                    }

                }
                else{
                    NumFriends.text="0"
                }
            }
        })
        val postRef= FirebaseDatabase.getInstance().reference
            .child("Contents").child("ProfileTimeLine")
            .child(firebaseUser.uid)
        postRef.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var ss: Int=0
                if(snapshot.exists()){
                    for(s in snapshot.children)
                    {
                        ss+=1
//                        if(s.child("post_type").value.toString()=="post"){
//                            ss+=1
//                        }
                    }
                }
                if(numPost!=null)
                numPost.text=ss.toString()
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
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}