package com.lamhong.mybook

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 0
    val ref  = FirebaseAuth.getInstance()
    private val auth:FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var gso : GoogleSignInOptions
    lateinit var googleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val googleSignIn = findViewById<SignInButton>(R.id.btnSignInGoogle)

        googleSignIn.setOnClickListener {
            signIn()
        }
        btnLogin.setOnClickListener {
            ImplementLogin()
        }
        txt_signup.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }
        tv_forgotpassword.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("UserInformation")
        userRef.orderByChild("uid").equalTo(user?.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val userMap = HashMap<String, Any>()
                    userMap["uid"]= user?.uid.toString()
                    userMap["fullname"]= user?.displayName.toString()
                    userMap["email"]= user?.email.toString()
                    userMap["avatar"]=user?.photoUrl.toString()
                    userRef.child(user?.uid.toString()).setValue(userMap).addOnCompleteListener { task ->
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        Toast.makeText(this, "Đăng nhập thành công.", Toast.LENGTH_LONG).show()
    }

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