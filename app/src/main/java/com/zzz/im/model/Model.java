package com.zzz.im.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.dao.UserDao;
import com.zzz.im.model.db.DBManager;
import com.zzz.im.model.db.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zzz on 2017/2/14.
 * 数据模型层全局类
 */
public class Model {

    private Context mContext;
    private UserDao userDao;
    private DBManager dbManager;

    private static Model model = new Model();

    private Model(){

    }
    private ExecutorService executors = Executors.newCachedThreadPool();

    //单例模式
    public static Model getInstance(){
        return model;
    }

    //初始化方法
    public  void init(Context context){
        mContext=context;

        //创建用户帐号数据库操作类的对象
         userDao = new UserDao(mContext);

        PreferenceManager.init(context);
    }

    //获取全局的线程池对象。
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }

    //用户登陆成功之后的操作
    public void loginSuccess(HskUser account) {
        if (account==null){
            return;
        }

        if (dbManager !=null){
            dbManager.close();
        }

        dbManager = new DBManager(mContext, account.getUsername());
    }

    public DBManager getDbManager(){
        return dbManager;
    }

    //获取用户帐号数据库操作类对象
    public UserDao getUserDao(){
        return userDao;
    }


    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    private static SharedPreferences mSharedPreferences;
    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn
    }
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null?val:true);
    }
}
