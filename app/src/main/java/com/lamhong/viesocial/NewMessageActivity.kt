package com.lamhong.viesocial

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        setSupportActionBar(toolbar_new_message)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu_new_message,menu)



/*        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.new_message_search)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        //val queryTextListener = SearchView.OnQueryTextListener

        val queryTextListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {

            }

            override fun onQueryTextSubmit(query: String): Boolean {

            }
        }*/

        val item = menu?.findItem(R.id.new_message_search)
        val searchView: SearchView = MenuItemCompat.getActionView(item) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!TextUtils.isEmpty(query?.trim())) {
                    searchUsers(query)
                }
                else {
                    fetchUsers()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!TextUtils.isEmpty(newText?.trim())) {
                    searchUsers(newText)
                }
                else {
                    fetchUsers()
                }
                return true
            }


        })






        return super.onCreateOptionsMenu(menu)
    }

    private fun searchUsers(newText: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/UserInformation")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                var senderUid: String? = FirebaseAuth.getInstance().uid

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    user!!.setName(it.child("fullname").value.toString())
                    user!!.setUid(it.child("uid").value.toString())
                    if (user!=null && user.getUid()!=senderUid){

                        if (user.getName().toLowerCase().contains(newText.toLowerCase())) {
                            adapter.add(UserItem(user))
                        }

                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context,ChatLogActivity::class.java)

                    intent.putExtra("name",userItem.user.getName())
                    intent.putExtra("uid",userItem.user.getUid())
                    intent.putExtra("image",userItem.user.getAvatar())

                    startActivity(intent)

                    finish()
                }


                rv_search_new_user.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/UserInformation")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                var senderUid: String? = FirebaseAuth.getInstance().uid

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    user!!.setName(it.child("fullname").value.toString())
                    user!!.setUid(it.child("uid").value.toString())
                    if (user!=null && user.getUid()!=senderUid){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context,ChatLogActivity::class.java)

                    intent.putExtra("name",userItem.user.getName())
                    intent.putExtra("uid",userItem.user.getUid())
                    intent.putExtra("image",userItem.user.getAvatar())

                    startActivity(intent)

                    finish()
                }


                rv_search_new_user.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.user_name_new_message.text = user.getName()
        Picasso.get().load(user.getAvatar()).into(viewHolder.itemView.profile_image_new_message)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}