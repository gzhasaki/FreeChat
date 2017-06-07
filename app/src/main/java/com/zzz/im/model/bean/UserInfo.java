package com.zzz.im.model.bean;

/**
 * Created by zzz on 2017/2/14.
 */
public class UserInfo {

    private String name;
    private String hxid;
    private String nick;
    private String photo;

    public UserInfo() {
    }

    public UserInfo(String hxid){
        this.hxid = hxid;
        this.name=hxid;
    }

    public UserInfo(String hxid,  String photo,String nick) {
        this.hxid = hxid;
        this.nick = nick;
        this.photo = photo;
    }

    public UserInfo(String name, String hxid, String nick, String photo) {
        this.name = name;
        this.hxid = hxid;
        this.nick = nick;
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getHxid() {
        return hxid;
    }

    public String getNick() {
        return nick;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
