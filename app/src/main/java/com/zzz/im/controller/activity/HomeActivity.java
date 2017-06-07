package com.zzz.im.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hyphenate.chat.EMClient;
import com.zzz.im.R;
import com.zzz.im.controller.adapter.MyPopupWindow;
import com.zzz.im.controller.fragment.ChatFragment;
import com.zzz.im.controller.fragment.ContactFragment;
import com.zzz.im.controller.fragment.MineFragment;
import com.zzz.im.model.EventListener;
import com.zzz.im.model.Model;
import com.zzz.im.utils.AppUtils;
import com.zzz.im.utils.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/12/10.
 */

public class HomeActivity extends FragmentActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener{


    //标题栏文字控件
    private TextView mtile_tv;

    //底部三个文字控件
    private TextView mTvMessage;
    private TextView mTvContact;
    private TextView mTvMine;

    //底部的3个导航控件
    private RelativeLayout mtab_Message;
    private RelativeLayout mtab_Contact;
    private RelativeLayout mtab_Mine;

    //底部3个导航控件中的图片按钮
    private ImageView mImgMessage;
    private ImageView mImgContact;
    private ImageView mImgMine;

    //标题栏图片控件
    private ImageView mAdd;

    //用户头像
    private CircleImageView userAvatar;

    private LocalBroadcastManager mLBM;

    private static ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private List<Fragment> mFragment = new ArrayList<>();

    private BroadcastReceiver RefreshUserInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取头像
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = AppUtils.getHttpBitmap(Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser()).getPhoto());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userAvatar.setImageBitmap(bitmap);
                        }
                    });
                }
            });

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        new EventListener(this);//开启监听
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(RefreshUserInfo,new IntentFilter(Constant.My_Info_Changed));
        initView();
        reImg();
        onPageSelected(0);
        initMainViewPager();

    }


    private void initMainViewPager() {
//        mConversationList = initConversationList();
        mFragment.add(new ChatFragment());
        mFragment.add(new ContactFragment());
        mFragment.add(new MineFragment());
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(this);
    }

    //控件ID绑定
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        userAvatar = (CircleImageView) findViewById(R.id.user_head);
        mtab_Message = (RelativeLayout) findViewById(R.id.tab_Message);
        mtab_Contact = (RelativeLayout) findViewById(R.id.tab_Contact);
        mtab_Mine = (RelativeLayout) findViewById(R.id.tab_Mine);

        mImgMessage = (ImageView) findViewById(R.id.id_Message_img);
        mImgContact = (ImageView) findViewById(R.id.id_Contact_img);
        mImgMine = (ImageView) findViewById(R.id.id_Mine_img);

        mTvMessage = (TextView) findViewById(R.id.id_Message_tv);
        mTvContact = (TextView) findViewById(R.id.id_Contact_tv);
        mTvMine = (TextView) findViewById(R.id.id_Mine_tv);

        initButton();

        mtile_tv = (TextView) findViewById(R.id.title_tv);
        mAdd = (ImageView) findViewById(R.id.title_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPopupWindow popupWindow = new MyPopupWindow(HomeActivity.this);
                popupWindow.showPopupWindow(mAdd);

            }
        });

        //获取头像
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
               final Bitmap bitmap = AppUtils.getHttpBitmap(Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser()).getPhoto());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userAvatar.setImageBitmap(bitmap);
                        Log.e("HomeActivity", "run:"+Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser()).getPhoto());
                    }
                });
            }
        });
    }

    //按钮事件
    private void initButton() {
        mtab_Message.setOnClickListener(this);
        mtab_Contact.setOnClickListener(this);
        mtab_Mine.setOnClickListener(this);

        mImgMine.setOnClickListener(this);
        mImgMessage.setOnClickListener(this);
        mImgContact.setOnClickListener(this);


//
    }

    public void onPageSelected(int position) {
        reImg();
        changeSelectedTabState(position);
    }

    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                mTvMessage.setTextColor(Color.parseColor("#0099ff"));
                mImgMessage.setImageResource(R.mipmap.icon_2_d);
                mtile_tv.setText("会话");
                break;
            case 1:
                mTvContact.setTextColor(Color.parseColor("#0099ff"));
                mImgContact.setImageResource(R.mipmap.icon_1_d);
                mtile_tv.setText("联系人");
                break;
            case 2:
                mTvMine.setTextColor(Color.parseColor("#0099ff"));
                mImgMine.setImageResource(R.mipmap.icon_3_d);
                mtile_tv.setText("我");
                break;
        }
    }

    /**
     * 将图片文字设置为暗色
     */
    private void reImg() {
        mImgMessage.setImageResource(R.mipmap.icon_2_n);
        mImgContact.setImageResource(R.mipmap.icon_1_n);
        mImgMine.setImageResource(R.mipmap.icon_3_n);

        mTvMessage.setTextColor(Color.parseColor("#abadbb"));
        mTvContact.setTextColor(Color.parseColor("#abadbb"));
        mTvMine.setTextColor(Color.parseColor("#abadbb"));


    }

    //按钮事件响应
    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.tab_Message:
               mViewPager.setCurrentItem(0, true);
               break;
           case R.id.id_Message_img:
               mViewPager.setCurrentItem(0,true);
               break;
           case R.id.tab_Contact:
               mViewPager.setCurrentItem(1, true);
               break;
           case R.id.id_Contact_img:
               mViewPager.setCurrentItem(1,true);
               break;
           case R.id.tab_Mine:
               mViewPager.setCurrentItem(2, true);
               break;
           case R.id.id_Mine_img:
               mViewPager.setCurrentItem(2,true);
               break;
       }
    }



    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //返回但不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
