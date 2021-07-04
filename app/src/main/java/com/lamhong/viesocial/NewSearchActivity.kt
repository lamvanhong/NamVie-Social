package com.lamhong.viesocial

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_search)

        recyclerView= findViewById(R.id.recycleView_newSearch)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(this)

        _users= ArrayList()
        userAdapter= this?.let { UserAdapter(it, _users as ArrayList<User> , true) }
        recyclerView?.adapter= userAdapter

        edit_newSearch.requestFocusFromTouch()
        val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        btn_return_newsearch.setOnClickListener{
            this.finish()
        }

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

    }
    fun searchUser(datainput : String){
        val getdata= FirebaseDatabase.getInstance().getReference().child("UserInformation").orderByChild("fullname")
            .startAt(datainput).endAt(datainput+ "\uf8ff")
        getdata.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _users?.clear()
                for (snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)

                    user?.setName(snap.child("fullname").value.toString())
                    user?.setEmail(snap.child("email").value.toString())

                    if (user != null) {
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