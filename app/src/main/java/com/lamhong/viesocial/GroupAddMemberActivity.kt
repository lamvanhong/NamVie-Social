package com.lamhong.viesocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.MemberAddAdapter
import com.lamhong.viesocial.Models.User
import kotlinx.android.synthetic.main.activity_group_add_member.*

class GroupAddMemberActivity : AppCompatActivity() {

    //private var usersRv:RecyclerView = RecyclerView

    private var groupID:String?=null

    private var currentUid = FirebaseAuth.getInstance().uid

    private var myGroupRole:String?=null

    private var userList = arrayListOf<User>()

    private var adapter = MemberAddAdapter(userList, groupID.toString(), myGroupRole.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_add_member)

        setSupportActionBar(toolbar_group_add_member)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val usersRv = usersRv

        groupID = intent.getStringExtra("groupID")

        loadGroupInfo()



    }

    private fun getAllUsers() {
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (ss in snapshot.children) {
                        val user = ss.getValue(User::class.java)
                        user!!.setName(ss.child("fullname").value.toString())
                        if (currentUid != user?.getUid()) {
                            if (user != null) {
                                userList.add(user)
                            }
                        }
                    }

                    adapter = MemberAddAdapter(userList,""+groupID,""+myGroupRole)
                    usersRv.layoutManager = LinearLayoutManager(this@GroupAddMemberActivity)
                    usersRv.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

            })
    }

    private fun loadGroupInfo() {
        val ref = FirebaseDatabase.getInstance().reference.child("groups")
        val ref1 = FirebaseDatabase.getInstance().reference.child("groups")
        ref.orderByChild("groupID")
            .equalTo(groupID)
            .addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ss in snapshot.children) {
                    val groupID = "" + ss.child("groupID").value
                    val groupTitle = "" + ss.child("groupTitle").value
                    val groupDescription = "" + ss.child("groupDescription").value
                    val groupIcon = "" + ss.child("groupIcon").value
                    val createBy = "" + ss.child("createBy").value
                    val timestamp = "" + ss.child("timestamp").value

                    ref1.child(groupID).child("members").child(currentUid.toString())
                        .addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    myGroupRole = "" + snapshot.child("role").value
                                    title_group_add_mem.text = "$groupTitle($myGroupRole)"
                                    getAllUsers()
                                }
                            }

                        })
                }
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}