package com.example.myapplication.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import com.example.myapplication.entity.VideoBean
import com.example.myapplication.view.VideoItemView

class VideoListAdapter(context: Context?, c: Cursor?) : CursorAdapter(context, c) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            return context?.let { VideoItemView(it) }!!
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val videoItemView = view as VideoItemView
        val videoBean=VideoBean.getVideoBean(cursor)
        videoItemView.setData(videoBean)
    }
}