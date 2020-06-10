package com.example.myapplication.activity

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity(),ViewPropertyAnimatorListener {
    override fun onAnimationCancel(view: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAnimationEnd(view: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        startActivityAndFinish<Main2Activity>()
    }

    override fun onAnimationStart(view: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        ViewCompat.animate(imageView).scaleX(1.0f).scaleY(1.0f).setListener(this).setDuration(2000)
    }

    override fun initListener() {
        }

    override fun getLayoutId(): Int {

      return  R.layout.activity_splash
    }
}
