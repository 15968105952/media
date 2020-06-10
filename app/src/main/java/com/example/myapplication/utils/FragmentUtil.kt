package com.example.myapplication.utils

import com.example.myapplication.R
import com.example.myapplication.fragment.BaseFragment
import com.example.myapplication.fragment.VBangFragment

class FragmentUtil private constructor() {


    val homeFragment by lazy {
        VBangFragment()
    }

    val mvFragment by lazy {
        VBangFragment()
    }

    val vbangFragment by lazy {
        VBangFragment()
    }

    val yuedanFragment by lazy {
        VBangFragment()
    }

    fun getFragment(it: Int): BaseFragment?{
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (it) {
            R.id.tab_home -> return homeFragment
            R.id.tab_mv -> return mvFragment
            R.id.tab_vbang -> return vbangFragment
            R.id.tab_yuedan -> return yuedanFragment

        }
        return null
    }

    companion object {
        val fragmentUtil by lazy {
            FragmentUtil()
        }
    }

}