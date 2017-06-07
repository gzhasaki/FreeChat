package com.zzz.im;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.zzz.im.controller.activity.ChatActivity;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.AppUtils;
import com.zzz.im.utils.Constant;

/**
 * Created by zzz on 2017/2/13.
 */
public class MyApplication extends Application {

    private static Context mContext;
    private EaseUI easeUI;

    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        if (EaseUI.getInstance().init(this, options)) {
            easeUI = EaseUI.getInstance();
            easeUI.init(mContext, options);
            setEaseUIProviders();
            setNotificationInfoProvider();
        }
        //初始化Model
        Model.getInstance().init(this);
        //初始化全局上下文
        mContext = this;
    }

    private void setNotificationInfoProvider() {
        EaseUI easeUI = EaseUI.getInstance();

        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
            @Override
            public String getDisplayedText(EMMessage emMessage) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(emMessage, mContext);
                if(emMessage.getType() == EMMessage.Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(emMessage.getFrom());
                if(user != null){
                    return getUserInfo(emMessage.getFrom()).getNick() + ": " + ticker;
                }else{
                    return emMessage.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage emMessage, int i, int i1) {
                    return  i + "个基友，发来了" + i1 + "条消息";
            }

            @Override
            public String getTitle(EMMessage emMessage) {
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage emMessage) {
                return 0;
            }

            @Override
            public Intent getLaunchIntent(EMMessage emMessage) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(mContext, ChatActivity.class);
                EMMessage.ChatType chatType = emMessage.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", emMessage.getFrom());
                    intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                }
                return intent;
            }
        });
    }

    private void setEaseUIProviders() {
        EaseUI easeUI = EaseUI.getInstance();
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username) {
        EaseUser user = null;
        // 从缓存里取昵称和头像
        HskUser myHskUser = null;
        HskUser contactByHx = Model.getInstance().getDbManager().getContactTableDao().getContactByHx(username);
        if (username == EMClient.getInstance().getCurrentUser()) {
            //如果是自己 ，设置自己的头像
            myHskUser = Model.getInstance().getUserDao().getAccountByHxid(username);
            user = new EaseUser(username);
            user.setAvatar(myHskUser.getPhoto());
            user.setNick(myHskUser.getNick());
        } else if (contactByHx != null && contactByHx.getPhoto() == null) {
            AppUtils.getUserFromBmob(username);
        } else if (contactByHx != null && contactByHx.getPhoto() != null) {
            //根据当前登陆用户查询对应联系人表，如果能查到 ，则设置联系人的头像
            user = new EaseUser(username);
            user.setAvatar(contactByHx.getPhoto());
            user.setNick(contactByHx.getNick());
        }
        return user;
    }

    //获取全局上下文
    public static Context getGlobalApplication() {
        return mContext;
    }


}
