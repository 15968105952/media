package com.example.myapplication.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import com.example.myapplication.entity.AudioBean
import com.example.myapplication.entity.VbangItemView


class VbangAdapter(context: Context?, cursor: Cursor?) : CursorAdapter(context, cursor) {
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val itemView = view as VbangItemView
        val audioBean = AudioBean.getAudioBean(cursor)
        itemView.setData(audioBean)
    }

    /**
     * 创建条目view
     */
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return VbangItemView(context)
    }
}