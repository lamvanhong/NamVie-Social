package com.lamhong.mybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_detail_user_infor_change.*
import kotlinx.android.synthetic.main.activity_detail_user_infor_change.btn_return
import kotlinx.android.synthetic.main.activity_profile_editting.*

class DetailUserInforChangeActivity : AppCompatActivity() {
    private var lstSchool  : ArrayList<String> = ArrayList()
    lateinit var firebaseUser: FirebaseUser
    private var typeChange: String = ""
    private var itd_content: String =""
    private var itd_status: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user_infor_change)
        firebaseUser= FirebaseAuth.getInstance().currentUser
        typeChange= intent.getStringExtra("typechange").toString()
        itd_content= intent.getStringExtra("itd_content").toString()
        itd_status= intent.getStringExtra("itd_status").toString()
        if(itd_content!="null"){
            edit_choose.setText(itd_content)
            if(itd_status=="danghoc"){
                chk_status.isChecked=true
            }
            else chk_status.isChecked=false
        }
        when(typeChange){
            "education" , "education1", "education2"->{
                img_typeEdit.setImageResource(R.drawable.icon_education_dart)
                tv_typeName.text="Thêm trường học"
            }
            "job" ->{
                img_typeEdit.setImageResource(R.drawable.icon_job)
                tv_typeName.text="Thêm công việc"
                container_checkbox.visibility=View.GONE
            }
            "relationship"->{
                container_checkbox.visibility=View.GONE
                img_typeEdit.setImageResource(R.drawable.icon_relationship_dart)
                tv_typeName.text="Sửa tình trạng cá nhân"
            }
            "home"->{
                container_checkbox.visibility=View.GONE
                img_typeEdit.setImageResource(R.drawable.icon_home_dart)
                tv_typeName.text="Sửa nơi ở"
            }
            "homeTown"->{
                container_checkbox.visibility=View.GONE
                img_typeEdit.setImageResource(R.drawable.icon_hometown_dart)
                tv_typeName.text="Sửa quê quán"
            }
            "workPlace"->{
                container_checkbox.visibility=View.GONE
                img_typeEdit.setImageResource(R.drawable.icon_workplace_dart)
                tv_typeName.text="Sửa nơi làm việc"
            }
        }
        btn_return.setOnClickListener{

            this.finish()
            if(checkEducation(typeChange) && itd_content=="null"){
                startActivity(Intent(this, DetailEditGeneralActivity::class.java))
            }
        }

        getShoolData(typeChange)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstSchool)

        edit_choose.setAdapter(adapter)
        btn_save_indetail.setOnClickListener{
            saveSchoolData(edit_choose.text.toString(),typeChange)
            saveFunc(typeChange)
            Toast.makeText(this, "Lưu thành công !!", Toast.LENGTH_SHORT).show()
            this.finish()
            if(checkEducation(typeChange) && itd_content=="null"){
                startActivity(Intent(this, DetailEditGeneralActivity::class.java))
            }



        }
    }
    fun checkEducation(type: String):Boolean{
        if(type=="education" || type=="education1" || type=="education2")
            return true
        return false
    }
    fun saveSchoolData(schoolname: String, type: String){
        if(type=="education" || type =="education1" || type =="education2"){
            FirebaseDatabase.getInstance().reference.child("Data").child("education")
                .child(schoolname).setValue(true)
        } else
        FirebaseDatabase.getInstance().reference.child("Data").child(type)
            .child(schoolname).setValue(true)
    }

    fun getShoolData(type: String){
        var thistype=type
        if(type=="education" || type =="education1" || type =="education2") {
            thistype="education"
        }
        val schooldataRef= FirebaseDatabase.getInstance().reference.child("Data")
            .child(thistype)
        schooldataRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    lstSchool.clear()
                    for (s in snapshot.children){
                        lstSchool.add(s.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // save
    private fun saveFunc(type : String) {
        if(type=="education" || type == "education1" || type=="education2"){
            var status : String =""
            if(chk_status.isChecked){
                status="danghoc"
            }else{
                status="dahoc"
            }
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).child("value").setValue(edit_choose.text.toString())
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).child("status").setValue(status)
        }else{
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).setValue(edit_choose.text.toString())
        }

    }

    fun saveEducation(schoolname: String ){
        val schoolRef= FirebaseDatabase.getInstance().reference.child("UserDetails")
            .child(firebaseUser.uid!!).child("education")
        schoolRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }
            }
        })
    }
}