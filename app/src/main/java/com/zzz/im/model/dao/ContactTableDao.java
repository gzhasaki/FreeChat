package com.zzz.im.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.chat.EMClient;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz on 2017/2/14.
 */
public class ContactTableDao {
    private DBHelper mHelper;

    public ContactTableDao(DBHelper dbHelper) {
        mHelper = dbHelper;
    }

    //获取所有联系人
    public List<HskUser> getContacts() {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_IS_CONTACT + "=1";
        Cursor cursor = db.rawQuery(sql, null);

        List<HskUser> hskUsers = new ArrayList<>();
        while (cursor.moveToNext()) {
            HskUser hskUser = new HskUser();


            hskUser.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COL_HXID)));
            hskUser.setNick(cursor.getString(cursor.getColumnIndex(UserTable.COL_NICK)));
            hskUser.setPhoto(cursor.getString(cursor.getColumnIndex(UserTable.COL_PHOTO)));
            hskUser.setBmobId(cursor.getString(cursor.getColumnIndex(UserTable.COL_BMOBID)));
            hskUser.setSignature(cursor.getString(cursor.getColumnIndex(UserTable.COL_SIGNATURE)));
            hskUser.setGender(cursor.getString(cursor.getColumnIndex(UserTable.COL_GENDER)));
            hskUsers.add(hskUser);
        }
        cursor.close();

        return hskUsers;
    }

    //通过环信id获取单个联系人id
    public HskUser getContactByHx(String hxId) {
        if (hxId == null) {
            return null;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        HskUser hskUser = null;
        if (cursor.moveToNext()) {
            hskUser = new HskUser();

            hskUser.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COL_HXID)));
            hskUser.setNick(cursor.getString(cursor.getColumnIndex(UserTable.COL_NICK)));
            hskUser.setPhoto(cursor.getString(cursor.getColumnIndex(UserTable.COL_PHOTO)));
            hskUser.setBmobId(cursor.getString(cursor.getColumnIndex(UserTable.COL_BMOBID)));
            hskUser.setSignature(cursor.getString(cursor.getColumnIndex(UserTable.COL_SIGNATURE)));
            hskUser.setGender(cursor.getString(cursor.getColumnIndex(UserTable.COL_GENDER)));

        }

        cursor.close();

        return hskUser;
    }


    //通过环信id获取联系人信息
    public List<HskUser> getConTactsByHx(List<String> hxIds) {
        if (hxIds == null || hxIds.size() <= 0) {
            return null;
        }
        List<HskUser> contacts = new ArrayList<>();
        //遍历hxIds来查找
        for (String hxid: hxIds){
            HskUser contact = getContactByHx(hxid);
            contacts.add(contact);
        }
        return contacts;
    }


    //保存单个联系人
    public void saveContact(HskUser hskUser, boolean isMyContact){
        if (hskUser == null ){
            return;
        }
        if (hskUser.getUsername().equals(EMClient.getInstance().getCurrentUser())){
            return;
        }

        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行保存语句
        ContentValues values = new ContentValues();

        values.put(ContactTable.COL_BMOBID, hskUser.getBmobId());
        values.put(ContactTable.COL_HXID, hskUser.getUsername());
        values.put(ContactTable.COL_NICK, hskUser.getNick());
        values.put(ContactTable.COL_PHOTO, hskUser.getPhoto());
        values.put(ContactTable.COL_SIGNATURE, hskUser.getSignature());
        values.put(ContactTable.COL_GENDER, hskUser.getGender());
        values.put(ContactTable.COL_IS_CONTACT,isMyContact ? 1:0);


        db.replace(ContactTable.TAB_NAME,null,values);
    }

    //保存联系人信息
    public void savaContacts(List<HskUser> contacts, boolean isMyContact){
        if (contacts == null || contacts.size()<=0){
            return;
        }

        for (HskUser contact : contacts){

            saveContact(contact,isMyContact);

        }
    }

    //删除联系人信息
    public void deleteContactByHxId(String hxId){
        if (hxId ==null){
            return;
        }
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        db.delete(ContactTable.TAB_NAME,ContactTable.COL_HXID+"=?" ,new String[]{hxId});
    }
}
