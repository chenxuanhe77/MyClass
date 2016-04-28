package space.levan.myclass.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtil;
import space.levan.myclass.utils.NetUtil;

/**
 * Created by 339 on 2016/4/19.
 */
public class StuFileActivity extends AppCompatActivity {

    @Bind(R.id.stu_avatar)
    CircleImageView mStuAvatar;
    @Bind(R.id.stu_id)
    TextView mStuId;
    @Bind(R.id.stu_name)
    TextView mStuName;
    @Bind(R.id.stu_QQ)
    TextView mStuQQ;
    @Bind(R.id.stu_TEL)
    TextView mStuTEL;

    private static final int TAG_QQ = 1;
    private static final int TAG_TEL = 2;
    private ProgressDialog mProDialog;
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    /**
     * 校验手机号
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 传递TAG来判断用户点的是修改QQ还是修改TEL
     * @param view
     */
    @OnClick({R.id.change_QQ, R.id.change_TEL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_QQ:
                ChangeInfo(TAG_QQ);
                break;
            case R.id.change_TEL:
                ChangeInfo(TAG_TEL);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_file);
        ButterKnife.bind(this);
        setTitle("个人信息");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initInfo();
    }

    /**
     * 用于界面返回按钮
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 从本地获取姓名，QQ，TEL，ID
     * 从网络获取头像
     */
    public void initInfo() {

        final Map<String, String> userInfo = InfoUtil.getUserInfo(StuFileActivity.this);
        mStuId.setText(userInfo.get("StuID"));
        mStuName.setText(userInfo.get("StuName"));
        mStuQQ.setText(userInfo.get("StuQQ"));
        mStuTEL.setText(userInfo.get("StuTEL"));

        /**
         * 开启新线程用于获取头像
         */
        new Thread() {
            @Override
            public void run() {
                try {
                    byte[] Avatar = NetUtil.getUserAvatar(userInfo.get("StuAvatar"));
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(Avatar, 0, Avatar.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStuAvatar.setImageBitmap(bitmap);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StuFileActivity.this,"请求头像失败，请重新打开App",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    /**
     * 根据tag的不同来判断是修改QQ还是修改TEL
     * @param tag
     */
    public void ChangeInfo(final int tag) {

        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_change, null);
        final EditText editText = (EditText) view.findViewById(R.id.change_info);

        AlertDialog.Builder builder = new AlertDialog.Builder(StuFileActivity.this);
        if(tag == 1){
            builder.setTitle("修改QQ");
        }else {
            builder.setTitle("修改电话");
        }
        builder.setView(view);
        builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String Info = editText.getText().toString().trim();
                final String QQ = mStuQQ.getText().toString();
                final String TEL = mStuTEL.getText().toString();

                if (Info.isEmpty()) {

                    Toast.makeText(StuFileActivity.this,"修改的信息不能为空",Toast.LENGTH_SHORT).show();

                }else {
                    new Thread() {
                        @Override
                        public void run() {
                            Map<String, String> getInfo = InfoUtil.getLoginInfo(StuFileActivity.this);
                            String mToken = getInfo.get("StuToken");
                            //获取token用来发起请求修改数据
                            if (tag == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProDialog = ProgressDialog.show(StuFileActivity.this,"","加载中，请稍候...");
                                    }
                                });
                                final String result = NetUtil.changeUserInfo(mToken,Info,TEL);
                                if (result != null) {
                                    getResult(result,tag,Info);
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProDialog.dismiss();
                                            Toast.makeText(StuFileActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else if (tag == 2){
                                /**
                                 * 这里判断是否为手机号
                                 */
                                if (isMobile(Info)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProDialog = ProgressDialog.show(StuFileActivity.this,"","加载中，请稍候...");
                                        }
                                    });
                                    final String result = NetUtil.changeUserInfo(mToken,QQ,Info);
                                    if (result != null) {
                                        getResult(result,tag,Info);
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProDialog.dismiss();
                                                Toast.makeText(StuFileActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(StuFileActivity.this,
                                                    "请输入正确的手机号",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        /**
                         * 用来获取修改信息是否成功
                         * 如果修改成功，则根据tag来直接保存输入的数据
                         * 如果服务器返回的error == 2则删除本地所有数据
                         * 重启程序提示重新登录
                         * @param result
                         * @param tag
                         * @param str
                         */
                        public void getResult(String result,final int tag, final String str) {

                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                final String message = jsonObject.getString("message");
                                if (jsonObject.getInt("error") == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(StuFileActivity.this,message,Toast.LENGTH_SHORT).show();
                                            boolean isSaveSuccess = InfoUtil.updateUserInfo(StuFileActivity.this,tag,str);
                                            if(isSaveSuccess) {
                                                initInfo();
                                                mProDialog.dismiss();
                                            }else {
                                                Toast.makeText(StuFileActivity.this,"更新数据保存失败",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else if(jsonObject.getInt("error") == 2) {
                                    InfoUtil.deleteUserInfo(StuFileActivity.this);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProDialog.dismiss();
                                            final Intent intent = getPackageManager().
                                                    getLaunchIntentForPackage(getPackageName());
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            Toast.makeText(StuFileActivity.this,
                                                    "数据异常，请重新登录帐号",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
        builder.create();
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setTextColor(getResources().getColor(R.color.colorPrimary));
    }


}
