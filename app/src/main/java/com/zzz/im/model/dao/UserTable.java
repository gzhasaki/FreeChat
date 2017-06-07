package com.zzz.im.model.dao;

/**
 * Created by zzz on 2017/2/14.
 */
public class UserTable {
    public static final String TAB_NAME = "tab_account";
    public static final String COL_BMOBID = "bmobid";
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";
    public static final String COL_SIGNATURE = "signature";
    public static final String COL_GENDER = "gender";


    public static final String CREATE_TABLE = "create table "
            + TAB_NAME + "("
            + COL_HXID + " text primary key,"
            + COL_BMOBID + " text,"
            + COL_NICK + " text,"
            + COL_SIGNATURE + " text,"
            + COL_GENDER + " text,"
            + COL_PHOTO + " text);";
}
