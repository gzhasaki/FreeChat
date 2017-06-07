package com.zzz.im.controller.activity;

/**
 * Created by zzz on 2017/2/13.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zzz.im.R;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.ClearWriteET;
import com.zzz.im.model.Model;
import com.zzz.im.utils.MyDialog;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by Administrator on 2016/12/18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImg_Background;
    //登陆按钮
    private Button mlogin;
    //注册按钮
    private TextView mRegister;
    //用户输入的帐号密码
    private String username, password;

    private HskUser hskUser;
    private LocalBroadcastManager mLBM;
    //
    private ClearWriteET lg_username, lg_pwd;
    private MyDialog myDialog;
    private BroadcastReceiver userInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 进行正常登陆跳转
            Log.e("LoginActivity", "Onreceiver"+ hskUser);
            if (hskUser != null) {
                toLogin();
            }
        }
    };

    private void toLogin() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(username, password, new EMCallBack() {     //从服务器获取用户信息存入到本地
                    //登陆成功
                    @Override
                    public void onSuccess() {
                        //对模型层数据处理
                        Log.e("登陆时，正在往数据库存入当前userinfo", "onSuccess: " + hskUser);
                        Model.getInstance().loginSuccess(hskUser);
                        //保存用户帐号信息到数据库
                        Model.getInstance().getUserDao().addAccount(hskUser);
                        //提示登陆成功并跳转页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "登陆失败" + s, Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        init();

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(LoginActivity.this);
        mLBM.registerReceiver(userInfoReceiver, new IntentFilter("hskUser"));

        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_bg_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }

    /**
     * id绑定
     */
    private void init() {
        mlogin = (Button) findViewById(R.id.login_button);
        mRegister = (TextView) findViewById(R.id.login_register);

        lg_username = (ClearWriteET) findViewById(R.id.login_username);
        lg_pwd = (ClearWriteET) findViewById(R.id.login_password);
        lg_username.setText(getIntent().getStringExtra("username"));
        lg_pwd.setText(getIntent().getStringExtra("password"));

        mlogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                username = lg_username.getText().toString().trim();
                password = lg_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                getUserFromBmob(username);

                myDialog = new MyDialog(LoginActivity.this);
                break;
            case R.id.login_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
        }
    }

    private void getUserFromBmob(final String username) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                BmobQuery<HskUser> query = new BmobQuery<HskUser>();
                query.addWhereEqualTo("username", username);
                query.findObjects(new FindListener<HskUser>() {
                    @Override
                    public void done(List<HskUser> hskUsers, final BmobException e) {
                        if (hskUsers != null && hskUsers.size() > 0) {
                            hskUser = hskUsers.get(0);
                            hskUser.setBmobId(hskUsers.get(0).getObjectId());
                            mLBM.sendBroadcast(new Intent("hskUser"));
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "登陆失败"+e, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(userInfoReceiver);
    }
}
