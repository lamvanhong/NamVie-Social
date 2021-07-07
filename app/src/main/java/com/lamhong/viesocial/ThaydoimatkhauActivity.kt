package com.lamhong.viesocial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_thaydoimatkhau.*

class ThaydoimatkhauActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thaydoimatkhau)
        btn_return_fromsetting.setOnClickListener{
            this.finish()
        }
        btnSubmit.setOnClickListener {
            val user : FirebaseUser = FirebaseAuth.getInstance().currentUser
            FirebaseAuth.getInstance().sendPasswordResetEmail(user.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(
                            this@ThaydoimatkhauActivity,
                            "Email sent successfully to reset your password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(this@ThaydoimatkhauActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
}