package com.zzz.im.model.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by zzz on 2017/2/14.
 */
public class HskUser extends BmobObject{

    private String bmobId;
    //bmob 的username 对应环信id
    private String username;
    //密码
    private String password;
    //用户昵称
    private String nick;
    //用户头像
    private String photo;
    //用户个性签名
    private String signature;
    //用户性别
    private String gender;

    public HskUser() {
    }

    public HskUser(String username) {
        this.nick = username;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getBmobId() {
        return bmobId;
    }

    public void setBmobId(String bmobId) {
        this.bmobId = bmobId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "HskUser{" +
                "bmobId='" + bmobId + '\'' +
                ", username='" + username + '\'' +
                ", nick='" + nick + '\'' +
                ", signature='" + signature + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
