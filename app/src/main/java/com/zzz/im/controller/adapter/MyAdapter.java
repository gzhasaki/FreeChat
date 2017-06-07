package com.zzz.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zzz.im.R;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.bean.Invitation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz on 2017/2/14.
 */
public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Invitation> mInvitations = new ArrayList<>();
    private InviteListener mInviteListener;
    private Invitation invitation;

    public MyAdapter(Context context, InviteListener inviteListener) {
        mContext = context;
        mInviteListener = inviteListener;
    }

    //刷新数据的方法
    public void refresh(List<Invitation> invationinfos) {
        if (invationinfos != null && invationinfos.size() >= 0) {
            mInvitations.clear();
            mInvitations.addAll(invationinfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitations == null ? 0 : mInvitations.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvitations.get(i);
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
        invitation = mInvitations.get(i);

        //3显示当前数据
        HskUser hskUser = invitation.getHskUser();

        if (hskUser != null) {//联系人
            //名称展示
            holder.name.setText(invitation.getHskUser().getNick());
            //默认关闭同意拒绝按钮显示
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            //原因
            if (invitation.getStatus() == Invitation.InvatationStatus.NEW_INVITE) {//新邀请
                if (invitation.getReason() == null) {
                    holder.reason.setText("添加好友");
                } else {
                    holder.reason.setText(invitation.getReason());
                }
                //设置同意和拒绝按钮为显示状态
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
            } else if (invitation.getStatus() == Invitation.InvatationStatus.INVITE_ACCEPT) {//接收邀请
                if (invitation.getReason() == null) {
                    holder.reason.setText("接收邀请");
                } else {
                    holder.reason.setText(invitation.getReason());
                }
            } else if (invitation.getStatus() == Invitation.InvatationStatus.INVITE_ACCEPT_BY_PEER) {//邀请被接收
                if (invitation.getReason() == null) {
                    holder.reason.setText("邀请被接受");
                } else {
                    holder.reason.setText(invitation.getReason());
                }
            } else if (invitation.getStatus() == Invitation.InvatationStatus.REJECT_INVITE) {//邀请被接收
                if (invitation.getReason() == null) {
                    holder.reason.setText("邀请被拒绝");
                } else {
                    holder.reason.setText(invitation.getReason());
                }
            }
            //按钮处理事件
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInviteListener.onAccept(invitation);
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInviteListener.onReject(invitation);
                }
            });
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

    public interface InviteListener {
        void onAccept(Invitation invitation);

        void onReject(Invitation invitation);

    }
}
