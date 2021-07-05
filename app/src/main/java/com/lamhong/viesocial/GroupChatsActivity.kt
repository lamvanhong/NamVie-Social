package com.lamhong.viesocial

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.GroupChatAdapter
import com.lamhong.viesocial.Models.GroupChat
import kotlinx.android.synthetic.main.activity_group_chats.*

class GroupChatsActivity : AppCompatActivity() {

    private val currentUid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chats)

        setSupportActionBar(toolbar_group_chats)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadGroupChat()
    }


    private fun loadGroupChat() {
        val groupchatsList = ArrayList<GroupChat>()

        FirebaseDatabase.getInstance().reference.child("groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    groupchatsList.clear()
                    for (ds in snapshot.children) {
                        if (ds.child("members").child(currentUid.toString()).exists()) {
                            val model = ds.getValue(GroupChat::class.java)
                            if (model != null) {
                                groupchatsList.add(model)
                            }
                        }
                    }

                    val adapter = GroupChatAdapter(groupchatsList)
                    rv_group_chats.layoutManager = LinearLayoutManager(this@GroupChatsActivity)
                    rv_group_chats.adapter = adapter
                    adapter.notifyDataSetChanged()
                }


            })
    }

    private fun searchGroupChat(query:String) {
        val groupchatsList = ArrayList<GroupChat>()

        FirebaseDatabase.getInstance().reference.child("groups")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    groupchatsList.size
                    for (ds in snapshot.children) {
                        if (ds.child("members").child(currentUid.toString()).exists()) {
                            if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                                val model = ds.getValue(GroupChat::class.java)
                                if (model != null) {
                                    groupchatsList.add(model)
                                }
                            }
                        }
                    }

                    val adapter = GroupChatAdapter(groupchatsList)
                    rv_group_chats.layoutManager = LinearLayoutManager(this@GroupChatsActivity)
                    rv_group_chats.adapter = adapter
                    adapter.notifyDataSetChanged()
                }


            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.top_menu_group_chats,menu)

        val item = menu?.findItem(R.id.search_group_chats)

        val searchView: SearchView = MenuItemCompat.getActionView(item) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!TextUtils.isEmpty(query?.trim())) {
                    if (query != null) {
                        searchGroupChat(query)
                    }
                }
                else {
                    loadGroupChat()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!TextUtils.isEmpty(newText?.trim())) {
                    if (newText != null) {
                        searchGroupChat(newText)
                    }
                }
                else {
                    loadGroupChat()
                }
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}