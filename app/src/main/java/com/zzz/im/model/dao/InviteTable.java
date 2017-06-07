package com.zzz.im.model.dao;

/**
 * Created by zzz on 2017/2/14.
 */
public class InviteTable {
    public static final String TAB_NAME = "tab_invite";

    public static final String COL_USER_HXID = "user_hxid";//用户的环信id
    public static final String COL_USER_NAME = "user_name";

    public static final String COL_REASON = "reason";//邀请的理由
    public static final String COL_STATUS = "status";//邀请的状态

    public static final String CREATE_TAB = "create table "
            + TAB_NAME + "("
            + COL_USER_HXID + " text primary key,"
            + COL_USER_NAME + " text,"
            + COL_REASON + " text,"
            + COL_STATUS + " integer);";


}
