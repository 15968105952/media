package com.example.myapplication.entity

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore

class VideoBean(var title: String?, var size: Long?, var duration: Long?, var data: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeValue(size)
        parcel.writeValue(duration)
        parcel.writeString(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoBean> {
        override fun createFromParcel(parcel: Parcel): VideoBean {
            return VideoBean(parcel)
        }

        override fun newArray(size: Int): Array<VideoBean?> {
            return arrayOfNulls(size)
        }

        fun getVideoBean(cursor: Cursor?): VideoBean {
            val videoBean = VideoBean("", 0, 0, "")
            cursor.let {
                videoBean.title =
                    cursor?.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                videoBean.size = cursor?.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                videoBean.duration =
                    cursor?.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                videoBean.data =
                    cursor?.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
            }
            return videoBean
        }

        fun getVideoBeans(cursor: Cursor): ArrayList<VideoBean> {
            val arrayListOf = arrayListOf<VideoBean>()
            cursor.let {
                //将cursor游标移动到-1
                it.moveToPosition(-1)
                while (cursor.moveToNext()) {
                    val videoBean = getVideoBean(it)
                    arrayListOf.add(videoBean)
                }
            }

            return arrayListOf

        }
    }
}