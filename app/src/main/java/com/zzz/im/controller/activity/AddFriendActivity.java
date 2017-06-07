package com.zzz.im.controller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzz.im.R;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;


/**
 * 添加联系人界面
 */
public class AddFriendActivity extends AppCompatActivity {


    private ImageView top_add;
    private EditText ed_add_friend;
    private RelativeLayout rl_add;
    private TextView tv_add_name;
    private Button add_button;

    //模拟自己服务器有用户
    private HskUser hskUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initView();

        initListner();

    }

    private void initListner() {
        //查找按钮
        top_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find();
            }
        });

        //添加按钮
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    //查找功能
    private void find() {
        //获取输入的昵称
        final String name = ed_add_friend.getText().toString();
        //
        if ("".equals(name)) {
            Toast.makeText(AddFriendActivity.this, "请输入帐号", Toast.LENGTH_SHORT).show();
            return;
        }
        //去服务器查询是否存在这个用户
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //模拟  去自己的服务器查询
                hskUser = new HskUser(name);
                //更新UI显示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_add.setVisibility(View.VISIBLE);
                        tv_add_name.setText(name);
                    }
                });
            }
        });
    }

    //添加好友功能
    private void add() {
        if (Model.getInstance().getDbManager().getContactTableDao().getContactByHx(hskUser.getUsername()) != null) {
            Toast.makeText(AddFriendActivity.this, "该用户已经是你的好友了", Toast.LENGTH_SHORT).show();
            return;
        }
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(hskUser.getUsername(), "添加好友");
                    //更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFriendActivity.this, "好友请求发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFriendActivity.this, "好友请求发送失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        top_add = (ImageView) findViewById(R.id.top_add);
        ed_add_friend = (EditText) findViewById(R.id.ed_add_friend);
        rl_add = (RelativeLayout) findViewById(R.id.rl_add);
        tv_add_name = (TextView) findViewById(R.id.tv_add_name);
        add_button = (Button) findViewById(R.id.add_button);

    }

    public void back(View view) {
        finish();
    }
}
