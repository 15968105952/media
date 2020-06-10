package com.example.myapplication.view

import android.content.Context
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.myapplication.R
import com.example.myapplication.entity.VideoBean
import com.example.myapplication.utils.StringUtils
import kotlinx.android.synthetic.main.video_list_item.view.*

class VideoItemView:RelativeLayout {
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr:Int):super(context,attributeSet,defStyleAttr)
    init {
        View.inflate(context, R.layout.video_list_item,this)
    }

    fun setData(videoBean: VideoBean) {
        video_list_item_tv_title.text=videoBean.title
        video_list_item_tv_size.text= videoBean.size?.let { Formatter.formatFileSize(context, it) }
        video_list_item_tv_duration.text= videoBean.duration?.let { StringUtils.formatTime(it) }
    }
}