package com.example.myapplication.activity


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.R
import com.example.myapplication.utils.FragmentUtil
import com.example.myapplication.utils.ToolBarManager
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.find

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class Main2Activity : BaseActivity(),ToolBarManager {
    //惰性加载
    override val toolbar by lazy {
     find<Toolbar>(R.id.toolbar)
    }
    override fun initData() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        initMainToolBar()
    }

    override fun initListener() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        bottomBar.setOnTabSelectListener {
            myToast("这是首页")
            var transaction= supportFragmentManager.beginTransaction();
            transaction.replace(R.id.container, FragmentUtil.fragmentUtil.getFragment(it)!!,it.toString())
            transaction.commit()
        }
    }

    override fun getLayoutId(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
       return R.layout.activity_main2
    }
}
