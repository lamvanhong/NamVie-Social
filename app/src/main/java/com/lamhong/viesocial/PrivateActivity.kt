package com.lamhong.viesocial

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_private.*

class PrivateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)
        card11.setOnClickListener {


            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("post").setValue("public")
            Toast.makeText(this, "Đã chuyển người có thể xem bài viết sang công khai", Toast.LENGTH_SHORT).show()
        }
        checkStatus()
        card12.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("post").setValue("friend")
            Toast.makeText(this, "Đã chuyển người có thể xem bài viết sang bạn bè", Toast.LENGTH_SHORT).show()

        }
        card13.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("post").setValue("private")
            Toast.makeText(this, "Đã chuyển người có thể xem bài viết sang riêng tư", Toast.LENGTH_SHORT).show()

        }

        card21.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("friend").setValue("public")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách bạn bè sang công khai", Toast.LENGTH_SHORT).show()

        }
        card22.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("friend").setValue("friend")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách bạn bè sang bạn bè", Toast.LENGTH_SHORT).show()

        }
        card23.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("friend").setValue("private")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách bạn bè sang riêng tư", Toast.LENGTH_SHORT).show()

        }

        card31.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("follow").setValue("public")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách theo dõi sang công khai", Toast.LENGTH_SHORT).show()


        }
        card32.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("follow").setValue("friend")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách theo dõi sang bạn bè", Toast.LENGTH_SHORT).show()

        }
        card33.setOnClickListener {

            // backend
            FirebaseDatabase.getInstance().reference.child("Contents")
                .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
                .child("follow").setValue("private")
            Toast.makeText(this, "Đã chuyển người có thể xem danh sách theo dõi sang riêng tư", Toast.LENGTH_SHORT).show()

        }
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
    }

    private fun checkStatus() {
        val ref= FirebaseDatabase.getInstance().reference.child("Contents")
            .child("User").child(FirebaseAuth.getInstance().currentUser.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("follow").exists()){
                    if(snapshot.child("follow").value=="public"){
                        tv31.setTextColor(Color.parseColor("#03A9F4"))
                        tv32.setTextColor(Color.parseColor("#808080"))
                        tv33.setTextColor(Color.parseColor("#808080"))
                        card31.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card32.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card33.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("follow").value=="friend"){
                        tv32.setTextColor(Color.parseColor("#03A9F4"))
                        tv31.setTextColor(Color.parseColor("#808080"))
                        tv33.setTextColor(Color.parseColor("#808080"))
                        card32.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card31.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card33.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("follow").value=="private"){
                        tv33.setTextColor(Color.parseColor("#03A9F4"))
                        tv32.setTextColor(Color.parseColor("#808080"))
                        tv31.setTextColor(Color.parseColor("#808080"))
                        card33.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card32.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card31.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }

                    if(snapshot.child("friend").value=="public"){
                        tv21.setTextColor(Color.parseColor("#03A9F4"))
                        tv22.setTextColor(Color.parseColor("#808080"))
                        tv23.setTextColor(Color.parseColor("#808080"))
                        card21.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card22.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card23.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("friend").value=="friend"){
                        tv22.setTextColor(Color.parseColor("#03A9F4"))
                        tv21.setTextColor(Color.parseColor("#808080"))
                        tv23.setTextColor(Color.parseColor("#808080"))
                        card22.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card21.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card23.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("friend").value=="private"){
                        tv23.setTextColor(Color.parseColor("#03A9F4"))
                        tv22.setTextColor(Color.parseColor("#808080"))
                        tv21.setTextColor(Color.parseColor("#808080"))
                        card23.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card22.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card21.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }

                    if(snapshot.child("post").value=="public"){
                        tv11.setTextColor(Color.parseColor("#03A9F4"))
                        tv12.setTextColor(Color.parseColor("#808080"))
                        tv13.setTextColor(Color.parseColor("#808080"))
                        card11.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card12.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card13.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("post").value=="friend"){
                        tv12.setTextColor(Color.parseColor("#03A9F4"))
                        tv11.setTextColor(Color.parseColor("#808080"))
                        tv13.setTextColor(Color.parseColor("#808080"))
                        card12.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card11.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card13.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                    else if(snapshot.child("post").value=="private"){
                        tv13.setTextColor(Color.parseColor("#03A9F4"))
                        tv12.setTextColor(Color.parseColor("#808080"))
                        tv11.setTextColor(Color.parseColor("#808080"))
                        card13.setCardBackgroundColor(Color.parseColor("#E0F5FF"))
                        card12.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        card11.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                    }
                }
            }
        })
    }
}