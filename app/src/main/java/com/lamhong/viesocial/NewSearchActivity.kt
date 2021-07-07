package com.lamhong.viesocial

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.UserAdapter
import com.lamhong.viesocial.Models.User
import kotlinx.android.synthetic.main.activity_new_search.*

class NewSearchActivity : AppCompatActivity() {

    private var recyclerView : RecyclerView?=null
    private var userAdapter : UserAdapter ?=  null
    private var _users : MutableList<User> ?=null

    private var blockList : ArrayList<String> = ArrayList()
    private var filter : String="home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_search)

        recyclerView= findViewById(R.id.recycleView_newSearch)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(this)

        _users= ArrayList()
        userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
        recyclerView?.adapter= userAdapter

        edit_newSearch.requestFocusFromTouch()
        val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        btn_return_newsearch.setOnClickListener{
            this.finish()
        }
        // init block list
        initBlockList()

        edit_newSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(edit_newSearch.text.toString() == ""){
                    _users!!.clear()
                    userAdapter!!.notifyDataSetChanged()

                }else
                {
                    recyclerView?.visibility= View.VISIBLE
                    searchUser(s.toString().toLowerCase())

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        // =========BUTTON FITER=========
        btn_home.setOnClickListener{
            resetAll()

            setBtnAppearanceSelected(btn_home)
            filter="home"
            userAdapter!!.notifyDataSetChanged()
            userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
            recyclerView?.adapter= userAdapter
        }
        btn_hometown.setOnClickListener{
            resetAll()

            setBtnAppearanceSelected(btn_hometown)
            filter="hometown"
            userAdapter!!.notifyDataSetChanged()
            userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
            recyclerView?.adapter= userAdapter
        }
        btn_work.setOnClickListener{
            resetAll()

            setBtnAppearanceSelected(btn_work)
            filter="work"
            userAdapter!!.notifyDataSetChanged()
            userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
            recyclerView?.adapter= userAdapter
        }
        btn_workplace.setOnClickListener{
            resetAll()

            setBtnAppearanceSelected(btn_workplace)
            filter="workplace"
            userAdapter!!.notifyDataSetChanged()
            userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
            recyclerView?.adapter= userAdapter
        }
        btn_school.setOnClickListener{
            resetAll()
            setBtnAppearanceSelected(btn_school)

            filter="school"
            userAdapter!!.notifyDataSetChanged()
            userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true,filter) }
            recyclerView?.adapter= userAdapter

        }




    }


    private fun initBlockList() {
        val ref= FirebaseDatabase.getInstance().reference.child("Friends")
            .child(FirebaseAuth.getInstance().currentUser.uid)
            .child("blockList")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    blockList.clear()
                    for (s in snapshot.children){
                        blockList.add(s.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    // =========SETTING BUTTON FILTER ===============
    fun setBtnAppearanceNonSelected(btn : AppCompatButton){
        btn.setTextColor(Color.parseColor("#989898"))
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
    fun setBtnAppearanceSelected(btn : AppCompatButton){

        btn.setTextColor(Color.parseColor("#FFFFFF"))
        btn.setBackgroundColor(Color.parseColor("#00BCD4"))
    }
    fun resetAll(){
        setBtnAppearanceNonSelected(btn_home)
        setBtnAppearanceNonSelected(btn_hometown)
        setBtnAppearanceNonSelected(btn_school)
        setBtnAppearanceNonSelected(btn_work)
        setBtnAppearanceNonSelected(btn_workplace)

    }

    fun searchUser(datainput : String){
        val getdata= FirebaseDatabase.getInstance().getReference().child("UserInformation")
            .orderByChild("fullname")
            .startAt(datainput).endAt(datainput+ "\uf8ff")
        getdata.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _users?.clear()
                for (snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)

                    user?.setName(snap.child("fullname").value.toString())
                    user?.setEmail(snap.child("email").value.toString())

                    if (user != null) {
                        var cc=false //temp
                       for (ind in blockList){
                           if(user.getUid()==ind){
                               cc=true
                               break
                           }
                       }
                        if(!cc)
                        _users?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}