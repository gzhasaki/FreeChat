package com.zzz.im.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.db.UserDB;

/**
 * Created by zzz on 2017/2/14.
 */
public class UserDao {

    private final UserDB mHelper;

    public UserDao(Context context) {
        mHelper = new UserDB(context);
    }



    //添加用户到数据库
    public void addAccount(HskUser hskUser) {
        SQLiteDatabase db = mHelper.getReadableDatabase();


        ContentValues values = new ContentValues();
        values.put(UserTable.COL_BMOBID, hskUser.getBmobId());
        values.put(UserTable.COL_HXID, hskUser.getUsername());
        values.put(UserTable.COL_NICK, hskUser.getNick());
        values.put(UserTable.COL_PHOTO, hskUser.getPhoto());
        values.put(UserTable.COL_SIGNATURE, hskUser.getSignature());
        values.put(UserTable.COL_GENDER, hskUser.getGender());

        //执行添加操作
        db.replace(UserTable.TAB_NAME, null, values);
    }

    //根据环信id获取所有用户信息
    public HskUser getAccountByHxid(String hxId) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询
        String sql = "select * from " + UserTable.TAB_NAME + " where " + UserTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        HskUser hskUser = null;
        if (cursor.moveToNext()) {
            hskUser = new HskUser();
            hskUser.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COL_HXID)));
            hskUser.setBmobId(cursor.getString(cursor.getColumnIndex(UserTable.COL_BMOBID)));
            hskUser.setNick(cursor.getString(cursor.getColumnIndex(UserTable.COL_NICK)));
            hskUser.setPhoto(cursor.getString(cursor.getColumnIndex(UserTable.COL_PHOTO)));
            hskUser.setSignature(cursor.getString(cursor.getColumnIndex(UserTable.COL_SIGNATURE)));
            hskUser.setGender(cursor.getString(cursor.getColumnIndex(UserTable.COL_GENDER)));

        }
        //关闭资源
        cursor.close();
        //返回数据
        return hskUser;
    }
}
