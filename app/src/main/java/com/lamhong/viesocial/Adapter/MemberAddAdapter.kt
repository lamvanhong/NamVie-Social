package com.lamhong.viesocial.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_member_add.view.*
import java.util.*


class MemberAddAdapter(private val userslist: ArrayList<User>, private val groupID: String, private val myGroupRole: String) : RecyclerView.Adapter<MemberAddAdapter.MemberAddViewHolder>() {

    class MemberAddViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarTv = itemView.avatar_group_add
        val nameTv = itemView.name_group_add
        val statusTv = itemView.statusTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAddViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_member_add,parent,false)
        return MemberAddAdapter.MemberAddViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userslist.size
    }

    override fun onBindViewHolder(holder: MemberAddViewHolder, position: Int) {
        val currentItem = userslist[position]

        val name = currentItem.getName()
        val image = currentItem.getAvatar()
        val uid = currentItem.getUid()

        holder.nameTv.text = name
        try {
            Picasso.get().load(image).into(holder.avatarTv)
        }
        catch (e:Exception) {
            holder.avatarTv.setImageResource(R.drawable.sontung)
        }

        checkIfAlreadyExists(currentItem,holder)

        holder.itemView.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("groups")
                .child(groupID)
                .child("members")
                .child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val hispreviousRole = "" + snapshot.child("role").value

                            var options: Array<String> = emptyArray()

                            val builder = AlertDialog.Builder(it.context)
                            builder.setTitle("Choose Option")
                            if (myGroupRole == "creator") {
                                if (hispreviousRole == "admin") {
                                    options = arrayOf("Xóa Admin","Xóa User")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        if (which==0) {
                                            removeAdmin(currentItem,it)
                                        } else {
                                            removeMember(currentItem,it)
                                        }
                                    }.show()
                                }
                                else if (hispreviousRole == "member") {
                                    options = arrayOf("Chọn Admin","Xóa User")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        if (which==0) {
                                            makeAdmin(currentItem,it)
                                        } else {
                                            removeMember(currentItem,it)
                                        }
                                    }.show()
                                }
                            }
                            else if (myGroupRole == "admin") {
                                if (hispreviousRole == "creator") {
                                    Toast.makeText(it.context,"Người sáng lập nhóm...",Toast.LENGTH_LONG).show()
                                }
                                else if (hispreviousRole == "admin") {
                                    options = arrayOf("Xóa Admin","Xóa User")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        if (which==0) {
                                            removeAdmin(currentItem,it)
                                        } else {
                                            removeMember(currentItem,it)
                                        }
                                    }.show()
                                }
                                else if (hispreviousRole == "member") {
                                    options = arrayOf("Chọn Admin","Xóa User")
                                    builder.setItems(
                                        options
                                    ) { dialog, which ->
                                        if (which==0) {
                                            makeAdmin(currentItem,it)
                                        } else {
                                            removeMember(currentItem,it)
                                        }
                                    }.show()
                                }
                            }

                        }
                        else {
                            val builder = AlertDialog.Builder(it.context)
                            builder.setTitle("Thêm thành viên")
                                .setMessage("Thêm người dùng này vào nhóm?")
                                .setPositiveButton("THÊM") {dialog, which ->
                                    addMember(currentItem,it)
                                }
                                .setNegativeButton("HỦY") {dialog, which ->
                                    dialog.dismiss()
                                }.show()
                        }
                    }

                })
        }
    }

    private fun addMember(currentItem: User, v: View) {
        val timestamp = "" + System.currentTimeMillis()
        val hashMap = hashMapOf<String, Any?>()
        hashMap["uid"] = currentItem.getUid()
        hashMap["role"] = "member"
        hashMap["timestamp"] = ""+timestamp
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID)
            .child("members")
            .child(currentItem.getUid())
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(v.context,"Thêm thành công",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(v.context,""+it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun makeAdmin(currentItem: User, v: View) {
        val timestamp = "" + System.currentTimeMillis()
        val hashMap = hashMapOf<String, Any?>()
        hashMap["role"] = "admin"
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID)
            .child("members")
            .child(currentItem.getUid())
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(v.context,"Người dùng này bây giờ là admin",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(v.context,""+it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeMember(currentItem: User, v: View) {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID)
            .child("members")
            .child(currentItem.getUid())
            .removeValue()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun removeAdmin(currentItem: User, v: View) {
        val timestamp = "" + System.currentTimeMillis()
        val hashMap = hashMapOf<String, Any?>()
        hashMap["role"] = "member"
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID)
            .child("members")
            .child(currentItem.getUid())
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(v.context,"Người dùng này không còn là admin",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(v.context,""+it.message,Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkIfAlreadyExists(currentItem: User, holder: MemberAddAdapter.MemberAddViewHolder) {
        FirebaseDatabase.getInstance().reference.child("groups")
            .child(groupID)
            .child("members")
            .child(currentItem.getUid())
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val hisRole = "" + snapshot.child("role").value
                        holder.statusTv.text = "($hisRole)"
                    }
                    else {
                        holder.statusTv.text = ""
                    }
                }

            })
    }

}