package com.lamhong.viesocial.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lamhong.viesocial.Models.ShortVideo
import com.lamhong.viesocial.R
import com.squareup.picasso.Picasso
import androidx.annotation.NonNull
import com.lamhong.viesocial.ShotVideoActivity

class ShotVideoListAdapter (private val mContext : Context, private val mListShotVideo : List<ShortVideo>)
    :RecyclerView.Adapter<ShotVideoListAdapter.ViewHolder>(){
    inner class ViewHolder(@NonNull itemview : View) : RecyclerView.ViewHolder(itemview){
        var image_thumb : ImageView
        var views: TextView
        init{
            image_thumb= itemview.findViewById(R.id.image_thumb)
            views=itemview.findViewById(R.id.num_views)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View= LayoutInflater.from(mContext).inflate(R.layout.layout_item_shot_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mListShotVideo.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shotVideo = mListShotVideo[position]
        Picasso.get().load(shotVideo.getThumb()).placeholder(R.color.black).into(holder.image_thumb)
        holder.views.text= shotVideo.getViews().toString() + " lượt xem"

        holder.image_thumb.setOnClickListener{
            val intent = Intent(mContext, ShotVideoActivity::class.java)
            intent.putExtra("type", shotVideo.getID())
            mContext.startActivity(intent)

        }
    }
}