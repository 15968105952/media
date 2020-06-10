package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
    private var list: List<Int>,
    private var context: Context
) : RecyclerView.Adapter<ListAdapter.RecyclerViewHolder>() {
    //定义一个函数式接口
    var listener: ((itemBean: Person) -> Unit?)? = null

    //给函数式接口传值，也就是传递一个函数，至于函数实现什么功能，用户自己定义
    fun setMyListener(listener: (itemBean: Person) -> Unit) {
        this.listener = listener
    }

    //定义一个函数式接口
    interface OnItemListener {
        fun mAction(get: Int)
    }
    //接口方法和接口定义
    private var mListener: OnItemListener? = null
    fun setOnItemListener(mListener: OnItemListener) {
        this.mListener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        //加载布局文件
        return RecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
            holder.textView.setText(list.get(position).toString())
            holder.textView.setOnClickListener {
                //                Toast.makeText(context,list.get(position).toString(),Toast.LENGTH_SHORT).show()
//                var intent=Intent()
//                 intent.setClass(context,SecondActivity::class.java)
//                context.startActivity(intent)
//                listener?.let {
//                }
                mListener?.mAction(position)
            }
        }
    }

    inner class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.tv_test)
    }
}