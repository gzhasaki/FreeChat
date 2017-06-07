package com.zzz.im.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;
import com.zzz.im.R;

/**
 * Created by zzz on 2017/3/18.
 */
public class MyDialog extends Dialog {
    private ImageView img;
    private TextView txt;

    public MyDialog(Context context) {
        super(context ,R.style.myDialog);
        //加载布局文件
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.progress_dialog, null);
        img=(ImageView) view.findViewById(R.id.progress_dialog_img);
        txt=(TextView) view.findViewById(R.id.progress_dialog_txt);
        //给图片添加动态效果
        Animation anim= AnimationUtils.loadAnimation(context, R.anim.loading_dialog_progressbar);
        img.setAnimation(anim);
        txt.setText("Loading...");
        setCanceledOnTouchOutside(false);//点击其他位置不消失
        //dialog添加视图
        setContentView(view);
        show();  //显示
//           dismiss(); //取消显示
    }

}