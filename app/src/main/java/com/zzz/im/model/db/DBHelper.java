package com.zzz.im.model.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zzz.im.model.dao.ContactTable;
import com.zzz.im.model.dao.InviteTable;

/**
 * Created by zzz on 2017/2/14.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context,String name) {
        super(context, name, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建联系人表
        db.execSQL(ContactTable.CREATE_TAB);

        //创建邀请信息表
        db.execSQL(InviteTable.CREATE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
