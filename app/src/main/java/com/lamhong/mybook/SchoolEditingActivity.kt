package com.lamhong.mybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_school_editing.*

class SchoolEditingActivity : AppCompatActivity() {
    lateinit var firebaseUser : FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_editing)
        firebaseUser= FirebaseAuth.getInstance().currentUser
        btn_return.setOnClickListener{
            this.finish()
            startActivity(Intent(this, DetailEditGeneralActivity::class.java))
        }
        getAndSetInfor()

        // change status learning
        chk_1.setOnClickListener{
            if(chk_1.isChecked){
                setStatusToEducation("education","danghoc")
            }
            else{
                setStatusToEducation("education","dahoc")
            }
        }
        chk_2.setOnClickListener{
            if(chk_2.isChecked){
                setStatusToEducation("education1","danghoc")
            }
            else{
                setStatusToEducation("education1","dahoc")
            }
        }
        chk_3.setOnClickListener{
            if(chk_3.isChecked){
                setStatusToEducation("education2","danghoc")
            }
            else{
                setStatusToEducation("education2","dahoc")
            }
        }
        btn_xoa1.setOnClickListener{
            delete1()
        }
        btn_xoa2.setOnClickListener{
            delete2()
        }
        btn_xoa3.setOnClickListener{
            delete3()
        }
        btn_chinhsua1.setOnClickListener{
            val modifyIntent= Intent(this, DetailUserInforChangeActivity::class.java)
            if(btn_chinhsua1.text!="Thêm")
            {
                modifyIntent.putExtra("typechange", "education")
                modifyIntent.putExtra("itd_content", tv_typeName1.text.toString())
                if(chk_1.isChecked){
                    modifyIntent.putExtra("itd_status", "danghoc")
                }
                else{
                    modifyIntent.putExtra("itd_status", "dahoc")
                }
            }

            startActivity(modifyIntent)


        }
        btn_chinhsua2.setOnClickListener{
            val modifyIntent= Intent(this, DetailUserInforChangeActivity::class.java)
            if(btn_chinhsua2.text!="Thêm")
            {

                modifyIntent.putExtra("itd_content", tv_typeName2.text.toString())
                if(chk_2.isChecked){
                    modifyIntent.putExtra("itd_status", "danghoc")
                }
                else{
                    modifyIntent.putExtra("itd_status", "dahoc")
                }
            }
            modifyIntent.putExtra("typechange", "education1")

            startActivity(modifyIntent)

        }
        btn_chinhsua3.setOnClickListener{
            val modifyIntent= Intent(this, DetailUserInforChangeActivity::class.java)
            if(btn_chinhsua3.text!="Thêm"){

                modifyIntent.putExtra("itd_content", tv_typeName3.text.toString())
                if(chk_3.isChecked){
                    modifyIntent.putExtra("itd_status", "danghoc")
                }
                else{
                    modifyIntent.putExtra("itd_status", "dahoc")
                }
            }
            modifyIntent.putExtra("typechange", "education2")
            startActivity(modifyIntent)

        }


    }
    fun delete1(){
        if(tv_typeName3.text!="Trường học 3"){
            var status =""
            if(chk_2.isChecked){
                status="danghoc"
            }
            else {
                status="dahoc"
            }
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education").child("status").setValue(status)
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education").child("value").setValue(tv_typeName2.text.toString())

            var status1 =""
            if(chk_3.isChecked){
                status1="danghoc"
            }
            else {
                status1="dahoc"
            }
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").child("status").setValue(status1)
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").child("value").setValue(tv_typeName3.text.toString())
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education2").removeValue()
        }
        else if(tv_typeName2.text!="Trường học 2"){
            var status =""
            if(chk_2.isChecked){
                status="danghoc"
            }
            else {
                status="dahoc"
            }
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education").child("status").setValue(status)
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education").child("value").setValue(tv_typeName2.text.toString())
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").removeValue()

        } else{
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education").removeValue()
        }
        this.recreate()
    }
    fun delete2(){
        if(tv_typeName3.text!="Trường học 3"){
            var status =""
            if(chk_3.isChecked){
                status="danghoc"
            }
            else {
                status="dahoc"
            }
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").child("status").setValue(status)
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").child("value").setValue(tv_typeName3.text.toString())
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education2").removeValue()
            this.recreate()
        }
        else{
            FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
                .child("education1").removeValue()
            this.recreate()
        }

    }
    fun delete3(){
        FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
            .child("education2").removeValue()
        this.recreate()
    }
    fun setStatusToEducation(edu: String, status: String){
        FirebaseDatabase.getInstance().reference.child("UserDetails").child(firebaseUser.uid!!)
            .child(edu).child("status").setValue(status)
    }

    private fun getAndSetInfor() {
        val ref= FirebaseDatabase.getInstance().reference.child("UserDetails")
            .child(firebaseUser.uid!!)
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("education").child("value").value.toString()!="null"){
                        tv_typeName1.text=snapshot.child("education").child("value").value.toString()
                        if(snapshot.child("education").child("status").value.toString()=="dahoc"){
                            chk_1.isChecked=false
                        }
                        else
                        {
                            chk_1.isChecked=true
                        }

                    }
                    else{
                        container_1.visibility= View.GONE
                        btn_chinhsua1.setText("Thêm")
                    }
                    if(snapshot.child("education1").child("value").value.toString()!="null"){
                        tv_typeName2.text=snapshot.child("education1").child("value").value.toString()
                        if(snapshot.child("education1").child("status").value.toString()=="dahoc"){
                            chk_2.isChecked=false
                        }
                        else
                        {
                            chk_2.isChecked=true
                        }

                    }
                    else{
                        container_2.visibility= View.GONE
                        btn_chinhsua2.setText("Thêm")
                        if(btn_chinhsua1.text=="Thêm"){
                            cardview_2.visibility=View.GONE
                        }
                    }
                    if(snapshot.child("education2").child("value").value.toString()!="null"){
                        tv_typeName3.text=snapshot.child("education2").child("value").value.toString()
                        if(snapshot.child("education2").child("status").value.toString()=="dahoc"){
                            chk_3.isChecked=false
                        }
                        else
                        {
                            chk_3.isChecked=true
                        }

                    }
                    else{
                        container_3.visibility= View.GONE
                        btn_chinhsua3.setText("Thêm")
                        if(btn_chinhsua2.text=="Thêm"){
                            cardview_3.visibility=View.GONE
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}