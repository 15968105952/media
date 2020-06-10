package com.example.myapplication.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import com.example.myapplication.R
import com.example.myapplication.entity.VideoBean
import com.example.myapplication.utils.StringUtils
import com.nineoldandroids.view.ViewHelper
import com.nineoldandroids.view.ViewPropertyAnimator
import kotlinx.android.synthetic.main.activity_video_play.*
import kotlinx.android.synthetic.main.video_player_bottom.*
import kotlinx.android.synthetic.main.video_player_top.*


class VideoPlayActivity : BaseActivity(), View.OnClickListener {

    var videoBeans: ArrayList<VideoBean>? = null
    var position: Int = -1
    var audioManager: AudioManager? = null
    val msg_get_system_time: Int = 0
    val msg_update_play_progress: Int = 1
    var screenHalfWidth: Int = 0
    var screenHalfHeight: Int = 0
    var topHeight: Int = 0
    var bottomHeight: Int = 0
    var isShowing: Boolean = false
    var detector: GestureDetector? = null
    var myBatteryChangedBroadcastReceiver: MyBatteryChangedBroadcastReceiver? = null
    override fun initData() {
        videoBeans = intent.getParcelableArrayListExtra<VideoBean>("videoBeans")
        position = intent.getIntExtra("position", -1)
        //初始化viewhelper
        ViewHelper.setAlpha(video_player_cover, 0F)
        //播放当前视频
        playItem()
        //获取当前的系统时间
        getSystemTime()
        //初始化音量seekbar
        initVolumeSeekbar()
        //获取屏幕宽高
        screenHalfHeight = resources.displayMetrics.heightPixels / 2;
        screenHalfWidth = resources.displayMetrics.widthPixels / 2;
        //自动隐藏控制条
        initHideControl()

    }

    private fun initHideControl() {
        //顶部控制条- 隐藏 (负的自身高度)
        //手动测量获取控件高度
        video_player_ll_top.measure(0, 0)
        topHeight = video_player_ll_top.measuredHeight
        //底部控制条- 隐藏
        video_player_ll_bottom.measure(0, 0)
        bottomHeight = video_player_ll_bottom.measuredHeight
        hideControl()
    }

    private fun hideControl() {
        ViewPropertyAnimator.animate(video_player_ll_top).translationYBy(-topHeight.toFloat())
        ViewPropertyAnimator.animate(video_player_ll_bottom).translationYBy(bottomHeight.toFloat())
        isShowing = false
    }

    private fun initVolumeSeekbar() {
        /**
         * 1. 初始化seekbar
        获取系统 音乐音量
         */
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        //获取流音量
        val musicCurrentVolume = getStreamCurrentVolume()
        //获取最大音量
        val streamMaxVolume = getStreamMaxVolume()
        video_player_sk_volume.setProgress(musicCurrentVolume)
        video_player_sk_volume.max = streamMaxVolume!!
    }

    private fun getStreamMaxVolume(): Int? {
        return audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    }

    private fun getStreamCurrentVolume(): Int {
        return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
    }

    var handle = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                msg_get_system_time -> {
                    //获取系统时间
                    getSystemTime()
                }
                msg_update_play_progress -> {
                    //更新进度
                    updatePlayProgress()
                }

            }
        }
    }

    private fun getSystemTime() {
        /**
         * 	1. 获取当前系统时间,初始化控件tv
        System.currentTimeMillis();
        ms -- HH:mm:ss
        2. 循环获取更新
        handler 发生延迟消息 --如何形成循环-- 收到消息,再次发生消息
         */
        val formatSystemTime = StringUtils.formatSystemTime()
        video_player_tv_system_time.text = formatSystemTime
        //每隔1秒发送一次进行计时
        handle.sendEmptyMessageDelayed(msg_get_system_time, 1000)
    }

    private fun playItem() {
        val videoBean = videoBeans?.get(position)
        /**
         * 		如何播放视频?
        1. 设置视频路径 : setVideoPath()
        2. 准备
        3. 播放
         */
        video_player_videoview.setVideoPath(videoBean?.data)//本地路径
        //视频标题
        video_player_tv_title.text = videoBean?.title
    }

    var myVideoSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        /**
         * 开始拖拽
         * @param seekBar
         */
        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        /**
         * 拖拽停止
         * @param seekBar
         */
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

        /**
         *  进度改变
         * @param seekBar  当前的sk
         * @param progress 拖拽后的新进度
         * @param fromUser 是否来自于用户
         */
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            //如果是音量
            if (seekBar?.id == R.id.video_player_sk_volume) {
                /**
                 * 调整系统音乐音量
                 */
                setStreamCurrentVolume(progress)
            } else {
                //如果是进度
                //如果来自用户 : 一卡一卡滴播放
                if (fromUser) {
                    video_player_videoview.seekTo(progress)
                }
            }
        }

    }

    private fun setStreamCurrentVolume(progress: Int) {
        //1.streamType 2.当前音量 3.flag 1: 弹出系统音量调整对话框 0: 不弹出
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 1)
    }

    var myCompletionListener = object : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer?) {
            //更新当前进度 == 总进度
            val duration = video_player_videoview.duration
            video_player_tv_position.text = StringUtils.formatTime(duration.toLong())
            video_player_sk_position.setProgress(duration)
            //移除消息
            handle.removeMessages(msg_update_play_progress)
            video_player_iv_pause.setImageResource(R.drawable.video_play_selector)
        }
    }

    var myVideoPrepreadListener = object : MediaPlayer.OnPreparedListener {
        //异步准备,准备好之后回调此方法
        override fun onPrepared(mp: MediaPlayer?) {
            //开始播放
            video_player_videoview.start()
            //播放按钮 -- 暂停
            switchPlayAndPausePic()
            //更新播放进度
            updatePlayProgress()

        }

    }

    private fun updatePlayProgress() {
        /**
         * 俩tv,1sk
        当前进度tv,总时长tv
         * 	1. 初始化 播放进度控件
        播放进度什么时候初始化?
        开始播放之后.
        2. 形成循环
         */
        //如何获取当前进度,总时长?
        val currentPosition = video_player_videoview.currentPosition
        val duration = video_player_videoview.duration
        val currentPositionS = StringUtils.formatTime(currentPosition.toLong())
        val durationS = StringUtils.formatTime(duration.toLong())
        //数字进度展示
        video_player_tv_position.text = currentPositionS
        video_player_tv_duration.text = durationS
        //进度条进度展示
        video_player_sk_position.setProgress(currentPosition)
        video_player_sk_position.max = duration
        //循环获取展示
        handle.sendEmptyMessageDelayed(msg_update_play_progress, 1000)
    }

    private fun switchPlayAndPausePic() {
        if (video_player_videoview.isPlaying) {
            //正在播放:暂停图片
            video_player_iv_pause.setImageResource(R.drawable.video_pause_selector)
        } else {
            video_player_iv_pause.setImageResource(R.drawable.video_play_selector)
        }
    }

    override fun initListener() {
        //播放暂定监听
        video_player_iv_pause.setOnClickListener(this)
        //注册系统电量发生改变的广播接受者
        registerBatteryChangedBroadcastReceiver()
        //音量添加进度监听
        video_player_sk_volume.setOnSeekBarChangeListener(myVideoSeekbarChangeListener)
        //静音监听
        video_player_iv_mute.setOnClickListener(this)
        //进度sk添加进度改变监听
        video_player_sk_position.setOnSeekBarChangeListener(myVideoSeekbarChangeListener)
        //videoView添加播放完成监听
        video_player_videoview.setOnCompletionListener(myCompletionListener)
        //准备监听
        video_player_videoview.setOnPreparedListener(myVideoPrepreadListener)
        //注册手势器
        //单击
        detector = object : GestureDetector(this, MySimpleOnGestureListener()) {

        }

        //上一曲,下一曲,点击监听
        video_player_iv_pre.setOnClickListener(this)
        video_player_iv_next.setOnClickListener(this)
        //全屏非全屏点击监听
        video_player_iv_fullscreen.setOnClickListener(this)

    }

    inner class MySimpleOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        //单击
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            hideOrShowControl()
            return super.onSingleTapConfirmed(e)
        }

        //双击
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            switchFullScreen()
            return super.onDoubleTap(e)
        }

        /**
         * 双击过程
         * @param e
         * @return
         */
        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return super.onDoubleTapEvent(e)
        }

        override fun onLongPress(e: MotionEvent?) {
            //切换播放和暂停
            switchPlayAndPause()
        }

    }

    /**
     * 切换播放和暂停
     */
    private fun switchPlayAndPause() {
        //判断当前状态
        if (video_player_videoview.isPlaying) {
            //播放 -- 暂停
            video_player_videoview.pause()
            //移除 更新当前进度 msg
            handle.removeMessages(msg_update_play_progress)
        } else {
            //暂停 -- 继续播放
            video_player_videoview.start()//播放/继续播放
            //重新发送
            //            handler.sendEmptyMessageDelayed(msg_update_play_progress,1000);
            updatePlayProgress()
        }
        switchPlayAndPausePic()
    }

    /**
     * 显示和隐藏控制条
     */
    private fun hideOrShowControl() {
//        if (isShowing) {
//            //显示 -- 隐藏
//            hideControl()
//        } else {
//            //隐藏 -- 显示
//            showControl()
//        }
        if (!isShowing) {
            //隐藏 -- 显示
            showControl()
        } else {
            //显示 -- 隐藏
            hideControl()
        }

    }

    private fun showControl() {
        ViewPropertyAnimator.animate(video_player_ll_top).translationYBy(topHeight.toFloat())
        ViewPropertyAnimator.animate(video_player_ll_bottom).translationYBy((-bottomHeight).toFloat())
        isShowing = true
    }

    private fun switchFullScreen() {
        video_player_videoview.switchFullScreen()
        switchFullScreenPic()
    }

    private fun switchFullScreenPic() {
        if (video_player_videoview.isFullScreen) {
            //如果是全屏 -- 非全屏图片
            video_player_iv_fullscreen.setImageResource(R.drawable.video_defaultscreen_selector)
        } else {
            video_player_iv_fullscreen.setImageResource(R.drawable.video_fullscreen_selector)
        }
    }

    private fun registerBatteryChangedBroadcastReceiver() {
        val intent = Intent.ACTION_BATTERY_CHANGED //包含充电状态,电量等级level 0-100,电池信息
        myBatteryChangedBroadcastReceiver = MyBatteryChangedBroadcastReceiver()
        val intentFilter = IntentFilter(intent)
        registerReceiver(myBatteryChangedBroadcastReceiver, intentFilter)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video_player_iv_pause -> {
                switchPlayAndPause()
            }
            R.id.video_player_iv_mute -> {
                switchMute()
            }
            R.id.video_player_iv_pre -> {
                //上一个视频
                playPre()
            }
            R.id.video_player_iv_next -> {
                //下一个视频
                playNext()
            }
            R.id.video_player_iv_fullscreen -> {
                //大小屏切换
                switchFullScreen()
            }
        }
    }

    private fun playNext() {
        if (position < videoBeans?.size!! - 1) {
            //不是最后一个视频
            position++
        } else {
            position = 0
        }
        playItem()
    }

    private fun playPre() {
        if (position > 0) {
            position--
        } else {
            position = videoBeans?.size!! - 1
        }
        playItem()
    }

    /**
     * 切换静音
     */
    var lastVolume: Int = 0//上一次的音量

    private fun switchMute() {
        //根据当前的音量 -- 切换
        if (getStreamCurrentVolume() != 0) {
            lastVolume = getStreamCurrentVolume()
            //切换到静音
            setStreamCurrentVolume(0)
            //进度条音量也为0
            video_player_sk_volume.setProgress(0)
        } else {
            //切换到正常音量
            setStreamCurrentVolume(lastVolume)
            video_player_sk_volume.setProgress(lastVolume)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video_play
    }

    inner class MyBatteryChangedBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra("level", 0)
            if (level!! < 10) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_0)
            } else if (level < 20) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_10)
            } else if (level < 40) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_20)
            } else if (level < 60) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_40)
            } else if (level < 80) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_60)
            } else if (level < 100) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_80)
            } else {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_100)
            }
        }

    }

    /**
     * 1. 手势调整 系统音量

    结论: 按照百分百来调整系统音量
    最终音量 = 按下时的当前音量 + 改变音量
    改变音量 = 手指在屏幕上划过距离百分百 * 最大音量
    手指在屏幕上划过距离百分百 = (moveY - downY) / screenHeight

    2. 手势调整 屏幕亮度
    1. 思路

    结论: 按照百分百来调整View的透明度
    最终透明度 = 按下时的当前透明度 + 改变透明度
    改变透明度 = 手指在屏幕上划过距离百分百 * 最大透明度 (0-1)
    = 手指在屏幕上划过距离百分百
    手指在屏幕上划过距离百分百 = (moveY - downY) / screenHeight

    2. 如何调整屏幕亮度
    1. 通过相关系统API
    2. 通过调整VIew的透明度
    1. 布局中 添加一个覆盖的 不透明的View

     * @param event
     * @return
     */
    var downY: Float = 0.0f
    var downX: Float = 0.0f
    var downCurrentAlpha: Float = 0.0f//按下时的透明度
    var downCurrentVolume: Int = 0//按下时的当前音量
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        detector?.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.y
                downX = event.x
                downCurrentVolume = getStreamCurrentVolume()
                downCurrentAlpha = ViewHelper.getAlpha(video_player_cover)
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = event.y
                //1. 手指在屏幕上划过距离百分百 = (moveY - downY) / screenHeight
                val disY = downY - moveY
                val disPrecent = disY / screenHalfHeight
                //如果在屏幕左侧按下 -- 音量
                if (downX < screenHalfWidth) {
                    //2.改变音量 = 手指在屏幕上划过距离百分百 * 最大音量
                    var disVolume = disPrecent * (getStreamMaxVolume()?.toFloat()!!)
                    //3.最终音量 = 按下时的当前音量 + 改变音量
                    var endVolume = downCurrentVolume + disVolume
                    //4. 设置
                    setStreamCurrentVolume(endVolume.toInt())
                    video_player_sk_volume.setProgress(endVolume.toInt())
                } else {
                    //改变透明度
                    //2.改变透明度 = 手指在屏幕上划过距离百分百 * 最大透明度 (0-1)
                    var disAlpha = disPrecent
                    //3.最终透明度 = 按下时的当前透明度 + 改变透明度
                    var endAlpha = downCurrentAlpha + disAlpha
                    //4.设置
                    if (endAlpha >= 0 && endAlpha <= 1) {
                        ViewHelper.setAlpha(video_player_cover, endAlpha)
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myBatteryChangedBroadcastReceiver)
        //移除所有消息
        handle.removeCallbacksAndMessages(null)//null移除所有回调和消息
    }
}
