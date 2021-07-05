package com.lamhong.mybook.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lamhong.mybook.GroupChatsLogActivity
import com.lamhong.mybook.Models.GroupChat
import com.lamhong.mybook.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_groupchats_list.view.*

class GroupChatAdapter(private val groupChatsList: ArrayList<GroupChat>) : RecyclerView.Adapter<GroupChatAdapter.GroupChatsViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_groupchats_list,parent,false)

        return GroupChatsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupChatsList.size
    }

    override fun onBindViewHolder(holder: GroupChatsViewHolder, position: Int) {
        val currentItem = groupChatsList[position]
        val groupID = currentItem.getGroupID()
        val groupIcon = currentItem.getGroupIcon()
        val groupTitle = currentItem.getGroupTitle()

        holder.groupTitleTv.text = groupTitle

        try {
            Picasso.get().load(groupIcon).into(holder.groupIconIv)
        }
        catch (e:Exception) {
            holder.groupIconIv.setImageResource(R.drawable.ic_group)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context,GroupChatsLogActivity::class.java)

            intent.putExtra("groupID",groupID)

            it.context.startActivity(intent)
        }

    }



    class GroupChatsViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupIconIv = itemView.image_group_chats
        val groupTitleTv = itemView.title_group_chats
        val groupMemberTv = itemView.member_group_chats
        val groupTimeTv = itemView.time_group_chats

    }

}