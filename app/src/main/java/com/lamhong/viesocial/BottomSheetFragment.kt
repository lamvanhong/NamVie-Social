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
import kotlinx.android.synthetic.main.sheet_layout.*
import kotlinx.android.synthetic.main.sheet_layout.view.*

class BottomSheetFragment(private var mcontext : Context, private var type2: String, private var id: String
                    , private var publisher: String): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.sheet_layout, container, false)
        if(publisher==FirebaseAuth.getInstance().currentUser.uid){
            if(view.btn_remove!=null && view.btn_unfollow!=null)
            {
                view.btn_remove.visibility=View.GONE
                view.btn_unfollow.visibility=View.GONE
            }
        }
        else{
            if(view.btn_delete!=null)
                view.btn_delete.visibility=View.GONE
        }
        view.btn_delete.setOnClickListener{
            openDialog(Gravity.BOTTOM, mcontext, "deletepost_owner")
            this.dismiss()
        }
        view.btn_savePost.setOnClickListener{
            if(!check){
                check=true
                savePost()
                Toast.makeText(mcontext.applicationContext, "Đã lưu bài viết" , Toast.LENGTH_LONG).show()
            }
            else{
                check=false
                deleteSaved()
                Toast.makeText(mcontext.applicationContext, "Đã xóa bài viết khỏi mục đã lưu" , Toast.LENGTH_LONG).show()
            }

            this.dismiss()
        }
        view.btn_remove.setOnClickListener{
            openDialog(Gravity.BOTTOM, mcontext, "deletepost")
            this.dismiss()

        }
        view.btn_unfollow.setOnClickListener{
            openDialog(Gravity.BOTTOM, mcontext, "unfollow")
            this.dismiss()

        }
        checkSaveStatus()



        return view
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
        if(type=="unfollow"){
            dialog.name.text="Bạn chắc chắn hủy theo dõi người dùng này"
        }
        else if(type=="deletepost"){
            dialog.name.text="Bạn chắc chắn xóa bài viết"

        }
        else if(type=="deletepost_owner"){
            dialog.name.text="Bạn chắc chắn xóa bài viết của mình"
        }
        val yes = dialog.btn_yes
        val no = dialog.btn_no


        yes.setOnClickListener {
            if(type=="unfollow"){
                FirebaseDatabase.getInstance().reference.child("Friends").child(FirebaseAuth.getInstance().currentUser.uid)
                    .child("followingList").child(publisher).removeValue()
                Toast.makeText(mcontext.applicationContext, "Đã hủy theo dõi thành công" , Toast.LENGTH_LONG).show()
            }
            else if(type=="deletepost"){
                FirebaseDatabase.getInstance().reference.child("Contents").child("UserTimeLine")
                    .child(FirebaseAuth.getInstance().currentUser.uid).child(id).removeValue()
                Toast.makeText(mcontext.applicationContext, "Đã ẩn bài viết thành công" , Toast.LENGTH_LONG).show()

            }
            else if(type=="deletepost_owner"){
                FirebaseDatabase.getInstance().reference.child("Contents").child("Delete")
                    .child(id).setValue(true)
                Toast.makeText(mcontext.applicationContext, "Đã xóa bài viết thành công" , Toast.LENGTH_LONG).show()
            }

            dialog.dismiss()
        }

        no.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()

    }
    private var check=false
    fun checkSaveStatus(){
        val checkref= FirebaseDatabase.getInstance().reference.child("Contents").child("SavePost")
            .child(FirebaseAuth.getInstance().currentUser.uid)
        checkref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.child(id).exists()){
                     check=false
                    savePost.text="Lưu bài viết"
                    desSavePost.text="Lưu bài viết vào mục bài viết đã lưu"
                }
                else{
                    check=true
                    savePost.text="Xóa bài viết"
                    desSavePost.text="Xóa bài viết khỏi mục bài viết đã lưu"
                }
            }
        })
    }
    fun deleteSaved(){
        FirebaseDatabase.getInstance().reference.child("Contents").child("SavePost")
            .child(FirebaseAuth.getInstance().currentUser.uid).child(id).removeValue()
    }
    fun savePost(){
        val timelineUser= FirebaseDatabase.getInstance().reference.child("Contents").child("SavePost")
            .child(FirebaseAuth.getInstance().currentUser.uid)
        val pMap = HashMap<String, Any>()
        pMap["post_type"]=type2
        pMap["id"]=id
        pMap["active"]=true
        timelineUser.child(id).setValue(pMap)
   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}