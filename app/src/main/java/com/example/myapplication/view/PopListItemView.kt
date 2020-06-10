package com.example.myapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.myapplication.R
import com.example.myapplication.entity.AudioBean
import kotlinx.android.synthetic.main.item_pop.view.*

class PopListItemView : RelativeLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_pop, this)
    }

    fun setData(data: AudioBean) {
        title.text = data.display_name
        artist.text = data.artist
    }
}