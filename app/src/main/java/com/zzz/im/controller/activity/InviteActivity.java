package com.zzz.im.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zzz.im.R;
import com.zzz.im.controller.adapter.MyAdapter;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.Invitation;
import com.zzz.im.utils.Constant;

import java.util.List;

public class InviteActivity extends Activity {

    private LocalBroadcastManager mLBM;
    private MyAdapter myAdapter;
    private ListView lv_invite;

    private MyAdapter.InviteListener mInviteListener = new MyAdapter.InviteListener() {
        @Override
        public void onAccept(final Invitation invitation) {

            //去环信服务器，点击了接收按钮
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invitation.getHskUser().getNick());
                        //更新数据库
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(Invitation.InvatationStatus.INVITE_ACCEPT, invitation.getHskUser().getNick());

                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了好友邀请", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        //页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受失败", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            });
        }

        @Override
        public void onReject(final Invitation invitation) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invitation.getHskUser().getNick());

                        //数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invitation.getHskUser().getNick());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝成功", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝失败", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    }
                }
            });
        }
    };
    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        init();
        initData();

    }

    private void initData() {
        myAdapter = new MyAdapter(this, mInviteListener);

        lv_invite.setAdapter(myAdapter);

        refresh();

        //注册邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
    }

    private void refresh() {
        List<Invitation> invitations = Model.getInstance().getDbManager().getInviteTableDao().getInvitation();
        //刷新适配器
        myAdapter.refresh(invitations);

    }

    private void init() {
        lv_invite = (ListView) findViewById(R.id.lv_invite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
    }

    public void back(View view) {
        finish();
    }
}
