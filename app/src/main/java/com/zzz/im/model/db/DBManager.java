package com.zzz.im.model.db;

import android.content.Context;

import com.zzz.im.model.Model;
import com.zzz.im.model.dao.ContactTable;
import com.zzz.im.model.dao.ContactTableDao;
import com.zzz.im.model.dao.InviteTable;
import com.zzz.im.model.dao.InviteTableDao;

/**
 * Created by zzz on 2017/2/14.
 *
 * 联系人和邀请信息表的操作类的管理类
 */
public class DBManager {
    private final DBHelper dbHelper;
    private InviteTableDao inviteTableDao;
    private ContactTableDao contactTableDao;

    public DBManager(Context context, String name) {

        //创建数据库
        dbHelper = new DBHelper(context, name);

        contactTableDao = new ContactTableDao(dbHelper);

        inviteTableDao = new InviteTableDao(dbHelper);
    }

    //获取联系人操作类对象
    public ContactTableDao getContactTableDao(){
        return contactTableDao;
    }

    //获取邀请信息操作类对象
    public InviteTableDao getInviteTableDao(){
        return inviteTableDao;
    }

    //关闭数据库的方法
    public void close() {

        dbHelper.close();
    }
}
