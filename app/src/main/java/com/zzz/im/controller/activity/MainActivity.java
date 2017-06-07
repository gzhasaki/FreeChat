package com.zzz.im.controller.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.chat.EMClient;
import com.zzz.im.R;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //如果当前activty已经退出 则不处理
            if(isFinishing()){
                return;
            }
            toActivity();
        }
    };

    //判断进入哪个页面
    private void toActivity() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                    //判断是否登陆过
                    if (EMClient.getInstance().isLoggedInBefore()) {//登陆过
                        HskUser account = Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
                        if(account == null){
                            Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);

                        }else {

                            //登陆成功后的方法
                            Model.getInstance().loginSuccess(account);
                            //跳转到主页
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    } else {//没登陆过  跳转到登陆页面
                        Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
            finish();
                }
        });
        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "f16aeff174a445da95c622dcbf0d9540");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        handler.sendMessageDelayed(Message.obtain(),2000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
