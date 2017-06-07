package com.zzz.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zzz.im.R;
import com.zzz.im.model.bean.InvationInfo;
import com.zzz.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz on 2017/2/14.
 */
public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvationInfo> mInvationinfo = new ArrayList<>();
    private OnInvtListenr mOnInvtListenr;
    private InvationInfo invationInfo;

    public InviteAdapter(Context context, OnInvtListenr onInvtListenr) {
        mContext = context;
        mOnInvtListenr = onInvtListenr;
    }

    //刷新数据的方法
    public void refresh(List<InvationInfo> invationinfos) {
        if (invationinfos != null && invationinfos.size() >= 0) {
            mInvationinfo.clear();
            mInvationinfo.addAll(invationinfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvationinfo == null ? 0 : mInvationinfo.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvationinfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //1获取或创建一个ViewHolder
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();

            view = View.inflate(mContext, R.layout.item_invite, null);

            holder.name = (TextView) view.findViewById(R.id.tv_invite_name);
            holder.reason = (TextView) view.findViewById(R.id.tv_invite_reason);
            holder.accept = (Button) view.findViewById(R.id.btn_accept);
            holder.reject = (Button) view.findViewById(R.id.btn_reject);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //2获取当前数据

        invationInfo = mInvationinfo.get(i);

        //3显示当前数据
        UserInfo userInfo = invationInfo.getUserInfo();

        if (userInfo != null) {//联系人
            //名称展示
            holder.name.setText(invationInfo.getUserInfo().getName());
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);


            //原因
            if (invationInfo.getStatus() == InvationInfo.InvatationStatus.NEW_INVITE) {//新邀请

                if (invationInfo.getReason() == null) {
                    holder.reason.setText("添加好友");
                } else {
                    holder.reason.setText(invationInfo.getReason());
                }
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);

            } else if (invationInfo.getStatus() == InvationInfo.InvatationStatus.INVITE_ACCEPT) {//接收邀请
                if (invationInfo.getReason() == null) {
                    holder.reason.setText("接收邀请");
                } else {
                    holder.reason.setText(invationInfo.getReason());
                }
            } else if (invationInfo.getStatus() == InvationInfo.InvatationStatus.INVITE_ACCEPT_BY_PEER) {//邀请被接收
                if (invationInfo.getReason() == null) {
                    holder.reason.setText("邀请被接受");
                } else {
                    holder.reason.setText(invationInfo.getReason());
                }
            }

            //按钮处理事件
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnInvtListenr.onAccept(invationInfo);
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnInvtListenr.onReject(invationInfo);
                }
            });

        } else {//群组

        }

        //4返回view
        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView reason;

        private Button accept;
        private Button reject;
    }

    public interface OnInvtListenr {
        void onAccept(InvationInfo invationInfo);

        void onReject(InvationInfo invationInfo);

    }
}
