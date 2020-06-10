package com.example.myapplication

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

 class MainActivity : AppCompatActivity(),ListAdapter.OnItemListener {
    var isShow:Boolean=true;
    var status:Int=0;
    private var adapter:ListAdapter?=null
    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

     private fun test() {
         tv_test!!.setText("我是测试文本")
         tv_test.setTextSize(20f)
         tv_test.setTextColor(resources.getColor(R.color.colorPrimaryDark))

         recycleview.layoutManager = LinearLayoutManager(this)
         //        textView.setOnClickListener {object :View.OnClickListener{
         //            override fun onClick(v: View?) {
         //                TODO("Not yet implemented")
         //                Toast.makeText(this@MainActivity,"测试点击事件",Toast.LENGTH_SHORT)
         //            }
         //
         //        } }
         //创建适配器
         adapter = ListAdapter(list, this)
         adapter!!.setMyListener {
             Toast.makeText(this, it.age, Toast.LENGTH_SHORT).show()
         }
         adapter!!.setOnItemListener(this)
         //绑定适配器
         recycleview.adapter = adapter
         var person = Person();
         person.age
         getSharedPreferences("this", Context.MODE_PRIVATE)
             .edit()
             .putString("test", person.age.toString())
             .apply()

         val string = getSharedPreferences("this", Context.MODE_PRIVATE)
             .getString("test", "暂无")
         Log.i("test", string);
         //获取sp
         var sp: SharedPreferences;
         tv_test.setOnClickListener {
             //            if (isShow){
     //            Toast.makeText(this,"测试点击事件",Toast.LENGTH_SHORT).show()
     //            }else{
     //            Log.i("test","测试点击事件")
     //            }
             when (status) {
     //                0->Toast.makeText(this,"测试点击事件",Toast.LENGTH_SHORT).show()
                 0 -> Toast.makeText(this, person.age.toString(), Toast.LENGTH_SHORT).show()
             }
         }
     }


     override fun onResume() {
        super.onResume()
        loadData()
    }
   private var list=ArrayList<Int>();
    private fun loadData() {
//         var list:List<Int>
        list.clear()
        for (index in 1..5){
            list.add(index)
        }
        adapter!!.notifyDataSetChanged()
    }
        override fun mAction(get: Int) {
            Toast.makeText(this,list.get(get).toString(),Toast.LENGTH_SHORT).show()
        }

       fun isAPPALive(mContext: MainActivity, packageName: String): Boolean {
         var isAPPRunning = false
         // 获取activity管理对象
         val activityManager =
             mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
         // 获取所有正在运行的app
         val appProcessInfoList = activityManager.runningAppProcesses
         // 遍历，进程名即包名
         for (appInfo in appProcessInfoList) {
             if (packageName == appInfo.processName) {
                 isAPPRunning = true
                 break
             }
         }
         return isAPPRunning
     }

}
