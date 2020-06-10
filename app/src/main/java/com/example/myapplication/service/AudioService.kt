package com.example.myapplication.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.activity.AudioPlayerActivity
import com.example.myapplication.activity.Main2Activity
import com.example.myapplication.entity.AudioBean
import com.ycbjie.notificationlib.NotificationUtils
import de.greenrobot.event.EventBus
import kotlin.random.Random

class AudioService : Service() {
    var mediaPlayer: MediaPlayer? = null
    var list: ArrayList<AudioBean>? = null
    var manager: NotificationManager? = null
    var notification: Notification? = null
    var position: Int = -2//正在播放的position
    var notificationUtils: NotificationUtils? = null

    val FROM_PRE = 1
    val FROM_NEXT = 2
    val FROM_STATE = 3
    val FROM_CONTENT = 4

    companion object {
        val MODE_ALL = 1
        val MODE_SINGLE = 2
        val MODE_RANDOM = 3
    }

    var mode = MODE_ALL //播放模式
    val sp by lazy {
        getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        //获取播放模式
        sp.getInt("mode", 1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //判断进入service的方法
        val from = intent?.getIntExtra("from", -1)
        when (from) {
            FROM_PRE -> {
                binder.playPre()
            }
            FROM_NEXT -> {
                binder.playNext()
            }
            FROM_CONTENT -> {
                binder.notifyUpdateUi()
            }
            FROM_STATE -> {
                binder.updatePlayState()
            }
            else -> {
                val pos = intent?.getIntExtra("position", -1) ?: -1 //想要播放的position
                if (pos != position) {
                    //不是同一个position
                    position = pos
                    //获取集合以及position
                    list = intent?.getParcelableArrayListExtra<AudioBean>("list")
                    //开始播放音乐
                    binder.playItem()
                } else {
                    //同一个position
                    //通知activity更新UI
                    EventBus.getDefault().post(list?.get(position))
                    //是同一个item
                    binder.continutePlay()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return binder
    }

    val binder by lazy {
        AudioBinder()
    }

    inner class AudioBinder : Binder(), Iservice, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
        override fun onPrepared(mp: MediaPlayer?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //播放音乐
            start()
            //通知界面ui更新
            notifyUpdateUi()
            //显示通知
            showNotification()
        }

        private fun showNotification() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val notificationChannel =
//                    NotificationChannel("2", "test", NotificationManager.IMPORTANCE_LOW)
//                manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                manager!!.createNotificationChannel(notificationChannel)
//                notification = getNotification()
//                manager?.notify(1, notification)
//            }
            val vibrate = longArrayOf(0)
            notificationUtils = NotificationUtils(this@AudioService)
            notificationUtils!!
                .setOngoing(true)
                .setContentIntent(getPendingIntent())
                .setTicker("正在播放歌曲${list?.get(position)?.display_name}")
                .setContent(getRemoteViews())
                .setSound(null)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setVibrate(null)
                .setVibrate(vibrate)
                .setDefaults(Notification.DEFAULT_ALL)
                .setFlags(Notification.FLAG_ONLY_ALERT_ONCE)//只震动一次
                .sendNotification(1, "这个是标题3", "这个是内容3", R.mipmap.ic_launcher)
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        private fun getNotification(): Notification? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val build = NotificationCompat.Builder(this@AudioService)
                .setTicker("正在播放歌曲${list?.get(position)?.display_name}")
                .setSmallIcon(R.mipmap.ic_launcher)
                //自定义通知view
                .setCustomContentView(getRemoteViews())
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)//设置不能滑动删除通知
                .setContentIntent(getPendingIntent())//通知栏主题通知事件
                .build()
            return build
        }

        private fun getPendingIntent(): PendingIntent? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val intentM = Intent(this@AudioService, Main2Activity::class.java)
            val intentA = Intent(this@AudioService, AudioPlayerActivity::class.java)
            intentA.putExtra("from", FROM_CONTENT)
            val arrayOf = arrayOf(intentM, intentA)
            val activities = PendingIntent.getActivities(
                this@AudioService,
                1,
                arrayOf,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            return activities
        }

        private fun getRemoteViews(): RemoteViews? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val remoteViews = RemoteViews(packageName, R.layout.notification)
            //修改标题和内容
            remoteViews.setTextViewText(R.id.title, list?.get(position)?.display_name)
            remoteViews.setTextViewText(R.id.artist, list?.get(position)?.artist)
//            if (mediaPlayer?.isPlaying()!!) {
//                remoteViews.setImageViewResource(R.id.state, R.mipmap.btn_audio_play_normal)
//            } else {
//                remoteViews.setImageViewResource(R.id.state, R.mipmap.btn_audio_pause_normal)
//            }
            //处理上一曲，下一曲的点击事件
            remoteViews.setOnClickPendingIntent(R.id.pre, getPrePendingIntent())
            remoteViews.setOnClickPendingIntent(R.id.state, getStatePendingIntent())
            remoteViews.setOnClickPendingIntent(R.id.next, getNextPendingIntent())
            return remoteViews
        }

        //播放下一首歌曲
        private fun getNextPendingIntent(): PendingIntent? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_NEXT)
            val service = PendingIntent.getService(
                this@AudioService,
                2,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            return service
        }

        /**
         * 播放暂停按钮点击事件
         */
        private fun getStatePendingIntent(): PendingIntent? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_STATE)
            val service = PendingIntent.getService(
                this@AudioService,
                3,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            return service
        }

        private fun getPrePendingIntent(): PendingIntent? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            var intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_PRE)
            val service = PendingIntent.getService(
                this@AudioService,
                4,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return service
        }

        /**
         * 歌曲播放完成之后回调
         */
        override fun onCompletion(mp: MediaPlayer?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //自动播放下一曲
            autoPlayNext()
        }

        private fun autoPlayNext() {
//           TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            when (mode) {
                //按顺序播放
                MODE_ALL -> {
                    list?.let {
                        //根据是否是最后一首判断，容易理解，代码麻烦
//                  if(position==it.size-1){
//                      position=0
//                  }else{
//                      position++
//                  }
                        //position加1取模就是下一首
                        position = (position + 1) % it.size
                    }
                }
                //随机播放
                MODE_RANDOM -> {
                    list?.let {
                        position = Random.nextInt(it.size - 1)
                    }
                }
            }
            playItem()
        }

        fun playItem() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //如果mediaplayer已经存在就先释放
            if (null != mediaPlayer) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.setOnPreparedListener(this)
                it.setOnCompletionListener(this)
                it.setDataSource(list?.get(position)?.data)
                it.prepareAsync()
            }
        }

        override fun updatePlayState() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //获取当前播放状态
            var isPlaying = isPlaying()
            //切换播放状态
            isPlaying?.let {
                if (isPlaying) {
                    //正在播放，暂停
                    pause()
                } else {
                    //已经暂停，进行播放
                    start()
                }
            }
        }

        private fun pause() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            mediaPlayer?.pause()
            EventBus.getDefault().post(list?.get(position))
            notification = notificationUtils?.getNotification(
                "这个是标题3",
                "这个是内容3", R.mipmap.ic_launcher
            )
            notification?.contentView?.setImageViewResource(
                R.id.state,
                R.mipmap.btn_audio_pause_normal
            )
            notificationUtils?.manager?.notify(1, notification)
//            //更新图标
//            notification?.contentView?.setImageViewResource(
//                R.id.state,
//                R.mipmap.btn_audio_pause_normal
//            )
//            //重新展示
//            manager?.notify(1, notification)
        }

        override fun isPlaying(): Boolean? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return mediaPlayer?.isPlaying
        }

        /**
         * 获取总进度
         */
        override fun getDuration(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return mediaPlayer?.duration ?: 0
        }

        /**
         * 获取当前播放进度
         */
        override fun getProgress(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return mediaPlayer?.currentPosition ?: 0
        }

        /**
         * 跳转到当前进度播放
         */
        override fun seekTo(progress: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            mediaPlayer?.seekTo(progress)
        }

        override fun updatePlayMode() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            when (mode) {
                MODE_ALL -> mode = MODE_SINGLE
                MODE_SINGLE -> mode = MODE_RANDOM
                MODE_RANDOM -> mode = MODE_ALL
            }
            //保存播放模式
            sp.edit().putInt("mode", mode).commit()
        }

        override fun getPlayMode(): Int {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return mode
        }

        //播放上一首歌曲
        override fun playPre() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            list?.let {
                when (mode) {
                    MODE_RANDOM -> list?.let { position = Random.nextInt(it.size - 1) }
                    else -> list?.let {
                        if (position == 0) {
                            position = it.size - 1
                        } else {
                            position--
                        }
                    }
                }
                playItem()
            }
        }

        //播放下一首歌曲
        override fun playNext() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            list?.let {
                when (mode) {
                    MODE_RANDOM -> list?.let { position = Random.nextInt(it.size - 1) }
                    else -> list?.let { position = position + 1 % it.size }
                }
                playItem()
            }
        }

        /*
        * 得到播放列表
        * */
        override fun getPlayList(): List<AudioBean>? {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return list
        }

        /**
         * 播放当前位置的歌曲
         */
        override fun playPosition(p2: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            position = p2
            playItem()
        }

        //开始播放音乐
        private fun start() {
            //开始播放音乐
            mediaPlayer?.start()
            //传递到activity更新UI
            EventBus.getDefault().post(list?.get(position))
            //更新图标
            notification = notificationUtils?.getNotification(
                "这个是标题3",
                "这个是内容3", R.mipmap.ic_launcher
            )
            notification?.contentView?.setImageViewResource(
                R.id.state,
                R.mipmap.btn_audio_play_normal
            )
            notificationUtils?.manager?.notify(1, notification)
//            notification?.contentView?.setImageViewResource(
//                R.id.state,
//                R.mipmap.btn_audio_play_normal
//            )
            //重新显示
//            manager?.notify(1, notification)
        }

        /**
         * 通知界面更新
         */
        fun notifyUpdateUi() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            EventBus.getDefault().post(list?.get(position))
        }

        /*
        * 继续播放
        * */
        fun continutePlay() {
            //如果时暂定状态，不会播放
            if (mediaPlayer?.isPlaying()!!) {
                mediaPlayer?.start()
            }
        }
    }

}
