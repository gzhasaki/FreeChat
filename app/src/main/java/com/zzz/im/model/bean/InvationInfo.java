package com.zzz.im.model.bean;

/**
 * Created by zzz on 2017/2/14.
 */
public class InvationInfo {
    private UserInfo userInfo;
    private GroupInfo groupInfo;

    private String reason;

    private InvatationStatus status;//邀请的状态

    public InvationInfo() {
    }

    public InvationInfo(UserInfo userInfo, InvatationStatus status, String reason, GroupInfo groupInfo) {
        this.userInfo = userInfo;
        this.status = status;
        this.reason = reason;
        this.groupInfo = groupInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public InvatationStatus getStatus() {
        return status;
    }

    public void setStatus(InvatationStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public enum InvatationStatus {
        INVITE_ACCEPT,//接收邀请
        NEW_GROUP_INVITE,
        NEW_GROUP_APPLICATION,
        GROUP_INVITE_ACCEPTED,
        GROUP_APPLICATION_ACCEPTED,
        GROUP_INVITE_DECLINED,
        GROUP_APPLICATION_DECLINED,
        GROUP_ACCEPT_INVITE,
        GROUP_ACCEPT_APPLICATION,
        GROUP_REJECT_APPLICATION,
        GROUP_REJECT_INVITE,
        INVITE_ACCEPT_BY_PEER, NEW_INVITE//新邀请

    }

    @Override
    public String toString() {
        return "InvationInfo{" +
                "userInfo=" + userInfo +
                ", groupInfo=" + groupInfo +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}
