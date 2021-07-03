package com.lamhong.viesocial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_editting.*

class ProfileEditting : AppCompatActivity() {
    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editting)

        //init firebase
        firebaseUser= FirebaseAuth.getInstance().currentUser

        btn_return.setOnClickListener{
            finish()
        }

        circleImageAvatar.setOnClickListener{
            startActivity(Intent(this, ChangeAvatarActivity::class.java))
        }
        setAccountInfor()
    }
    fun setAccountInfor(){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser.uid!!)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Picasso.get().load(snapshot.child("avatar").value.toString()).placeholder(R.drawable.cty)
                        .into(circleImageAvatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}