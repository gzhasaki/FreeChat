package com.zzz.im.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zzz.im.MyApplication;

/**
 * Created by zzz on 2017/2/14.
 */
public class SpUtils {

    private static SpUtils instance = new SpUtils();
    private static SharedPreferences sp;
    public static String IS_NEW_INVITE="is_new_invite";//新的邀请标记

    private SpUtils() {

    }

    //单例
    public static SpUtils getInstance() {
        if (sp == null) {
            sp = MyApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }

        return instance;
    }

    //保存
    public void save(String key, Object value) {
        if (value instanceof String) {
            sp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).commit();
        }
    }

    //获取数据的方法
    public String getString(String key, String value) {
        return sp.getString(key, value);
    }

    //获取b数据
    public Boolean getboolean(String key, Boolean value) {
        return sp.getBoolean(key, value);
    }

    //获取int数据
    public int getInt(String key, Integer value) {
        return sp.getInt(key, value);
    }
}
