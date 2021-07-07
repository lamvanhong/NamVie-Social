package com.lamhong.viesocial

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_confirm_deletefriend.*
import kotlinx.android.synthetic.main.sheet_layout_profile.*
import kotlinx.android.synthetic.main.sheet_layout_profile.view.*

class BottomSheetFragmentProfile (private var mcontext : Context, private var id: String ): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.sheet_layout_profile, container, false)
        view.btn_block.setOnClickListener{
            if(checkblock){
                openDialog(Gravity.BOTTOM, mcontext, "unblock")

            }
            else{
                openDialog(Gravity.BOTTOM, mcontext, "block")

            }


            this.dismiss()
        }
        view.btn_theodoi.setOnClickListener{
            if(check){
                openDialog(Gravity.BOTTOM, mcontext, "unfollow")
            }
            else{
                FirebaseDatabase.getInstance().reference.child("Friends").child(FirebaseAuth.getInstance().currentUser.uid)
                    .child("followingList").child(id).setValue(true)
                Toast.makeText(mcontext.applicationContext, "Theo dõi người dùng thành công" , Toast.LENGTH_LONG).show()

            }

            this.dismiss()

        }

        checkfollowStatus()
        checkBlockStatus()



        return view
    }
    var check=false
    private fun checkfollowStatus() {
        val ref= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid).child("followingList")
            .child(id)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    check=true
                    if(tv_theodoi!=null || tv_theodois!=null){
                        tv_theodoi.text="Bỏ theo dõi"
                        tv_theodois.text="bỏ theo dõi người dùng này"
                    }

                }
                else {
                    check=false
                    if(tv_theodoi!=null || tv_theodois!=null){
                        tv_theodoi.text="Theo dõi"
                        tv_theodois.text="Theo dõi người dùng này"
                    }

                }
            }
        })
    }
    var checkblock=false
    private fun checkBlockStatus() {
        val ref= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid).child("blockList")
            .child(id)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    checkblock=true
                    if(tv_block !=null && tv_blocks!=null){
                        tv_block.text="Bỏ chặn"
                        tv_blocks.text="bỏ chặn người dùng này"
                    }

                }
                else {
                    checkblock=false
                    if(tv_block !=null && tv_blocks!=null){
                        tv_block.text="Chặn người dùng"
                        tv_blocks.text="Người dùng này không thể tìm bạn"
                    }

                }
            }
        })
    }


    private fun openDialog(gravity: Int, v:Context, type: String) {

        val dialog = Dialog(v)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_deletefriend)

        val window = dialog.window

        if (window==null) {
            return
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window.attributes
        windowAttributes.gravity = gravity

        window.attributes = windowAttributes

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true)
        }
        if(type=="block"){
            dialog.name.text="Chặn người dùng này"
            dialog.btn_yes.text="Xác nhận"
            dialog.btn_no.text="Từ bỏ"
        }
        else if(type=="unfollow"){
            dialog.name.text="Bạn chắc chắn hủy theo dõi"

        }
        else if(type=="unblock"){
            dialog.name.text="Bạn chắc chắn bỏ chặn"
            dialog.btn_yes.text="Xác nhận"
            dialog.btn_no.text="Từ bỏ"
        }

        val yes = dialog.btn_yes
        val no = dialog.btn_no


        yes.setOnClickListener {
            if(type=="block"){
                FirebaseDatabase.getInstance().reference.child("Friends").child(FirebaseAuth.getInstance().currentUser.uid)
                    .child("blockList").child(id).setValue(true)
                Toast.makeText(mcontext, "Đã block thành công" , Toast.LENGTH_LONG).show()
            }
            else if(type=="unfollow"){
                FirebaseDatabase.getInstance().reference.child("Friends").child(FirebaseAuth.getInstance().currentUser.uid)
                    .child("followingList").child(id).removeValue()
                Toast.makeText(mcontext.applicationContext, "Đã xóa theo dõi thành công" , Toast.LENGTH_LONG).show()

            }
            else if(type=="unblock"){
                FirebaseDatabase.getInstance().reference.child("Friends").child(FirebaseAuth.getInstance().currentUser.uid)
                    .child("blockList").child(id).removeValue()
                Toast.makeText(mcontext.applicationContext, "Đã bỏ block thành công" , Toast.LENGTH_LONG).show()
            }

            dialog.dismiss()
        }

        no.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }
}