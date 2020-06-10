package com.example.myapplication.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.View

import com.example.hiapad.myapplication.IMyAidlInterface
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity


class AidlActivity : BaseActivity() {
    //aidl测试
    private var mybinder: IMyAidlInterface? = null
    internal var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mybinder = IMyAidlInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl)
        //aidl测试
        val intent = Intent()
        intent.action = "com.example.hiapad.myapplication.MyService"
        intent.setPackage("com.example.hiapad.myapplication")
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun setAlarmOnce(view: View) {
        try {
            mybinder!!.pay()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }


}
