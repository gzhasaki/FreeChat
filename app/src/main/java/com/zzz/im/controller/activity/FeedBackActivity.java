package com.zzz.im.controller.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.zzz.im.R;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.FeedBack;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.AppUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedBackActivity extends AppCompatActivity {

    private EditText title, content;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        init();
    }

    private void init() {
        title = (EditText) findViewById(R.id.fd_title_tv);
        content = (EditText) findViewById(R.id.fd_content_tv);
        submit = (Button) findViewById(R.id.fd_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFeedBack();
            }
        });
    }

    private void uploadFeedBack() {
        String t = title.getText().toString().trim();
        String c = content.getText().toString().trim();

        if (!AppUtils.checkNetworkAvailable(FeedBackActivity.this)) {
            Toast.makeText(FeedBackActivity.this, "网络出问题了...请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        if (t.length() == 0 || c.length() == 0) {
            Toast.makeText(FeedBackActivity.this, "输入有误，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        FeedBack feedback = new FeedBack();
        feedback.setId(EMClient.getInstance().getCurrentUser());
        feedback.setContent(c);
        feedback.setTitle(t);
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(FeedBackActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(FeedBackActivity.this, "反馈失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back(View view){
        finish();
    }
}
