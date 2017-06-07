package com.zzz.im.model.bean;

/**
 * Created by zzz on 2017/2/14.
 */
public class Invitation {
    private HskUser hskUser;

    private String reason;

    private InvatationStatus status;//邀请的状态

    public Invitation() {
    }


    public HskUser getHskUser() {
        return hskUser;
    }

    public void setHskUser(HskUser hskUser) {
        this.hskUser = hskUser;
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


    public enum InvatationStatus {
        INVITE_ACCEPT,//接收邀请
        INVITE_ACCEPT_BY_PEER,
        NEW_INVITE,//新邀请
        REJECT_INVITE

    }

    @Override
    public String toString() {
        return "Invitation{" +
                "hskUser=" + hskUser +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}
