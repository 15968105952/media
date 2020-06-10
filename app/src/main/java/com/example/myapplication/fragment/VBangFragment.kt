package com.example.myapplication.fragment

import android.Manifest
import android.content.AsyncQueryHandler
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.R
import com.example.myapplication.activity.AudioPlayerActivity
import com.example.myapplication.adapter.VbangAdapter
import com.example.myapplication.entity.AudioBean
import kotlinx.android.synthetic.main.fragment_vbang.*

class VBangFragment : BaseFragment() {
    override fun initView(): View? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return View.inflate(context, R.layout.fragment_vbang, null)
    }

    override fun initData() {
        //动态权限申请
        myToast("这是首页fragment")
        handlePermission()
    }

    private fun handlePermission() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        //查看是否拥有权限
        val checkSelfPermission =
            context?.let { ActivityCompat.checkSelfPermission(it, permission) }
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            //已经获取
            loadSongs()
        } else {
            //没有获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity as FragmentActivity,
                    permission
                )
            ) {
                //需要弹出提示
            } else {
                //获取权限
                myRequestPermission()
            }
        }
    }

    private fun myRequestPermission() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        }
    }

    private fun loadSongs() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //加载音乐列表数据
        val contentResolver = context?.contentResolver
        val handler = object : AsyncQueryHandler(contentResolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
                //查询完成回调  主线程中
                //打印数据
                //                CursorUtil.logCursor(cursor)
                //刷新列表
                //                (cookie as VbangAdapter).notifyDataSetChanged()
                //设置数据源
                //刷新adapter
                (cookie as VbangAdapter).swapCursor(cursor)
            }
        }
        //开始查询
        handler.startQuery(
            0, adapter, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST
            ), null, null, null
        )
    }

    var adapter: VbangAdapter? = null

    override fun initListener() {
        adapter = VbangAdapter(context, null)
        listView.adapter = adapter
        //设置条目点击事件
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            //获取数据集合
            val cursor = adapter?.getItem(position) as Cursor
            //通过当前位置cursor获取整个播放列表
            val audioBeans = AudioBean.getAudioBeans(cursor)
            //跳转到音乐播放界面
//            startActivity<AudioPlayerActivity>("list" to list,"position" to i)
            var intent = Intent(context, AudioPlayerActivity::class.java)
            intent.putExtra("list", audioBeans)
            intent.putExtra("position", position)
            context?.startActivity(intent)
        })
    }
}