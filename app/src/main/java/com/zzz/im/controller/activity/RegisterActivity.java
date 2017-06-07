package com.zzz.im.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzz.im.R;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.ClearWriteET;
import com.zzz.im.model.Model;
import com.zzz.im.utils.MyDialog;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Administrator on 2016/12/18.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private HskUser u;
    private Button regButton;
    private ClearWriteET username, password;
    private String rg_username, rg_password;
    private ImageView mImg_Background;
    private TextView rg_login;


    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        init();
        mImg_Background = (ImageView) findViewById(R.id.rg_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.login_bg_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }

    public void init() {
        username = (ClearWriteET) findViewById(R.id.reg_username);
        regButton = (Button) findViewById(R.id.reg_button);
        password = (ClearWriteET) findViewById(R.id.reg_password);
        rg_login = (TextView) findViewById(R.id.reg_login);

        regButton.setOnClickListener(this);
        rg_login.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_button:
                //将对应的数据保存于本地数据库的tab_account表中，方便之后的页面调取数据显示相应视图。
                rg_username = username.getText().toString().trim();
                rg_password = password.getText().toString().trim();
                if (inputCheck()) {
                    final MyDialog myDialog = new MyDialog(this);
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(rg_username, rg_password);
                                //存入Bmob后端云
                                u = new HskUser();
                                u.setUsername(rg_username);
                                u.setPassword(rg_password);
                                u.setNick("请修改昵称");
                                u.setPhoto("https://imgsa.baidu.com/forum/w%3D580/sign=e896c74ea918972ba33a00c2d6cc7b9d/4caa67f33a87e9500daa083612385343faf2b452.jpg");
                                u.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, final BmobException e) {
                                        if (e == null) {
                                            myDialog.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("username", rg_username);
                                            intent.putExtra("password", rg_password);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                                //更新页面显示
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "注册失败" + e, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }
                    });
                }
                break;
            case R.id.reg_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("username", rg_username);
                intent.putExtra("password", rg_password);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    public boolean inputCheck() {
        if (rg_username.length() < 8) {
            Toast.makeText(RegisterActivity.this, "帐号不得少于8位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (rg_password.length() < 8) {
            Toast.makeText(RegisterActivity.this, "密码过于简单", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
