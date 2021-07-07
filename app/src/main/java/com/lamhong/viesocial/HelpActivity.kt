package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
        btnSubmit.setOnClickListener {
            val feedback : String = txtphanhoi.text.toString().trim { it <= ' '}
            if (feedback.isEmpty()){
                Toast.makeText(this@HelpActivity,"Vui lòng nhập phản hồi", Toast.LENGTH_LONG).show()
            }else{
                val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
                val Ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("feedbacks")
                val Map = HashMap<String, Any>()
                Map["uid"]= currentUserID
                Map["content"]= feedback

                Ref.child(currentUserID).setValue(Map).addOnCompleteListener{
                        task->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Gửi phản hồi thành công" , Toast.LENGTH_LONG).show()
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