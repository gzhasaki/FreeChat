package com.zzz.im.controller.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.zzz.im.R;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.AppUtils;
import com.zzz.im.utils.Constant;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    //上级页面传送过来的自己的id
    private String mHxid;
    //根据该id从本地数据库获取的user
    private HskUser hskUser;
    //用户id显示
    private TextView profileId;
    //昵称显示
    private TextView profileNick;
    //头像显示
    private CircleImageView profileAvatar;
    //头像url解析的bitmap
    private Bitmap bitmap;
    //性别显示
    private TextView profileGender;
    //签名显示
    private TextView profileSign;

    //发送信息按钮
    private Button sendMsg;
    //删除好友按钮
    private Button profile_del;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver RefreshUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        getUserFromBmob(mHxid);
        init();
        initData();
        //注册广播
        mLBM = LocalBroadcastManager.getInstance(UserProfileActivity.this);
        mLBM.registerReceiver(RefreshUser, new IntentFilter(Constant.Contact_Info_Refresh));

    }

    //控件绑定
    private void init() {
        profileId = (TextView) findViewById(R.id.profile_id);
        profileNick = (TextView) findViewById(R.id.profile_nick);
        profileAvatar = (CircleImageView) findViewById(R.id.profile_avatar);
        profileGender = (TextView) findViewById(R.id.profile_gender);
        profileSign = (TextView) findViewById(R.id.profile_signature);

        sendMsg = (Button) findViewById(R.id.profile_sendMsg);
        profile_del = (Button) findViewById(R.id.profile_del);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EMClient.getInstance().getCurrentUser().equals(mHxid)) {
                    Toast.makeText(UserProfileActivity.this, "不能与自己聊天！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, mHxid);
                startActivity(intent);
                finish();
            }
        });

        if (!EMClient.getInstance().getCurrentUser().equals(mHxid)) {
            profile_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(UserProfileActivity.this).setMessage("确定要删除好友：" + mHxid + "吗？").setCancelable(false)
                            .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteContact();
                                    finish();
                                }
                            }).setNegativeButton(R.string.dl_cancel, null).create().show();
                }
            });

        }
    }

    private void initData() {
        //如果点击的是自己的头像，则设置自己的信息
        if (mHxid.equals(EMClient.getInstance().getCurrentUser())) {
            //从本地数据库将自己的信息取出来
            hskUser = Model.getInstance().getUserDao().getAccountByHxid(mHxid);
        } else {
            //设置好友的信息 ,从本地数据库将好友的信息取出来
            hskUser = Model.getInstance().getDbManager().getContactTableDao().getContactByHx(mHxid);
        }
        profileId.setText(mHxid);
        profileNick.setText(hskUser.getNick());

        if (hskUser.getGender() == null) profileGender.setText("未填写");
        else profileGender.setText(hskUser.getGender());

        if (hskUser.getSignature() == null) profileSign.setText("");
        else profileSign.setText(hskUser.getSignature());

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                bitmap = AppUtils.getHttpBitmap(hskUser.getPhoto());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileAvatar.setImageBitmap(bitmap);
                    }
                });
            }
        });
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBigAvatar();

            }
        });
    }

    //点击头像显示头像的大图
    private void showBigAvatar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.bigavatar_layout, null);
        ImageView bigAvater = (ImageView) view.findViewById(R.id.big_avatar);
        bigAvater.setImageBitmap(bitmap);
        dialog.setView(view);
        dialog.show();
    }

    private void getUserFromBmob(String username) {
        if (getApplicationContext() != null) {
            BmobQuery<HskUser> query = new BmobQuery<HskUser>();
            query.addWhereEqualTo("username", username);
            query.findObjects(new FindListener<HskUser>() {
                @Override
                public void done(List<HskUser> hskUsers, BmobException e) {
                    if (hskUsers != null && hskUsers.size() > 0) {
                        HskUser getHskUser = hskUsers.get(0);//这里的list就是查询出list
                        getHskUser.setBmobId(hskUsers.get(0).getObjectId());
                        //保存用户帐号信息到数据库
                        if (!getHskUser.getUsername().equals(EMClient.getInstance().getCurrentUser())) {
                            Model.getInstance().getDbManager().getContactTableDao().saveContact(getHskUser, true);
                            mLBM.sendBroadcast(new Intent(Constant.Contact_Info_Refresh));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(RefreshUser);
    }

    public void back(View view) {
        finish();
    }


    //删除联系人
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    //从环信服务器删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    //更新本地数据库
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    if (UserProfileActivity.this == null) return;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, "删除成功" + mHxid, Toast.LENGTH_SHORT).show();
                            mLBM.sendBroadcast(new Intent("ContactUserRefresh"));
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (UserProfileActivity.this == null) {
                        return;
                    }
                    //刷新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, "删除失败" + mHxid, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }


}
