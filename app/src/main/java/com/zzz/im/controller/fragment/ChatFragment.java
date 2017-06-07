package com.zzz.im.controller.fragment;


import android.content.Intent;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.zzz.im.controller.activity.ChatActivity;

import java.util.List;

/**
 * Created by zzz on 2017/2/14.
 */
public class ChatFragment extends EaseConversationListFragment  {


    @Override
    protected void initView() {
        super.initView();

         hideTitleBar();//隐藏父类中的标题栏，使用自己定义的标题栏
        //跳转会话详情界面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation emConversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //传递参数：聊天对象id
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emConversation.conversationId());
                startActivity(intent);
            }
        });
        //低版本模拟器避免会话列表出现相同的会话
        conversationList.clear();

        //监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }


    private EMMessageListener emMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> list) {//用户收到消息

          //设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);
          //刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };



}
