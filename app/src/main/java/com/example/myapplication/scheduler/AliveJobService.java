package com.example.myapplication.scheduler;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.example.myapplication.MainActivity;
import com.example.myapplication.utils.Appalive;


/**
 * Project Name:learnDaemon
 * Package Name:com.lahm.learndaemon.scheduler
 * Created by lahm on 2018/3/4 下午9:16 .
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private final ThreadLocal<Handler> mHandler = new ThreadLocal<Handler>() {
        @Override
        protected Handler initialValue() {
            return new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    // 具体任务逻辑
                    if (Appalive.isAPPALive(getApplicationContext(), "com.lahm.learndaemon")) {
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    // 通知系统任务执行结束
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        jobFinished((JobParameters) msg.obj, false);
                    }
                    return true;
                }
            });
        }
    };

    @Override
    public boolean onStartJob(JobParameters params) {
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler.get(), MESSAGE_ID_TASK, params);
        mHandler.get().sendMessage(msg);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.get().removeMessages(MESSAGE_ID_TASK);
        return false;
    }
}