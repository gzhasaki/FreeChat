package com.zzz.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.zzz.im.R;
import com.zzz.im.controller.activity.InviteActivity;
import com.zzz.im.controller.activity.UserProfileActivity;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.Constant;
import com.zzz.im.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/12/10.
 */

public class ContactFragment extends EaseContactListFragment {

    private String mHxid;
    private ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private LinearLayout contact_ll_add;
    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //刷新联系人列表
            refreshContacts();
        }
    };
    private BroadcastReceiver ContactInviteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            MySharedPreferences.getInstance().save(MySharedPreferences.NEW_INVITE, true);
        }
    };
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshContacts();
        }
    };

    @Override
    protected void initView() {
        super.initView();
        hideTitleBar();
        //新增头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contacat, null);
        listView.addHeaderView(headerView);
        //首次进入HomeActivity时，延迟刷新联系人列表，将查询到的联系人的头像昵称显示出来
        handler.sendMessageDelayed(Message.obtain(), 3500);
        //获取红点对象
        iv_contact_red = (ImageView) headerView.findViewById(R.id.iv_contact_red);
        //获取邀请信息条目的对象
        contact_ll_add = (LinearLayout) headerView.findViewById(R.id.contact_ll_add);
        contact_ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新红点显示----有变无
                iv_contact_red.setVisibility(View.GONE);
                MySharedPreferences.getInstance().save(MySharedPreferences.NEW_INVITE, false);
                //跳转页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser easeUser) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                //传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, easeUser.getUsername());
                startActivity(intent);
            }
        });
    }


    //业务逻辑执行的方法
    @Override
    protected void setUpView() {
        super.setUpView();

        //初始化红点的显示
        Boolean isNewInvite = MySharedPreferences.getInstance().getboolean(MySharedPreferences.NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        //从环信服务器获取所有联系人信息
        getContactFromHxServer();

        //绑定listview和contextmenu
        registerForContextMenu(listView);
    }

    /**
     * 删除联系人
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //获取环信id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();

        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_delete) {
            //删除联系人
            deleteContact();
        }
        return super.onContextItemSelected(item);
    }

    //删除联系人
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    //将该好友从本地数据库中删除
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除成功" + mHxid, Toast.LENGTH_SHORT).show();
                            refreshContacts();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() == null) {
                        return;
                    }
                    //刷新页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除失败" + mHxid, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Log.e("从环信获取联系人个数", "size"+hxids.size() );
                    if (hxids != null && hxids.size() >= 0) {
                        List<HskUser> contacts = new ArrayList<HskUser>();
                        for (String hxid : hxids) {
                            //根据该id去本地数据库查询该好友信息
                            HskUser contactByHx = Model.getInstance().getDbManager().getContactTableDao().getContactByHx(hxid);
                            //本地数据库查不到该好友信息，则保存该好友信息到数据库
                            if (contactByHx == null) {
                                HskUser hskUser = new HskUser(hxid);
                                contacts.add(hskUser);
                                Log.e("正在从环信服务器获取联系人", "run: " + hskUser);
                            } else {
                                //如果查到该好友，则将该好友数据取出来放至contacts中
                                contacts.add(contactByHx);
                                Log.e("正在从本地服务器获取联系人", "run: " + contactByHx);
                            }

                        }
                        if (getActivity() == null) {
                            return;
                        }
                        //保存好友信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().savaContacts(contacts, true);
                        //刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContacts();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshContacts() {
        //\
        List<HskUser> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        if (contacts != null && contacts.size() >= 0) {
            //设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();
            setContactsMap(contactsMap);

            //转换
            for (HskUser contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getUsername());
                contactsMap.put(contact.getUsername(), easeUser);
            }

            refresh();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
    }
}

