package com.zzz.im.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.zzz.im.R;
import com.zzz.im.model.Model;
import com.zzz.im.model.bean.HskUser;
import com.zzz.im.utils.AppUtils;
import com.zzz.im.utils.Constant;
import com.zzz.im.utils.MyDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UpdateInfoActivity extends AppCompatActivity implements View.OnClickListener {


    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/Hasaki/";// sd路径
    private HskUser hskUser;
    private String objectId;

    private RelativeLayout rl_avatar;
    private RelativeLayout rl_nick;
    private RelativeLayout rl_gender;
    private RelativeLayout rl_signature;

    private TextView choose_nick;
    private ImageView choose_avatar;
    private TextView choose_gender;
    private TextView choose_signature;

    private String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
    }

    private void initView() {
        mLBM = LocalBroadcastManager.getInstance(this);

        hskUser = Model.getInstance().getUserDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
        objectId = hskUser.getBmobId();

        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar_background);
        rl_avatar.setOnClickListener(this);

        rl_nick = (RelativeLayout) findViewById(R.id.rl_nick_background);
        rl_nick.setOnClickListener(this);

        rl_gender = (RelativeLayout) findViewById(R.id.rl_gender_background);
        rl_gender.setOnClickListener(this);

        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature_background);
        rl_signature.setOnClickListener(this);

        choose_avatar = (ImageView) findViewById(R.id.choose_avatar);
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap httpBitmap = AppUtils.getHttpBitmap(hskUser.getPhoto());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        choose_avatar.setImageBitmap(httpBitmap);
                    }
                });
            }
        });

        choose_nick = (TextView) findViewById(R.id.choose_nick);
        choose_nick.setText(getIntent().getStringExtra("nick"));

        choose_gender = (TextView) findViewById(R.id.choose_gender);
        if (hskUser.getGender() == null) {
            choose_gender.setText("未填写");
        } else choose_gender.setText(hskUser.getGender());

        choose_signature = (TextView) findViewById(R.id.choose_signature);
        if (hskUser.getSignature() == null) {
            choose_signature.setText("");
        } else choose_signature.setText(hskUser.getSignature());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_avatar_background:// 更换头像
                showTypeDialog();
                break;
            case R.id.rl_nick_background://更换昵称
                if (!AppUtils.checkNetworkAvailable(this)) {
                    Toast.makeText(UpdateInfoActivity.this, "网络出问题了...请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
                }
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString().trim();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UpdateInfoActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateNick(nickString);

                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.rl_gender_background://更换性别
                if (!AppUtils.checkNetworkAvailable(this)) {
                    Toast.makeText(UpdateInfoActivity.this, "网络出问题了...请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (hskUser.getGender() != null) {
                    Toast.makeText(UpdateInfoActivity.this, "性别已设置，无法修改", Toast.LENGTH_SHORT).show();
                    break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateInfoActivity.this);
                builder.setTitle("请选择性别（一旦设置将无法更改）");
                final String[] sex = {"男", "女"};
                //    设置一个单项选择下拉框
                /**
                 * 第一个参数指定我们要显示的一组下拉单选框的数据集合
                 * 第二个参数代表索引，指定默认哪一个单选框被勾选上，-1代表谁都不选
                 * 第三个参数给每一个单选项绑定一个监听器
                 */
                builder.setSingleChoiceItems(sex, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gender = sex[which];
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        updateGender(gender);//开始连接服务器修改性别
                    }
                }).setNegativeButton(R.string.dl_cancel, null).show();
                break;

            case R.id.rl_signature_background://更换个性签名
                if (!AppUtils.checkNetworkAvailable(this)) {
                    Toast.makeText(UpdateInfoActivity.this, "网络出问题了...请稍后再试", Toast.LENGTH_SHORT).show();
                    break;
                }
                final EditText editText2 = new EditText(this);
                editText2.setText(hskUser.getSignature());
                new AlertDialog.Builder(this).setTitle(R.string.setting_signature).setView(editText2)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mSignature = editText2.getText().toString().trim();
                                myDialog = new MyDialog(UpdateInfoActivity.this);
                                updateSignature(mSignature);

                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
        }
    }

    private MyDialog myDialog;

    //性别更新
    private void updateGender(final String gender) {
        hskUser.setValue("gender", gender);
        hskUser.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //更新本页面的性别显示
                    choose_gender.setText(gender);
                    //更新用户的性别
                    hskUser.setGender(gender);
                    //本地数据库更新
                    Model.getInstance().getUserDao().addAccount(hskUser);
                } else {
                    Toast.makeText(UpdateInfoActivity.this, "更新失败" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //签名更新
    private void updateSignature(final String signature) {
        if (signature.equals(hskUser.getSignature())) {
            myDialog.dismiss();
            return;
        }
        hskUser.setValue("signature", signature);
        hskUser.update(objectId, new UpdateListener() {
            @Override
            public void done(final BmobException e) {
                if (e == null) {
                    choose_signature.setText(signature);
                    hskUser.setSignature(signature);
                    //本地数据库更新
                    Model.getInstance().getUserDao().addAccount(hskUser);
                            Toast.makeText(UpdateInfoActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            myDialog.dismiss();
                } else {
                            Toast.makeText(UpdateInfoActivity.this, "更新失败" + e, Toast.LENGTH_LONG).show();
                            if (myDialog != null) myDialog.dismiss();
                        }
            }
        });
    }

    //昵称更新
    private void updateNick(final String nickString) {
        //服务器端更新
        hskUser.setValue("nick", nickString);
        hskUser.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    choose_nick.setText(nickString);
                    hskUser.setNick(nickString);
                    //本地数据库更新
                    Model.getInstance().getUserDao().addAccount(hskUser);
                    //通知MineFragment更新nick信息
                    mLBM.sendBroadcast(new Intent(Constant.My_Info_Changed));
                } else {
                    Toast.makeText(UpdateInfoActivity.this, "更新失败" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_layout, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.choosePhoto);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.takePhoto);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), hskUser.getUsername() + ".jpg")));
                startActivityForResult(intent2, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    private LocalBroadcastManager mLBM;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + hskUser.getUsername() + ".jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        setPicToView(head);// 保存在SD卡中
                        final MyDialog myDialog = new MyDialog(this);
                        //如果有网络就开始服务器端更新，否则Toast
                        if (AppUtils.checkNetworkAvailable(this)) {
                            final String url = hskUser.getPhoto();
                            final String objectId = hskUser.getBmobId();
                            final BmobFile file = new BmobFile(new File(fileName));
                            file.uploadblock(new UploadFileListener() {
                                @Override
                                public void done(BmobException e) {
                                    hskUser.setValue("photo", file.getFileUrl());
                                    hskUser.update(objectId, new UpdateListener() {
                                        @Override
                                        public void done(final BmobException e) {
                                            if (e == null) {
                                                BmobFile deleteAvatar = new BmobFile();
                                                deleteAvatar.setUrl(url);
                                                deleteAvatar.delete(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if (e == null) {
                                                            Log.e("---------", "done: 文件删除成功:" + url);
                                                        } else {
                                                            Log.e("失败了", "done:" + url);
                                                        }
                                                    }
                                                });
                                                choose_avatar.setImageBitmap(head);// 用ImageView显示出来
                                                Toast.makeText(UpdateInfoActivity.this, "头像更新成功", Toast.LENGTH_SHORT).show();
                                                //本地数据库更新
                                                hskUser.setPhoto(file.getFileUrl());
                                                Model.getInstance().getUserDao().addAccount(hskUser);
                                                mLBM.sendBroadcast(new Intent(Constant.My_Info_Changed));
                                                myDialog.dismiss();
                                            } else {
                                                Toast.makeText(UpdateInfoActivity.this, "头像更新失败：" + e, Toast.LENGTH_LONG).show();
                                                myDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(UpdateInfoActivity.this, "网络出现问题了...请稍候再试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);

    }

    private String fileName;

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        fileName = path + hskUser.getUsername() + ".jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void back(View view) {
        finish();
    }

}
