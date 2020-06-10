package com.example.myapplication.fragment

import android.Manifest
import android.content.AsyncQueryHandler
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.R
import com.example.myapplication.activity.VideoPlayActivity
import com.example.myapplication.adapter.VideoListAdapter
import com.example.myapplication.entity.VideoBean
import kotlinx.android.synthetic.main.simple_listview.*

class VideoListFragment : BaseFragment() {
    var adapter: VideoListAdapter? = null
    override fun initView(): View? {
        return View.inflate(context, R.layout.video_list, null)
    }

    override fun initData() {
        handlePermission()
    }

    override fun initListener() {
        adapter = VideoListAdapter(context, null)
        simple_listview.adapter = adapter
        simple_listview.setOnItemClickListener { parent, view, position, id ->
            val cursor = adapter?.getItem(position) as Cursor
            val videoBeans = VideoBean.getVideoBeans(cursor)
            var intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("videoBeans", videoBeans)
            startActivity(intent)

        }
    }


    private fun handlePermission() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        //查看是否拥有权限
        val checkSelfPermission =
            context?.let { ActivityCompat.checkSelfPermission(it, permission) }
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            //已经获取
            getVideoList()
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

    private fun getVideoList() {
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
                (cookie as VideoListAdapter).swapCursor(cursor)
            }
        }
        handler.startQuery(
            0, adapter, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA
            ), null, null, null
        )

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
            getVideoList()
        }
    }
}