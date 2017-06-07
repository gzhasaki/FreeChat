package com.zzz.im.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.bean.Invitation;
import com.zzz.im.utils.Constant;
import com.zzz.im.utils.MySharedPreferences;

/**
 * Created by zzz on 2017/2/14.
 */
public class EventListener {

    private Context mContext;
    private LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        EMClient.getInstance().contactManager().setContactListener(mContactListener);
    }

    public final EMContactListener mContactListener = new EMContactListener() {

        //联系人增加执行的方法
        @Override
        public void onContactAdded(String hxid) {
            //数据库更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new HskUser(hxid), true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //联系人删除的方法
        @Override
        public void onContactDeleted(String hxid) {
            //数据库更新 从对应的联系人表中删除该用户。并且从邀请信息表中删除该邀请
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //接收联系人的新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            //是否已经是好友
            if (Model.getInstance().getDbManager().getContactTableDao().getContactByHx(hxid) != null){
                return;
            }
            //数据库更新
            Invitation invitation = new Invitation();
            invitation.setHskUser(new HskUser(hxid));
            invitation.setReason(reason);
            invitation.setStatus(Invitation.InvatationStatus.NEW_INVITE);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            MySharedPreferences.getInstance().save(MySharedPreferences.NEW_INVITE, true);
            //发送信息广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
        //别人同意了你的邀请
        @Override
        public void onFriendRequestAccepted(String hxid) {
            //数据库更新
            Invitation invitation = new Invitation();
            invitation.setHskUser(new HskUser(hxid));
            invitation.setStatus(Invitation.InvatationStatus.INVITE_ACCEPT_BY_PEER);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            MySharedPreferences.getInstance().save(MySharedPreferences.NEW_INVITE, true);
            //发送信息广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
        //别人拒绝了你的邀请
        @Override
        public void onFriendRequestDeclined(String hxid) {
            //数据库更新
            Invitation invitation = new Invitation();
            invitation.setHskUser(new HskUser(hxid));
            invitation.setStatus(Invitation.InvatationStatus.REJECT_INVITE);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            MySharedPreferences.getInstance().save(MySharedPreferences.NEW_INVITE, true);
            //发送信息广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
    };
}
