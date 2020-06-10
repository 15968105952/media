package com.example.myapplication.entity

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore

data class AudioBean(var data: String?, var size: Long, var display_name: String?, var artist: String?):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(data)
        parcel.writeLong(size)
        parcel.writeString(display_name)
        parcel.writeString(artist)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioBean> {
        override fun createFromParcel(parcel: Parcel): AudioBean {
            return AudioBean(parcel)
        }

        override fun newArray(size: Int): Array<AudioBean?> {
            return arrayOfNulls(size)
        }

        /**
         * 根据特定位置上的cursor获取bean
         */
        fun getAudioBean(cursor: Cursor?) :AudioBean{
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            val audioBean=AudioBean("",0,"","")
            //解析cursor并且设置到bean对象中
            cursor?.let {
                audioBean.data=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                audioBean.size=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                audioBean.display_name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                audioBean.display_name=audioBean.display_name?.substring(0, audioBean.display_name!!.lastIndexOf("."))
                audioBean.artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

            }
            return audioBean
        }

        fun getAudioBeans(cursor: Cursor):ArrayList<AudioBean> {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             //创建集合
            val list = arrayListOf<AudioBean>()
            //cursor是否为空
            cursor.let {
                //将cursor游标移动到-1
                it.moveToPosition(-1)
                //解析cursor添加到集合中
                while (cursor.moveToNext()){
                    val audioBean = getAudioBean(it)
                    list.add(audioBean)
                }
            }
            return list
        }
    }
}