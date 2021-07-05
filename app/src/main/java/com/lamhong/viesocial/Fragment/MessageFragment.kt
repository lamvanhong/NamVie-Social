package com.lamhong.viesocial.Fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.viesocial.Adapter.MessageUsersAdapter
import com.lamhong.viesocial.GroupChatsActivity
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.NewMessageActivity
import com.lamhong.viesocial.R
import kotlinx.android.synthetic.main.fragment_message.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_message, container,false)
        val mToolbar : Toolbar = view.findViewById<Toolbar>(R.id.toolbar_message)
        (requireActivity() as AppCompatActivity).setSupportActionBar(mToolbar)

        val users = ArrayList<User>()

        val adapter = MessageUsersAdapter(users)

        view.rv_message_users.layoutManager = LinearLayoutManager(activity)

        view.rv_message_users.adapter = adapter

        val usersmess = ArrayList<String>()

        var senderUid: String? = FirebaseAuth.getInstance().uid

        FirebaseDatabase.getInstance().reference.child("chats").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                usersmess.clear()
                for (ss in snapshot.children) {
                    val usermess = ss.key.toString()
                    usersmess.add(usermess)
                }
            }
        })



        FirebaseDatabase.getInstance().reference.child("UserInformation").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (ss in snapshot.children) {
                    val user = ss.getValue(User::class.java)
                    user!!.setName(ss.child("fullname").value.toString())
                    user!!.setUid(ss.child("uid").value.toString())
                    for (i in usersmess) {
                        if (user != null && (senderUid+user.getUid())==i) {
                            users.add(user)
                        }
                        //Toast.makeText(activity,i, Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })


        return view
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.new_message -> {
                startActivity(Intent(context, NewMessageActivity::class.java))
            }
/*            R.id.search_message -> {

            }*/

            R.id.group_message -> {
                startActivity(Intent(context, GroupChatsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_menu_message, menu)


       /* val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search_message).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo())
        }*/

        val item = menu.findItem(R.id.search_message)
        val searchView: SearchView = MenuItemCompat.getActionView(item) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(s: String): Boolean {

                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s)
                }
                else {

                    val users = ArrayList<User>()

                    val adapter = MessageUsersAdapter(users)

                    val usersmess = ArrayList<String>()

                    var senderUid: String? = FirebaseAuth.getInstance().uid

                    view?.rv_message_users?.layoutManager = LinearLayoutManager(activity)

                    view?.rv_message_users?.adapter = adapter

                    FirebaseDatabase.getInstance().reference.child("chats").addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            usersmess.clear()
                            for (ss in snapshot.children) {
                                val usermess = ss.key.toString()
                                usersmess.add(usermess)
                            }
                        }
                    })



                    FirebaseDatabase.getInstance().reference.child("UserInformation").addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            users.clear()
                            for (ss in snapshot.children) {
                                val user = ss.getValue(User::class.java)
                                user!!.setName(ss.child("fullname").value.toString())
                                user!!.setUid(ss.child("uid").value.toString())
                                for (i in usersmess) {
                                    if (user != null && (senderUid+user.getUid())==i) {
                                        users.add(user)
                                    }
                                    //Toast.makeText(activity,i, Toast.LENGTH_SHORT).show()
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }
                    })

                }

                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {

               if (!TextUtils.isEmpty(s.trim())) {
                   searchUsers(s)
               }
                else {

                   val users = ArrayList<User>()

                   val adapter = MessageUsersAdapter(users)

                   val usersmess = ArrayList<String>()

                   var senderUid: String? = FirebaseAuth.getInstance().uid

                   view?.rv_message_users?.layoutManager = LinearLayoutManager(activity)

                   view?.rv_message_users?.adapter = adapter

                   FirebaseDatabase.getInstance().reference.child("chats").addValueEventListener(object : ValueEventListener{
                       override fun onCancelled(error: DatabaseError) {
                       }

                       override fun onDataChange(snapshot: DataSnapshot) {
                           usersmess.clear()
                           for (ss in snapshot.children) {
                               val usermess = ss.key.toString()
                               usersmess.add(usermess)
                           }
                       }
                   })



                   FirebaseDatabase.getInstance().reference.child("UserInformation").addValueEventListener(object : ValueEventListener{
                       override fun onCancelled(error: DatabaseError) {

                       }

                       override fun onDataChange(snapshot: DataSnapshot) {
                           users.clear()
                           for (ss in snapshot.children) {
                               val user = ss.getValue(User::class.java)
                               user!!.setName(ss.child("fullname").value.toString())
                               user!!.setUid(ss.child("uid").value.toString())
                               for (i in usersmess) {
                                   if (user != null && (senderUid+user.getUid())==i) {
                                       users.add(user)
                                   }
                                   //Toast.makeText(activity,i, Toast.LENGTH_SHORT).show()
                               }
                           }
                           adapter.notifyDataSetChanged()
                       }
                   })

               }

                return true
            }
        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun searchUsers(s: String) {

        val users = ArrayList<User>()

        val adapter = MessageUsersAdapter(users)

        var senderUid: String? = FirebaseAuth.getInstance().uid

        view?.rv_message_users?.layoutManager = LinearLayoutManager(activity)

        view?.rv_message_users?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("UserInformation").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (ss in snapshot.children) {
                    val user = ss.getValue(User::class.java)
                    user!!.setName(ss.child("fullname").value.toString())
                    if (user != null && user.getUid()!=senderUid) {

                        if (user.getName().toLowerCase().contains(s.toLowerCase())) {
                            users.add(user)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}