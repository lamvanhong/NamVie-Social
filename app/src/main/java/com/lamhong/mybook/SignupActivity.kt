package com.lamhong.mybook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // startActivity(Intent(this, MainActivity::class.java))
        btnSignUp.setOnClickListener {
           CreateAccount();
        }
        txt_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
    fun CreateAccount(){
        val name= txt_input_name.text.toString()
        val email=txt_input_email.text.toString()
        val password = txt_input_password.text.toString()

        when{
            TextUtils.isEmpty(name)-> {
                Toast.makeText(this, "Vui lòng nhập tên bạn !!" , Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(email)-> {
                Toast.makeText(this, "Vui lòng nhập email !!" , Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(password)-> {
                Toast.makeText(this, "Vui lòng nhập mật khẩu !!" , Toast.LENGTH_LONG).show()
            }
            password!=txt_input_password_again.text.toString() ->{
                Toast.makeText(this, "Mật khẩu khác nhau " , Toast.LENGTH_LONG).show()
            }
            else ->{
                val processDialog = ProgressDialog(this@SignupActivity)
                processDialog.setTitle("Sign up")
                processDialog.setMessage("Chờ một lúc nhé ...")
                processDialog.setCanceledOnTouchOutside(false)
                processDialog.show()
                val _Auth :FirebaseAuth = FirebaseAuth.getInstance()

                _Auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        ImpletementSave(name, email  , processDialog)
                    }
                    else{
                         val failmess= task.exception!!.toString()
                         Toast.makeText(this, "$failmess", Toast.LENGTH_LONG).show()
                        _Auth.signOut()
                        processDialog.dismiss()
                    }

                }
            }
        }
    }
    fun ImpletementSave(name : String , email:String, processDialog : ProgressDialog){
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("UserInformation")
        val userMap = HashMap<String, Any>()
        userMap["uid"]= currentUserID
        userMap["fullname"]= name.toLowerCase()
        userMap["email"]= email.toLowerCase()
        userMap["avatar"]="https://firebasestorage.googleapis.com/v0/b/my-book-f1ef6.appspot.com/o/Images%2Fimages.jfif?alt=media&token=a3ad7508-6a86-4082-ab17-614f5bf9118b"



        userRef.child(currentUserID).setValue(userMap).addOnCompleteListener{
            task->

            if (task.isSuccessful){
                processDialog.dismiss()
                Toast.makeText(this, "Đã tạo tài khoản thành công." , Toast.LENGTH_LONG).show()
                val intent= Intent(this@SignupActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
            else{
                val failmess= task.exception!!.toString()
                Toast.makeText(this, "$failmess", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                processDialog.dismiss()
            }
        }

    }
}