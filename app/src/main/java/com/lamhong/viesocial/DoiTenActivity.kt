package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_doi_ten.*
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.activity_help.btnSubmit
import kotlinx.android.synthetic.main.activity_help.btn_return_fromsetting

class DoiTenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doi_ten)
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
        btnSubmit.setOnClickListener {
            val name : String = txtname.text.toString().trim { it <= ' '}
            if (name.isEmpty()){
                Toast.makeText(this@DoiTenActivity,"Vui lòng nhập phản hồi", Toast.LENGTH_LONG).show()
            }else
            {
                val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
                val Ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("UserInformation")
                val Map = HashMap<String, Any>()
                Map["fullname"]= name
                Ref.child(currentUserID).updateChildren(Map).addOnCompleteListener{
                        task->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Đổi tên thành công" , Toast.LENGTH_LONG).show()
                    }
                    else{
                        val failmess= task.exception!!.toString()
                        Toast.makeText(this, "$failmess", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}