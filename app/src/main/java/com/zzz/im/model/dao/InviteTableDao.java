package com.zzz.im.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzz.im.model.bean.HskUser;
import com.zzz.im.model.bean.Invitation;
import com.zzz.im.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzz on 2017/2/14.
 */
public class InviteTableDao {

    private DBHelper mHelper;

    public InviteTableDao(DBHelper helper) {

        mHelper = helper;
    }


    //添加邀请
    public void addInvitation(Invitation invitation) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(InviteTable.COL_REASON, invitation.getReason());
        values.put(InviteTable.COL_STATUS, invitation.getStatus().ordinal());

        HskUser hskUser = invitation.getHskUser();
        if (hskUser != null) {//联系人

            values.put(InviteTable.COL_USER_HXID, invitation.getHskUser().getUsername());
            values.put(InviteTable.COL_USER_NAME, invitation.getHskUser().getNick());
        }
        db.replace(InviteTable.TAB_NAME, null, values);

    }

    //获取所有邀请信息
    public List<Invitation> getInvitation() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "select * from " + InviteTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<Invitation> invitations = new ArrayList<>();

        while (cursor.moveToNext()) {
            Invitation invitation = new Invitation();

            invitation.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invitation.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            HskUser hskUser = new HskUser();

            hskUser.setUsername(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
            hskUser.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));

            invitation.setHskUser(hskUser);
            invitations.add(invitation);
        }
        cursor.close();
        return invitations;

    }

    //将int转换为邀请的状态
    private Invitation.InvatationStatus int2InviteStatus(int intStatus) {
        if (intStatus == Invitation.InvatationStatus.NEW_INVITE.ordinal()) {
            return Invitation.InvatationStatus.NEW_INVITE;
        }
        if (intStatus == Invitation.InvatationStatus.INVITE_ACCEPT.ordinal()) {
            return Invitation.InvatationStatus.INVITE_ACCEPT;
        }
        //INVITE_ACCEPT_BY_PEER
        if (intStatus == Invitation.InvatationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return Invitation.InvatationStatus.INVITE_ACCEPT_BY_PEER;
        }
        if (intStatus == Invitation.InvatationStatus.REJECT_INVITE.ordinal()) {
            return Invitation.InvatationStatus.REJECT_INVITE;
        }
        return null;
    }

    //删除邀请
    public void removeInvitation(String hxid) {
        if (hxid == null) {
            return;
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.delete(InviteTable.TAB_NAME, InviteTable.COL_USER_HXID + "=?", new String[]{hxid});
    }


    //更新邀请状态
    public void updateInvitationStatus(Invitation.InvatationStatus invatationStatus, String hxId) {
        if (hxId == null) {
            return;
        }

        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS, invatationStatus.ordinal());

        db.update(InviteTable.TAB_NAME, values, InviteTable.COL_USER_HXID + "=?", new String[]{hxId});
    }

}
