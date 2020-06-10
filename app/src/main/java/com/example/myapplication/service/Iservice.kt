package com.example.myapplication.service

import com.example.myapplication.entity.AudioBean


/**
 * ClassName:Iservice
 * Description:定义各种状态的接口
 */
interface Iservice {
    fun updatePlayState()
    fun isPlaying():Boolean?
    fun getDuration(): Int
    fun getProgress(): Int
    fun seekTo(p1: Int)
    fun updatePlayMode()
    fun  getPlayMode(): Int
    fun playPre()
    fun playNext()
    fun getPlayList(): List<AudioBean>?
    fun playPosition(p2: Int)
}