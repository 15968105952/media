package com.example.myapplication.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import com.example.myapplication.R
import com.example.myapplication.adapter.PopAdapter
import com.example.myapplication.entity.AudioBean
import com.example.myapplication.popwindow.PlayListPopWindow
import com.example.myapplication.service.AudioService
import com.example.myapplication.service.Iservice
import com.example.myapplication.utils.StringUtil
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_music_player_bottom.*
import kotlinx.android.synthetic.main.activity_music_player_middle.*
import kotlinx.android.synthetic.main.activity_music_player_top.*

class AudioPlayerActivity : BaseActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    AdapterView.OnItemClickListener {
    var duration: Int = 0
    var drawable: AnimationDrawable? = null
    var audioBean: AudioBean? = null
    val MSG_PROGRESS = 0
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> startUpdateProgress()
            }
        }

    }

    //开始更新进度
    private fun startUpdateProgress() {
        //获取当前进度
        val progress = isService?.getProgress() ?: 0
        //更新进度数据
        updateProgress(progress)
        //实时获取播放进度
        handler.sendEmptyMessage(MSG_PROGRESS)
    }

    override fun onClick(v: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (v?.id) {
            R.id.state -> updatePlayState()
            R.id.mode -> updatePlayMode()
            R.id.pre -> isService?.playPre()
            R.id.next -> isService?.playNext()
            R.id.playlist -> showPlayList()

        }
    }

    private fun showPlayList() {
        val playList = isService?.getPlayList()
        val popAdapter = playList?.let { PopAdapter(it) }
        //获取底部高度
        val height = audio_player_bottom.height
        val playListPopwindow = PlayListPopWindow(this, popAdapter, this, window)
        playListPopwindow.showAsDropDown(audio_player_bottom, 0, -height)

    }

    private fun updatePlayMode() {
        //更新service中的mode
        isService?.updatePlayMode()
        //更新本地UI
        updatePlayModeBtn()

    }

    //根据播放模式修改播放图标
    private fun updatePlayModeBtn() {
        isService?.let {
            //获取播放模式
            val playMode = isService?.getPlayMode()
            //根据不同状态展示
            when (playMode) {
                AudioService.MODE_ALL -> mode.setImageResource(R.drawable.selector_btn_playmode_order)
                AudioService.MODE_SINGLE -> mode.setImageResource(R.drawable.selector_btn_playmode_single)
                AudioService.MODE_RANDOM -> mode.setImageResource(R.drawable.selector_btn_playmode_random)
            }
        }
    }

    private fun updatePlayState() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //更新播放状态
        isService?.updatePlayState()
        //更新播放状态图标
        updatePlayStateBtn()
    }

    private fun updatePlayStateBtn() {
        //获取当前的播放状态
        val playing = isService?.isPlaying()
        playing?.let {
            //根据状态更新图标
            if (playing) {
                //展示播放图标
                state.setImageResource(R.drawable.selector_btn_audio_play)
                //开始播放动画
                drawable?.start()
                //开始更新进度
                handler.sendEmptyMessage(MSG_PROGRESS)
            } else {
                //展示暂停图标
                state.setImageResource(R.drawable.selector_btn_audio_pause)
                //停止播放动画
                drawable?.stop()
                //暂停进度
                handler.removeMessages(MSG_PROGRESS)
            }
        }
    }

    /**
     * 进度改变回调
     * progress:改变之后的进度
     * fromUser:true 通过用户手指拖动改变进度  false代表通过代码方式改变进度
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        //判断是否是用户操作
        if (!fromUser)
            return
        //更新播放进度
        isService?.seekTo(progress)
        updateProgress(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    /**
     * 弹出的播放列表条目点击事件
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //播放当前歌曲
        isService?.playPosition(position)
    }

    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        EventBus.getDefault().register(this)
        //获取传递过来的参数
        var intent = intent
        //开启并传入音乐服务中
        intent.setClass(this, AudioService::class.java)
        //绑定服务
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        //开启服务
        startService(intent)
    }

    val conn by lazy {
        AudioConnection()
    }

    override fun initListener() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //播放状态切换监听
        state.setOnClickListener(this)
        back.setOnClickListener { finish() }
        //进度条变化监听
        progress_sk.setOnSeekBarChangeListener(this)
        //播放模式点击事件监听
        mode.setOnClickListener(this)
        //上一曲和下一曲点击事件监听
        pre.setOnClickListener(this)
        next.setOnClickListener(this)
        //播放列表
        playlist.setOnClickListener(this)
        //歌词播放进度拖动监听
        lyricView.setProgressListener {
            //更新播放进度
            isService?.seekTo(it)
            //更新进度显示
            updateProgress(it)
        }
    }


    private fun updateProgress(it: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //更新进度数值
        progress.text = StringUtil.parseDuration(it) + "/" + StringUtil.parseDuration(duration)
        //更新进度条
        progress_sk.setProgress(it)
        //更新歌词播放进度
        lyricView.updateProgress(it)
    }

    override fun getLayoutId(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return R.layout.activity_audio_player
    }

    var isService: Iservice? = null

    inner class AudioConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            isService = service as Iservice
        }

    }

    //接受eventbus方法
    fun onEventMainThread(itemBean: AudioBean) {
        //设置播放歌曲名称
        itemBean.display_name?.let { lyricView.setSongName(it) }
        //记录播放歌曲的bean
        this.audioBean = itemBean
        //歌曲名
        audio_title.text = itemBean.display_name
        //歌手名
        artist.text = itemBean.artist
        //更新播放按钮状态
        updatePlayStateBtn()
        //动画播放
        drawable = audio_anim.drawable as AnimationDrawable
        if (isService?.isPlaying()!!) {
            drawable?.start()
        } else {
            drawable?.stop()
        }
        //获取总进度
        duration = isService?.getDuration() ?: 0
        //设置歌词播放总进度
        lyricView.setSongDuration(duration)
        //进度条设置进度最大值
        progress_sk.max = duration
        //更新播放进度
        startUpdateProgress()
        //更新播放模式图标
        updatePlayModeBtn()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

}

