package com.example.myapplication.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.myapplication.entity.AudioBean
import com.example.myapplication.view.PopListItemView

class PopAdapter(var playList: List<AudioBean>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var itemView: PopListItemView? = null
        if (convertView == null) {
            itemView = PopListItemView(parent?.context)
        } else {
            itemView = convertView as PopListItemView
        }
        itemView.setData(playList.get(position))
        return itemView
    }

    override fun getItem(position: Int): Any {
        return playList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return playList.size
    }
}