package com.lamhong.mybook

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import kotlinx.android.synthetic.main.activity_following_list.*

class FollowingListActivity : AppCompatActivity() {
    private var userID: String = ""
    private var lstFollowList : ArrayList<String> = ArrayList()
    private var lstFollowmeList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)
        btn_return_fromfollowlist.setOnClickListener{
            this.finish()
        }
        //innit something
        userID= intent.getStringExtra("userID").toString()




        //set button selected click
        btn_banbe_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= gridLayoutManager
            //recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE

        }
        btn_dagui_friendlist.setOnClickListener{
            setBtnAppearanceNonSelected(btn_dangcho_friendlist)
            setBtnAppearanceSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_waittingFriendAdapter
            // recyclerView1.visibility= View.GONE
        }

        btn_dangcho_friendlist.setOnClickListener{
            setBtnAppearanceSelected(btn_dangcho_friendlist)
            setBtnAppearanceNonSelected(btn_dagui_friendlist)
            setBtnAppearanceNonSelected(btn_tatca_friendlist)
            setBtnAppearanceNonSelected(btn_banbe_friendlist)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_confirmFriendAdapter
            // recyclerView1.visibility= View.GONE

        }

    }
    fun setBtnAppearanceNonSelected(btn : AppCompatButton){
        btn.setTextColor(Color.parseColor("#989898"))
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
    fun setBtnAppearanceSelected(btn : AppCompatButton){

        btn.setTextColor(Color.parseColor("#FFFFFF"))
        btn.setBackgroundColor(Color.parseColor("#00BCD4"))
    }
}