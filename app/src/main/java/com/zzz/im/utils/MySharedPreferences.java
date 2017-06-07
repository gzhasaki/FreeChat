package com.zzz.im.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zzz.im.MyApplication;

/**
 * Created by zzz on 2017/2/14.
 */
public class MySharedPreferences {

    private static MySharedPreferences instance = new MySharedPreferences();
    private static SharedPreferences sp;
    public static String NEW_INVITE="new_invite";//新的邀请标记

    private MySharedPreferences() {

    }

    //单例
    public static MySharedPreferences getInstance() {
        if (sp == null) {
            sp = MyApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }

        return instance;
    }

    //保存
    public void save(String key, Object value) {
       if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).commit();
        }
    }


    //获取b数据
    public Boolean getboolean(String key, Boolean value) {
        return sp.getBoolean(key, value);
    }
}
