package space.levan.myclass.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import org.json.JSONTokener;

import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

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

    @OnClick({R.id.stu_QQ, R.id.stu_TEL})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stu_QQ:
                ChangeInfo(TAG_QQ);
                break;
            case R.id.stu_TEL:
                ChangeInfo(TAG_TEL);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_file);
        ButterKnife.bind(this);
        setTitle("个人档案");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initInfo();
    }

    /**
     * 从本地获取姓名，QQ，TEL，ID
     * 从网络获取头像
     */
    public void initInfo() {

        mProDialog = ProgressDialog.show(StuFileActivity.this,"","加载中，请稍候...");

        final Map<String, String> userInfo = InfoUtils.getUserInfo(StuFileActivity.this);
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
                    byte[] Avatar = NetUtils.getUserAvatar(userInfo.get("StuAvatar"));
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(Avatar, 0, Avatar.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStuAvatar.setImageBitmap(bitmap);
                            mProDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
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
        builder.setTitle("修改信息");
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
                            Map<String, String> getInfo = InfoUtils.getLoginInfo(StuFileActivity.this);
                            String mToken = getInfo.get("StuToken");
                            if (tag == 1) {
                                final String result = NetUtils.changeUserInfo(mToken,Info,TEL);
                                getResult(result);
                            } else if (tag == 2){
                                /**
                                 * 这里判断是否为手机号
                                 */
                                if (isMobile(Info)) {
                                    final String result = NetUtils.changeUserInfo(mToken,QQ,Info);
                                    getResult(result);
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(StuFileActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        /**
                         * 用来提示是否修改成功
                         * @param result
                         */
                        public void getResult(String result) {
                            if (result != null) {
                                try {
                                    JSONTokener jsonTokener = new JSONTokener(result);
                                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                                    final String message = jsonObject.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(StuFileActivity.this,message+"再次打开APP即可显示修改后的信息",Toast.LENGTH_SHORT).show();
                                            initInfo();
                                        }
                                    });
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    /**
     * 用于界面返回按钮
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
