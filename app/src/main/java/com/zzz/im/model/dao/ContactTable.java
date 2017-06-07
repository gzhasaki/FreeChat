package com.zzz.im.model.dao;

/**
 * Created by zzz on 2017/2/14.
 * <p/>
 * 联系人的建表语句
 */
public class ContactTable {

    public static final String TAB_NAME = "tab_contact";

    public static final String COL_BMOBID ="bmobid" ;
    public static final String COL_HXID = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";
    public static final String COL_SIGNATURE = "signature";
    public static final String COL_GENDER = "gender";

    public static final String COL_IS_CONTACT = "is_contact";//是否是联系人

    public static final String CREATE_TAB = "create table "
            + TAB_NAME + "("
            + COL_HXID + " text primary key,"
            + COL_BMOBID + " text,"
            + COL_NICK + " text,"
            + COL_PHOTO + " text,"
            + COL_SIGNATURE + " text,"
            + COL_GENDER + " text,"
            + COL_IS_CONTACT + " integer);";


}
