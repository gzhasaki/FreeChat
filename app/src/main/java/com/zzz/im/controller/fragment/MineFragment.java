package com.zzz.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.zzz.im.R;
import com.zzz.im.controller.activity.AboutActivity;
import com.zzz.im.controller.activity.UpdateInfoActivity;
import com.zzz.im.controller.activity.LoginActivity;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.AppUtils;
import com.zzz.im.utils.Constant;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by Administrator on 2016/12/10.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Button lgout_button;
    private TextView nick;
    private ImageView myAvatar;
    private String myId;
    private HskUser hskUser;
    //解析头像url得到的bitmap
    private Bitmap bitmap;

    private RelativeLayout ll_user_profile;


    private EaseSwitchButton notify;
    private RelativeLayout ll_notify;
    private EaseSwitchButton sound;
    private RelativeLayout ll_sound;
    private EaseSwitchButton vibrate;
    private RelativeLayout ll_vibrate;
    private EaseSwitchButton speaker;
    private RelativeLayout ll_speaker;

    private RelativeLayout ll_about;

    private LocalBroadcastManager mLBM;

    private BroadcastReceiver UserInfoChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    final HskUser userinfo = Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
                    final String userNick = userinfo.getNick();
                    //环信服务器更新
                    EMClient.getInstance().pushManager().updatePushNickname(userNick);
                    bitmap = AppUtils.getHttpBitmap(userinfo.getPhoto());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nick.setText(userNick);
                            myAvatar.setImageBitmap(bitmap);
                            Log.e("MineFragment ", "onReceive:" + userinfo.getPhoto());
                        }
                    });
                }
            });

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.mine_fragment, null);
        init(mView);
        setInfo();
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(UserInfoChanged, new IntentFilter(Constant.My_Info_Changed));
        return mView;
    }

    //设置user信息
    private void setInfo() {
        //昵称显示，从数据库中获取
        nick.setText(hskUser.getNick());
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                bitmap = AppUtils.getHttpBitmap(hskUser.getPhoto());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myAvatar.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }


    private void init(View mView) {

        lgout_button = (Button) mView.findViewById(R.id.lgout_button);
        nick = (TextView) mView.findViewById(R.id.myNick);
        myAvatar = (ImageView) mView.findViewById(R.id.myAvatar);
        myId = EMClient.getInstance().getCurrentUser();
        hskUser = Model.getInstance().getUserDao().getAccountByHxid(myId);

        ll_user_profile = (RelativeLayout) mView.findViewById(R.id.start_user_profile);
        ll_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                intent.putExtra("nick", nick.getText());

                startActivity(intent);
            }
        });


        notify = (EaseSwitchButton) mView.findViewById(R.id.mine_notify);
        ll_notify = (RelativeLayout) mView.findViewById(R.id.ll_notify);
        ll_notify.setOnClickListener(this);

        sound = (EaseSwitchButton) mView.findViewById(R.id.mine_sound);
        ll_sound = (RelativeLayout) mView.findViewById(R.id.ll_sound);
        ll_sound.setOnClickListener(this);

        vibrate = (EaseSwitchButton) mView.findViewById(R.id.mine_vibrate);
        ll_vibrate = (RelativeLayout) mView.findViewById(R.id.ll_vibrate);
        ll_vibrate.setOnClickListener(this);

        speaker = (EaseSwitchButton) mView.findViewById(R.id.mine_speaker);
        ll_speaker = (RelativeLayout) mView.findViewById(R.id.ll_speaker);
        ll_speaker.setOnClickListener(this);

        ll_about = (RelativeLayout) mView.findViewById(R.id.ll_about);
        ll_about.setOnClickListener(this);

        if (Model.getInstance().getSettingMsgNotification()) {
            notify.openSwitch();
        } else {
            notify.closeSwitch();
        }

        // sound notification is switched on or not?
        if (Model.getInstance().getSettingMsgSound()) {
            sound.openSwitch();
        } else {
            sound.closeSwitch();
        }

        // vibrate notification is switched on or not?
        if (Model.getInstance().getSettingMsgVibrate()) {
            vibrate.openSwitch();
        } else {
            vibrate.closeSwitch();
        }
        // vibrate notification is switched on or not?
        if (Model.getInstance().getSettingMsgSpeaker()) {
            speaker.openSwitch();
        } else {
            speaker.closeSwitch();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_notify:
                if (notify.isSwitchOpen()) {
                    notify.closeSwitch();
                    ll_sound.setVisibility(View.GONE);
                    ll_vibrate.setVisibility(View.GONE);
                    Model.getInstance().setSettingMsgNotification(false);
                } else {
                    notify.openSwitch();
                    ll_sound.setVisibility(View.VISIBLE);
                    ll_vibrate.setVisibility(View.VISIBLE);
                    Model.getInstance().setSettingMsgNotification(true);
                }
                break;
            case R.id.ll_sound:
                if (sound.isSwitchOpen()) {
                    sound.closeSwitch();
                    Model.getInstance().setSettingMsgSound(false);
                } else {
                    sound.openSwitch();
                    Model.getInstance().setSettingMsgSound(true);
                }
                break;
            case R.id.ll_vibrate:
                if (vibrate.isSwitchOpen()) {
                    vibrate.closeSwitch();
                    Model.getInstance().setSettingMsgVibrate(false);
                } else {
                    vibrate.openSwitch();
                    Model.getInstance().setSettingMsgVibrate(true);
                }
                break;
            case R.id.ll_speaker:
                if (speaker.isSwitchOpen()) {
                    speaker.closeSwitch();
                    Model.getInstance().setSettingMsgSpeaker(false);
                } else {
                    speaker.openSwitch();
                    Model.getInstance().setSettingMsgSpeaker(true);
                }
                break;
            case R.id.ll_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {

        lgout_button.setText("注销（" + EMClient.getInstance().getCurrentUser() + "）");
        lgout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //退出登陆
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //关闭DBHelper
                                Model.getInstance().getDbManager().close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                                        //回到登陆页面
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.putExtra("username", hskUser.getUsername());
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, final String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出失败" + s, Toast.LENGTH_SHORT).show();

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
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(UserInfoChanged);
    }


}
