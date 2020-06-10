package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.myapplication.R;
import com.example.myapplication.entity.ImpressTagEntity;
import com.example.myapplication.view.TagAdapter;
import com.example.myapplication.view.TaoFlowLayout;

import java.util.List;


/**
 * textView标签适配器
 *
 * @Description:
 * @Author: tao
 * @CreateDate: 2018/11/12 18:11
 */
public class TextTagAdapter extends TagAdapter<ImpressTagEntity> {

    private Context context;
    private GradientDrawable gradientDrawable;

    public TextTagAdapter(Context context, List datas) {
        super(datas);
        this.context = context;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(TaoFlowLayout parent, int position, ImpressTagEntity dataBean) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.tag_text, null);
        TextView tag_textView = view.findViewById(R.id.tag_textView);
        tag_textView.setText(dataBean.getEnName());
//        gradientDrawable = new GradientDrawable();
//        gradientDrawable.setCornerRadius(36);
//        gradientDrawable.setStroke(2, Color.parseColor(dataBean.getColor()));
        if(dataBean.isSelector()){
            //展示选中状态
//            gradientDrawable.setColor(Color.parseColor(dataBean.getColor()));
//            tag_textView.setTextColor(Color.parseColor("#FFFFFF"));
            tag_textView.setBackgroundResource(R.drawable.shape_recharge_background);
            tag_textView.setTextColor(context.getResources().getColor(R.color.color_ffffffff));
        }else{
            //展示 未选中状态
//            gradientDrawable.setCornerRadius(36);
//            gradientDrawable.setColor(Color.parseColor("#FFFFFF"));
//            gradientDrawable.setStroke(2, Color.parseColor(dataBean.getColor()));
//            tag_textView.setTextColor(Color.parseColor(dataBean.getColor()));
            tag_textView.setBackgroundResource(R.drawable.logout_background);
            tag_textView.setTextColor(context.getResources().getColor(R.color.color_993c2e4c));
        }
//        tag_textView.setBackground(gradientDrawable);

        return view;
    }

}
