package com.example.musicplayer.view.adapter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.view.adapter.viewholder.MediaViewHolder
import com.example.musicplayer.model.MediaFileModel

class MediaAdapter(private var items: ArrayList<MediaFileModel>): RecyclerView.Adapter<MediaViewHolder>() {

    private var musicClickListener: ((position: Int, item: MediaFileModel) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.vh_musics_and_videos, parent, false)
        return MediaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val singleItem = items[position]
        holder.onBind(singleItem, musicClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun musicClickListener(musicClickListener: (position: Int, item: MediaFileModel) -> Unit){
        this.musicClickListener = musicClickListener
    }

//
//    fun changeItem(position: Int){
//        notifyItemChanged(position)
//    }
}