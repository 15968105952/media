package com.example.myapplication.utils

import android.os.Build

import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
interface ToolBarManager {
    val toolbar: Toolbar


    fun initMainToolBar() {
        toolbar.setTitle("这是测试toolbar")
        toolbar.inflateMenu(R.menu.main)
        //kotlin 和java调用特性
        //如果java接口中只有一个未实现的方法  可以省略接口对象 直接用{}表示未实现的方法
        toolbar.setOnMenuItemClickListener {
            true
        }
    }

    fun initSettingToolbar() {
        toolbar.setTitle("设置界面")
    }
}