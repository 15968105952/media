package com.example.myapplication.popwindow

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import com.example.myapplication.R
import com.example.myapplication.adapter.PopAdapter
import org.jetbrains.anko.find

class PlayListPopWindow(
    context: Context,
    popAdapter: PopAdapter?,
    listener: AdapterView.OnItemClickListener,
    val window: Window
) : PopupWindow() {
    //记录当前程序窗体透明度
    var alpha: Float = 0f

    init {
        //记录当前窗体的透明度
        alpha = window.attributes.alpha
        //设置布局
        val inflate = LayoutInflater.from(context).inflate(R.layout.pop_playlist, null, false)
        //获取listview
        val listView = inflate.find<ListView>(R.id.listView)
        //设置adapter
        listView.adapter = popAdapter
        //设置列表的条目点击事件
        listView.setOnItemClickListener(listener)
        contentView = inflate
        //设置宽度和高度
        width = ViewGroup.LayoutParams.MATCH_PARENT
        //设置高度为屏幕高度的3/5
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        val windowHeight = point.y
        height = windowHeight * 3 / 5
        //设置获取焦点
        isFocusable=true
        //设置外部点击
        isOutsideTouchable=true
        //能够响应返回按钮
        setBackgroundDrawable(ColorDrawable())
        //设置动画
        animationStyle=R.style.pop
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        //popWindow已经显示,设置透明度
        val attributes = window.attributes
        attributes.alpha=0.3f
        //设置到应用程序窗体上
        window.attributes=attributes
    }

    override fun dismiss() {
        super.dismiss()
        //popwindow隐藏，恢复应用程序窗体透明度
        val attributes = window.attributes
        attributes.alpha=alpha
        window.attributes=attributes
    }
}