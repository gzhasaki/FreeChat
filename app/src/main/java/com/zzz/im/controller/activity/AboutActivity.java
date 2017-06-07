package com.zzz.im.controller.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzz.im.R;
import com.zzz.im.utils.AppUtils;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_clean;
    private TextView cache_size;

    private RelativeLayout rl_feedback;
    private RelativeLayout rl_contact_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        init();
    }

    private void init() {

        rl_clean = (RelativeLayout) findViewById(R.id.rl_clean);
        cache_size = (TextView) findViewById(R.id.cache_size_tv);
        try {
            cache_size.setText(AppUtils.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
        rl_contact_service = (RelativeLayout) findViewById(R.id.rl_contact_service);

        rl_clean.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_contact_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_clean:
                try {
                    AppUtils.clearAllCache(this);
                    Toast.makeText(this, "成功清空缓存", Toast.LENGTH_SHORT).show();
                    cache_size.setText(AppUtils.getTotalCacheSize(this));
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "清除缓存失败" + e, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rl_feedback:
                startActivity(new Intent(this, FeedBackActivity.class));

                break;

            case R.id.rl_contact_service:
                String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=774145276&version=1";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, "未安装QQ或者QQ版本不支持", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void back(View view) {
        finish();
    }
}
