package com.lamhong.viesocial

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin.setOnClickListener {
            ImplementLogin()
        }
        txt_signup.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    // not remain ding
    fun ImplementLogin(){
        val email = txt_input_email.text.toString()
        val password = txt_input_password.text.toString()
        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Vui lòng nhập email !!", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Vui lòng nhập password !!", Toast.LENGTH_LONG).show()
            }
            else ->{
            val processDialog = ProgressDialog(this@LoginActivity)
            processDialog.setTitle("Log in")
            processDialog.setMessage("Chờ một lúc nhé ...")
            processDialog.setCanceledOnTouchOutside(false)
            processDialog.show()
            val _Auth :FirebaseAuth = FirebaseAuth.getInstance()
                _Auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        task ->
                        if (task.isSuccessful){
                            processDialog.dismiss()
                            Toast.makeText(this, "Đăng nhập thành công." , Toast.LENGTH_LONG).show()

                            val intent= Intent(this@LoginActivity, MainActivity::class.java)
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
    }
    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser !=null){
            val intent= Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}