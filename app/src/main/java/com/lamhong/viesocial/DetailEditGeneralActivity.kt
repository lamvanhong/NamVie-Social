package com.lamhong.viesocial

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.UserInfor
import kotlinx.android.synthetic.main.activity_detail_edit_general.*
import kotlinx.android.synthetic.main.activity_detail_edit_general.btn_return
import kotlinx.android.synthetic.main.activity_detail_edit_general.tv_education
import kotlinx.android.synthetic.main.activity_detail_edit_general.tv_home
import kotlinx.android.synthetic.main.activity_detail_edit_general.tv_hometown

class DetailEditGeneralActivity : AppCompatActivity() {
    private var lstEducation  : ArrayList<String> = ArrayList()
    private var lstJob : ArrayList<String> = ArrayList()
    private var lstEducationValue : ArrayList<String> = ArrayList()
    private var edu1 : Boolean = false
    private var edu2 : Boolean = false
    private var edufinal : Boolean = false

    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_edit_general)
        firebaseUser= FirebaseAuth.getInstance().currentUser
        btn_return.setOnClickListener{
            this.finish()
        }
        // btn implement function
        btn_dahoc.setOnClickListener{
            startActivity(Intent(this, SchoolEditingActivity::class.java))
        }
        btn_dahoc1.setOnClickListener{
            startActivity(Intent(this, SchoolEditingActivity::class.java))
        }
        btn_dahoc2.setOnClickListener{
            startActivity(Intent(this, SchoolEditingActivity::class.java))
        }

        btn_addSchool.setOnClickListener{
            this.finish()
            if(edufinal){
                startActivity(Intent(this, SchoolEditingActivity::class.java))
            }else if(edu2){
                activityToDetail("education2")
            }
            else if(edu1){
                activityToDetail("education1")
            }
            else{
                activityToDetail("education")
            }

        }
        btn_addHome.setOnClickListener{
            activityToDetail("home")
        }
        btn_addwork.setOnClickListener{
            activityToDetail("job")
        }
        btn_AddHometown.setOnClickListener{
            activityToDetail("homeTown")
        }
        btn_addworkplace.setOnClickListener{
            activityToDetail("workPlace")
        }
        btn_addRelationship.setOnClickListener{
            activityToDetail("relationship")
        }
        getUserDetailInfor()

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
                    // another info
                    if(userInfor.getHome()!="null"){
                        tv_home.text=userInfor.getHome()
                        ic_home.setImageResource(R.drawable.icon_home_dart)
                        btn_addHome.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        btn_addHome.setText("Chỉnh sửa")
                    }
                    else{
                        ic_home.setImageResource(R.drawable.icon_home_light)
                        btn_addHome.setBackgroundColor(Color.parseColor("#00B0D1"))
                        btn_addHome.setText("Thêm")
                    }
                    if(userInfor.getHomeTown()!="null"){
                        tv_hometown.text=userInfor.getHomeTown()
                        ic_hometown.setImageResource(R.drawable.icon_hometown_dart)
                        btn_AddHometown.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        btn_AddHometown.setText("Chỉnh sửa")

                    }
                    else{
                        ic_hometown.setImageResource(R.drawable.icon_hometown_light)
                        btn_AddHometown.setBackgroundColor(Color.parseColor("#00B0D1"))
                        btn_AddHometown.setText("Thêm")
                    }
                    if(userInfor.getRelationship().toString()!="null"){
                        tv_relationship1.text=userInfor.getRelationship().toString()
                        ic_relationship.setImageResource(R.drawable.icon_relationship_dart)
                        btn_addRelationship.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        btn_addRelationship.setText("Chỉnh sửa")
                    }
                    else{
                        ic_relationship.setImageResource(R.drawable.icon_relationship_light)
                        btn_addRelationship.setBackgroundColor(Color.parseColor("#00B0D1"))
                        btn_addRelationship.setText("Thêm")
                    }
                    if(userInfor.getJob().toString()!="null"){
                        tv_job1.text=userInfor.getJob()
                        ic_job.setImageResource(R.drawable.icon_job)
                        btn_addwork.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        btn_addwork.setText("Chỉnh sửa")
                    }
                    else{
                        ic_job.setImageResource(R.drawable.icon_job_light)
                        btn_addwork.setBackgroundColor(Color.parseColor("#00B0D1"))
                        btn_addwork.setText("Thêm")
                    }
                    if(userInfor.getWorkPlace()!="null"){
                        tv_workplace1.text=userInfor.getWorkPlace()
                        ic_workplace.setImageResource(R.drawable.icon_workplace_dart)
                        btn_addworkplace.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        btn_addworkplace.setText("Chỉnh sửa")
                    }
                    else{
                        ic_workplace.setImageResource(R.drawable.icon_wordplace_light)
                        btn_addworkplace.setBackgroundColor(Color.parseColor("#00B0D1"))
                        btn_addworkplace.setText("Thêm")
                    }


                    //education
                    if(userInfor.getEducation()=="" || userInfor.getEducation()=="null" ){
                        cv_education.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
                        container_1a.visibility=View.GONE
                        container_2.visibility=View.GONE
                        cardview_dahoc.visibility=View.GONE
                        ic_education.setImageResource(R.drawable.icon_education_light)
                    }
                    else{
                       // cv_education.layoutParams.width=222
                        tv_education.text=userInfor.getEducation()
                        if(snapshot.child("education").child("status").value.toString() == "dahoc"){
                            btn_dahoc.setText("Đã học")
                            btn_dahoc.setTextColor(Color.parseColor("#03A9F4"))
                        }else {
                            btn_dahoc.setText("Đang học")
                            btn_dahoc.setTextColor(Color.parseColor("#F44336"))
                        }

                        edu1 = true
                        if(snapshot.child("education1").value!=null ) {
                            btn_addSchool.setBackgroundColor(Color.parseColor("#00B0D1"))
                            edu2=true
                            tv_education1.text = snapshot.child("education1").child("value").value.toString()

                            if(snapshot.child("education1").child("status").value.toString() == "dahoc"){
                                btn_dahoc1.setText("Đã học")
                                btn_dahoc1.setTextColor(Color.parseColor("#03A9F4"))
                            }else {
                                btn_dahoc1.setText("Đang học")
                                btn_dahoc1.setTextColor(Color.parseColor("#F44336"))
                            }

                        }
                        else{
                            container_1a.visibility= View.GONE
                            btn_addSchool.setBackgroundColor(Color.parseColor("#9CB5BA"))
                        }
                        if(snapshot.child("education2").value!=null){
                            edufinal=true
                            tv_education2.text = snapshot.child("education2").child("value").value.toString()
                            if(snapshot.child("education2").child("status").value.toString() == "dahoc"){
                                btn_dahoc2.setTextColor(Color.parseColor("#03A9F4"))
                                btn_dahoc2.setText("Đã học")
                            }else {
                                btn_dahoc2.setText("Đang học")
                                btn_dahoc2.setTextColor(Color.parseColor("#F44336"))
                            }
                            btn_addSchool.setText("Chỉnh sửa")
                            btn_addSchool.setBackgroundColor(Color.parseColor("#00B0D1"))
                            cv_education.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT

                        }
                        else
                        {
                            btn_addSchool.setBackgroundColor(Color.parseColor("#9CB5BA"))
                            container_2.visibility= View.GONE
                        }


                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun activityToDetail(type: String){
        val intent  = Intent(this, DetailUserInforChangeActivity::class.java)
        intent.putExtra("typechange", type)
        startActivity(intent)
    }
}