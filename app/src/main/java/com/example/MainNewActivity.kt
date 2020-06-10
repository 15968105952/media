package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.activity.AidlActivity
import com.example.myapplication.activity.AlarmClockActivity
import com.example.myapplication.adapter.TextTagAdapter
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.entity.ImpressTagEntity
import com.example.myapplication.view.TagFlowLayout
import com.example.myapplication.view.TaoFlowLayout
import kotlinx.android.synthetic.main.activity_main_new.*

class MainNewActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val list = ArrayList<String>()
    private val impressTagEntities = ArrayList<ImpressTagEntity>()
    private val classes = java.util.ArrayList<Class<out BaseActivity>>()
    private var textTagAdapter: TextTagAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)
        addList()
        setAdapter()
        addBean()
    }

    private fun addBean() {
        for (i in list.indices) {
            val impressTagEntity = ImpressTagEntity()
            impressTagEntity.setEnName(list[i])
            impressTagEntities.add(impressTagEntity)
        }
        if (null != impressTagEntities && impressTagEntities.size > 0) {
            Log.i(TAG, impressTagEntities.size.toString() + "")
            textTagAdapter?.notifyDataChanged()
        }
    }

    private fun setAdapter() {
        textTagAdapter = TextTagAdapter(this@MainNewActivity, impressTagEntities)
        tag_flow_layout?.adapter = textTagAdapter
        tag_flow_layout?.setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
            override fun onTagClick(view: View, position: Int, parent: TaoFlowLayout): Boolean {
                startActivity(Intent(this@MainNewActivity, classes[position]))
                return false
            }
        })

    }

    private fun addList() {
        list.add("AIDL")
        classes.add(AidlActivity::class.java)
        list.add("闹钟和进程保活")
        classes.add(AlarmClockActivity::class.java)
    }
}
