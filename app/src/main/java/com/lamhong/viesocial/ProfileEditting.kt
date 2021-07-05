package com.lamhong.viesocial

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.UserInfor
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_editting.*

class ProfileEditting : AppCompatActivity() {
    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editting)

        //init firebase
        firebaseUser= FirebaseAuth.getInstance().currentUser
        getUserDetailInfor()
        btn_return.setOnClickListener{
            finish()
        }

        circleImageAvatar.setOnClickListener{
            startActivity(Intent(this, ChangeAvatarActivity::class.java))
        }
        anhbia.setOnClickListener{
            startActivity(Intent(this, ChangeCoverImageActivity::class.java))
        }
        setAccountInfor()

        // bio changing
        btn_saveBio.setOnClickListener{
            if(edit_bio.text.length<1 ){
                Toast.makeText(this, "Vui lòng nhập nội dung trước !!", Toast.LENGTH_SHORT).show()
            }
            else{
                saveBio()
                edit_bio.clearFocus()
                val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                Toast.makeText(this, "Đã lưu thành công! ", Toast.LENGTH_SHORT).show()

            }
        }

        // detail infor changing
        change_detail.setOnClickListener{
            startActivity(Intent(this, DetailEditGeneralActivity::class.java))
        }
        tv_education_edit1.setOnClickListener{
            activityToDetail("education")
        }
        tv_job_edit.setOnClickListener{
            activityToDetail("job")
        }
        tv_home_edit1.setOnClickListener{
            activityToDetail("home")
        }
        tv_hometown_edit.setOnClickListener{
            activityToDetail("homeTown")
        }
        tv_relationship_edit.setOnClickListener{
            activityToDetail("relationship")
        }
        tv_workplace_edit.setOnClickListener{
            activityToDetail("workPlace")
        }
    }
    fun activityToDetail(type:String){
        val detailIntent = Intent(this, DetailUserInforChangeActivity::class.java)
        detailIntent.putExtra("typechange", type)
        startActivity(detailIntent)
    }

    private fun saveBio() {
        FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(firebaseUser.uid!!)
            .child("bio").setValue(edit_bio.text.toString())

    }
    private fun getUserDetailInfor(){
        val userDetailRef = FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(firebaseUser.uid!!)
        userDetailRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val userInfor : UserInfor = UserInfor()
                    userInfor!!.setEducation(snapshot.child("education").child("value").value.toString())
                    userInfor!!.setHome(snapshot.child("home").value.toString())
                    userInfor!!.setHomeTown(snapshot.child("homeTown").value.toString())
                    userInfor!!.setJob(snapshot.child("job").value.toString())
                    userInfor!!.setRelationship(snapshot.child("relationship").value.toString())
                    userInfor!!.setWorkPlace(snapshot.child("workPlace").value.toString())

                    // education info
                    if(userInfor.getEducation()!="null"){
                        if(snapshot.child("education").child("status").value.toString()!="dahoc"){
                            tv_education_edit1.text= userInfor.getEducation()
                        }
                        else{
                            tv_education_edit1.text= userInfor.getEducation()
                        }
                        ic_education_edit1.setImageResource(R.drawable.icon_home_dart)
                    }
                    else{
                        ic_education_edit1.setImageResource(R.drawable.icon_home_light)
                    }


                    if(snapshot.child("education1").child("value").value.toString()!="null"){
                        if(snapshot.child("education1").child("status").value.toString()!="dahoc"){
                            tv_education_edit2.text=snapshot.child("education1").child("value").value.toString()
                        }
                        else{
                            tv_education_edit2.text=snapshot.child("education1").child("value").value.toString()
                        }
                        ic_education_edit2.setImageResource(R.drawable.icon_home_dart)
                    }
                    else{
                        ic_education_edit2.setImageResource(R.drawable.icon_home_light)
                        container_edit2.visibility= View.GONE
                    }
                    if(snapshot.child("education2").child("value").value.toString()!="null"){
                        if(snapshot.child("education2").child("status").value.toString()!="dahoc"){
                            tv_education_edit3.text=snapshot.child("education2").child("value").value.toString()
                        }
                        else{
                            tv_education_edit3.text=snapshot.child("education2").child("value").value.toString()
                        }
                        ic_education_edit3.setImageResource(R.drawable.icon_home_dart)
                    }
                    else{
                        ic_education_edit3.setImageResource(R.drawable.icon_home_light)
                        container_edit2.visibility= View.GONE
                        container_edit3.visibility= View.GONE

                    }
                    // another info
                    if(userInfor.getHome()!="null"){
                        tv_home_edit1.text=userInfor.getHome()
                        ic_home_edit.setImageResource(R.drawable.icon_home_dart)
                    }
                    else{
                        ic_home_edit.setImageResource(R.drawable.icon_home_light)
                    }
                    if(userInfor.getHomeTown()!="null"){
                        tv_hometown_edit.text=userInfor.getHomeTown()
                        ic_hometown_edit.setImageResource(R.drawable.icon_hometown_dart)

                    }
                    else{
                        ic_hometown_edit.setImageResource(R.drawable.icon_hometown_light)
                    }
                    if(userInfor.getRelationship().toString()!="null"){
                        tv_relationship_edit.text=userInfor.getRelationship().toString()
                        ic_relationship_edit.setImageResource(R.drawable.icon_relationship_dart)
                    }
                    else{
                        ic_relationship_edit.setImageResource(R.drawable.icon_relationship_light)
                    }
                    if(userInfor.getJob().toString()!="null"){
                        tv_job_edit.text=userInfor.getJob()
                        ic_job_edit.setImageResource(R.drawable.icon_job)
                    }
                    else{
                        ic_job_edit.setImageResource(R.drawable.icon_job_light)
                    }
                    if(userInfor.getWorkPlace()!="null"){
                        tv_workplace_edit.text=userInfor.getWorkPlace()
                        ic_workplace_edit.setImageResource(R.drawable.icon_workplace_dart)
                    }
                    else{
                        ic_workplace_edit.setImageResource(R.drawable.icon_wordplace_light)
                    }


                    //education


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
        val userInforRef= FirebaseDatabase.getInstance().reference
            .child("UserDetails")
            .child(firebaseUser.uid!!)
        userInforRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user : UserInfor= UserInfor()
                    user!!.setBio(snapshot.child("bio").value.toString())

                    Picasso.get().load(snapshot.child("coverImage").value.toString()).placeholder(R.drawable.cty)
                        .into(anhbia)
                    edit_bio.setText(user!!.getBio())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}