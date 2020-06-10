package com.example.myapplication.entity

import android.content.Context
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.myapplication.R
import com.example.myapplication.entity.AudioBean
import kotlinx.android.synthetic.main.item_vbang.view.*


/**
 * ClassName:VbangItemView
 * Description:
 */
class VbangItemView:RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    init {
        View.inflate(context, R.layout.item_vbang,this)
    }

    /**
     * view+data绑定
     */
    fun setData(itemBean: AudioBean) {
        //歌曲名
        title.text = itemBean.display_name
        //歌手名
        artist.text = itemBean.artist
        //歌曲大小
        size.text = Formatter.formatFileSize(context,itemBean.size)
    }
}