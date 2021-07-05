package com.lamhong.viesocial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.MemberAddAdapter
import com.lamhong.viesocial.Models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group_info.*
import java.text.SimpleDateFormat
import java.util.*

class GroupInfoActivity : AppCompatActivity() {

    private var groupID:String?=null

    private var myGroupRole:String?=null

    private var userList = arrayListOf<User>()

    private var adapter = MemberAddAdapter(userList, groupID.toString(), myGroupRole.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_info)


        setSupportActionBar(toolbar_group_info)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        groupID = intent.getStringExtra("groupID")

        loadGroupInfo()
        loadGroupRole()

        addMemberTv.setOnClickListener {
            val intent = Intent(this,GroupAddMemberActivity::class.java)
            intent.putExtra("groupID",groupID)
            startActivity(intent)
        }

        editGroupTv.setOnClickListener {
            val intent = Intent(this,GroupEditActivity::class.java)
            intent.putExtra("groupID",groupID)
            startActivity(intent)
        }

        leaveGroupTv.setOnClickListener{
            var dialogTitle = ""
            var dialogDescription = ""
            var positiveButtonTitle = ""
            if (myGroupRole == "creator") {
                dialogTitle="Delete Group"
                dialogDescription = "Are you sure to delete this group?"
                positiveButtonTitle = "DELETE"
            }
            else {
                dialogTitle="Leave group"
                dialogDescription="Are you sure to leave this group?"
                positiveButtonTitle="LEAVE"
            }
            val builder = AlertDialog.Builder(this)
            builder.setTitle(dialogTitle)
                .setMessage(dialogDescription)
                .setPositiveButton(positiveButtonTitle) {dialog, which ->
                    if (myGroupRole == "creator") {
                        deleteGroup()
                    }
                    else {
                        leaveGroup()
                    }
                }
                .setNegativeButton("CANCEL") {dialog, which ->
                    dialog.dismiss()
                }
            builder.show()
        }
    }

    private fun leaveGroup() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("members")
            .child(FirebaseAuth.getInstance().uid.toString())
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this,"Group left successfully",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this,GroupChatsActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this,"" + it.message,Toast.LENGTH_SHORT).show()
            }

    }

    private fun deleteGroup() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this,"Group deleted successfully",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this,GroupChatsActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this,"" + it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadGroupRole() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("members")
            .orderByChild("uid")
            .equalTo(FirebaseAuth.getInstance().uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        myGroupRole = "" + ss.child("role").value
                        text_group_info.text = "($myGroupRole)"

                        if (myGroupRole == "admin") {
                            editGroupTv.visibility = View.GONE
                            addMemberTv.visibility = View.VISIBLE
                            leaveGroupTv.text = "Leave Group"
                        }
                        else if (myGroupRole == "creator") {
                            editGroupTv.visibility = View.VISIBLE
                            addMemberTv.visibility = View.VISIBLE
                            leaveGroupTv.text = "Delete Group"
                        }
                        else if (myGroupRole == "member") {
                            editGroupTv.visibility = View.GONE
                            addMemberTv.visibility = View.GONE
                            leaveGroupTv.text = "Leave Group"
                        }
                        loadMembers()
                    }
                }

            })
    }

    private fun loadMembers() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID.toString())
            .child("members")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()

                    for (ss in snapshot.children) {
                        val uid = "" + ss.child("uid").value
                        FirebaseDatabase.getInstance().reference.child("UserInformation")
                            .orderByChild("uid").equalTo(uid)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ss in snapshot.children) {
                                        val user = ss.getValue(User::class.java)
                                        user!!.setName(ss.child("fullname").value.toString())
                                        if (user != null) {
                                            userList.add(user)
                                        }
                                    }

                                    adapter = MemberAddAdapter(userList,""+groupID,""+myGroupRole)
                                    membersRv.layoutManager = LinearLayoutManager(this@GroupInfoActivity)
                                    membersRv.adapter = adapter
                                    adapter.notifyDataSetChanged()

                                    membersTv.text = "Members (${userList.size})"
                                }

                            })
                    }
                }

            })
    }

    private fun loadGroupInfo() {
        FirebaseDatabase.getInstance().reference.child("groups")
            .orderByChild("groupID").equalTo(groupID)
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        val groupID = "" + ss.child("groupID").value
                        val groupTitle = "" + ss.child("groupTitle").value
                        val groupDescription = "" + ss.child("groupDescription").value
                        val groupIcon = "" + ss.child("groupIcon").value
                        val timestamp = "" + ss.child("timestamp").value
                        val createBy = "" + ss.child("createBy").value


                        var dateFormat= SimpleDateFormat("dd/mm/yyyy hh:mm a")
                        val date = Date(timestamp.toLong())
                        val dateTime = dateFormat.format(date)

                        loadCreateInfo(dateTime,createBy)
                        text_group_info.text = groupTitle
                        desciptionTv.text = groupDescription

                        try {
                            Picasso.get().load(groupIcon).into(groupIconIv)
                        }
                        catch (e:Exception) {
                            groupIconIv.setImageResource(R.drawable.sontung)
                        }
                    }

                }

            })


    }

    private fun loadCreateInfo(dateTime: String, createBy: String) {
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .orderByChild("uid")
            .equalTo(createBy).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ss in snapshot.children) {
                        val name = "" + ss.child("fullname").value
                        createByTv.text = "Create by $name on $dateTime"
                    }

                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}