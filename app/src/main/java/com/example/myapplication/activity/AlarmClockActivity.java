package com.example.myapplication.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.nls.NLS;
import com.example.myapplication.nls.NLSProtectService;
import com.example.myapplication.scheduler.JobSchedulerManager;
import com.example.myapplication.screen.ScreenManager;
import com.example.myapplication.screen.ScreenReceiverUtil;
import com.example.myapplication.service.BgPlayService;
import com.example.myapplication.service.ForegroundDaemonService;
import com.example.myapplication.service.YourService;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

public class AlarmClockActivity extends BaseActivity {

    private static MyHandler myHandler;
    private YourService mYourService;
    private Intent mServiceIntent;
    private PendingIntent pendingIntent;
    public static class MyHandler extends Handler {
        private WeakReference<AlarmClockActivity> activity;

        public MyHandler(AlarmClockActivity activity) {
            this.activity = new WeakReference<AlarmClockActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("after", "循环中");
                    myHandler.sendEmptyMessageDelayed(1, 500);

                    break;
                case 2:
                    Log.i("after", "停止循环");
                    myHandler.removeMessages(1);//移除循环信息
                    break;
                default:
                    break;
            }
        }
    }
    private AlarmManager am;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        startDaemon();
        mYourService = new YourService();

        mServiceIntent = new Intent(this, mYourService.getClass());

        if (!isMyServiceRunning(mYourService.getClass())) {

            startService(mServiceIntent);

        }
        myHandler = new MyHandler(this);
        myHandler.sendEmptyMessage(1);
        //获取闹钟管理器
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarmOnce(View view){
        //获取当前系统时间
        Calendar calendar= Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);

        //弹出时间对话框
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c= Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                c.set(Calendar.MINUTE,minute);
                //设置闹钟
                PendingIntent pendingIntent= PendingIntent.getBroadcast(AlarmClockActivity.this,0x101,new Intent("com.example.android27_zhangkai_alarm_notification.RING"),0);
//                am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
                am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 5*1000, pendingIntent);
            }
        },hour,minute,true);
        timePickerDialog.show();
    }

    public void setAlarmRepeat(View view){
        //获取当前系统时间
        Calendar calendar= Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);

        //1.弹出时间对话框
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c= Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                c.set(Calendar.MINUTE,minute);
                //2.获取到时间      hourOfDay       minute
                //3.设置闹钟
                pendingIntent = PendingIntent.getBroadcast(AlarmClockActivity.this, 0x102, new Intent("com.zking.administrator.g160628_android29_alarm_notification.RING"), 0);
                am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),5000, pendingIntent);//设置五秒响一次
            }
        },hour,minute,true);
        timePickerDialog.show();
    }
    public void cancelAlarmRepeat(View view){
        am.cancel(pendingIntent);
    }

    private void startDaemon() {
        initForegroundDaemonService();
        initScreenBroadcastReceiver();
        initBgMusicService();
        initJobScheduler();
        initNLS();
    }

    //-------------前台----------------
    private Intent foregroundDaemonServiceIntent;

    private void initForegroundDaemonService() {
        foregroundDaemonServiceIntent = new Intent(this, ForegroundDaemonService.class);
        startService(foregroundDaemonServiceIntent);
    }

    //--------------1像素--------------
    private ScreenReceiverUtil screenReceiverUtil;// 动态注册锁屏等广播

    private ScreenManager screenManager;// 1像素Activity管理类

    private void initScreenBroadcastReceiver() {
        screenReceiverUtil = new ScreenReceiverUtil(this);
        screenReceiverUtil.setScreenReceiverListener(mScreenListener);
        screenManager = ScreenManager.getScreenManagerInstance(this);
    }

    private ScreenReceiverUtil.ScreenStateListener mScreenListener = new ScreenReceiverUtil.ScreenStateListener() {
        @Override
        public void onScreenOn() {
            // 亮屏，移除"1像素"
            screenManager.finishActivity();
        }

        @Override
        public void onScreenOff() {
            screenManager.startActivity();
        }

        @Override
        public void onUserPresent() {
            // 解锁，暂不用，保留
        }
    };

    //------------后台音乐播放
    private Intent bgService;

    private void initBgMusicService() {
        bgService = new Intent(this, BgPlayService.class);
        startService(bgService);
    }

    //------------JobService
    private JobSchedulerManager jobManager;

    private void initJobScheduler() {
        jobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        jobManager.startJobScheduler();
    }

    //------------NLS
    private Intent nlsIntent;
    private Intent nlsProtectIntent;

    private void initNLS() {
        if (NLS.isNotificationListenerEnabled(this)) {
            nlsIntent = new Intent(this, NLS.class);
            startService(nlsIntent);
            nlsProtectIntent = new Intent(this, NLSProtectService.class);
            startService(nlsProtectIntent);
        } else startActivity(NLS.go2NLSSettingIntent());//这里可以加一个startActivityForResult去做回调开启
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (foregroundDaemonServiceIntent != null) stopService(foregroundDaemonServiceIntent);
        if (screenReceiverUtil != null) screenReceiverUtil.stopScreenReceiverListener();
        if (bgService != null) stopService(bgService);
        if (jobManager != null) jobManager.stopJobScheduler();
        if (nlsIntent != null) stopService(nlsIntent);
        if (nlsProtectIntent != null) stopService(nlsProtectIntent);
        if(null!=myHandler)
            myHandler.removeCallbacksAndMessages(null);
        stopService(mServiceIntent);
    }

    public static boolean isAPPALive(Context mContext, String packageName) {
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for (ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList) {
            if (packageName.equals(appInfo.processName)) {
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {

                Log.i ("Service status", "Running");

                return true;

            }

        }

        Log.i ("Service status", "Not running");

        return false;

    }





}
