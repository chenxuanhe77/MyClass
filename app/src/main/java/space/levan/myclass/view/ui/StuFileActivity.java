package space.levan.myclass.view.ui;

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

import java.util.Map;
import java.util.StringTokenizer;

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

    @OnClick({R.id.stu_QQ, R.id.stu_TEL})
    public void onClick(View view) {
        String str;
        switch (view.getId()) {
            case R.id.stu_QQ:
                str = mStuQQ.getText().toString().replaceAll("[^QQ]", "");
                ChangeInfo(str);
                break;
            case R.id.stu_TEL:
                str = mStuTEL.getText().toString().replaceAll("[^电话]", "");
                ChangeInfo(str);
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

    public void initInfo() {

        final Map<String, String> userInfo = InfoUtils.getUserInfo(StuFileActivity.this);
        mStuId.setText("学号：" + userInfo.get("StuID"));
        mStuName.setText("姓名：" + userInfo.get("StuName"));
        mStuQQ.setText("QQ：" + userInfo.get("StuQQ"));
        mStuTEL.setText("电话：" + userInfo.get("StuTEL"));

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
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void ChangeInfo(final String str) {

        final String QQ = "QQ";
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.dialog_change, null);
        final EditText editText = (EditText) view.findViewById(R.id.change_info);
        AlertDialog.Builder builder = new AlertDialog.Builder(StuFileActivity.this);
        builder.setTitle("更改" + str);
        builder.setView(view);
        builder.setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String Info = editText.getText().toString().trim();
                final String QQ = mStuQQ.getText().toString();
                final String TEL = mStuTEL.getText().toString();
                if (Info.isEmpty())
                {
                    Toast.makeText(StuFileActivity.this,"修改的信息不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if (str.equals(QQ)) {
                        new Thread() {
                            @Override
                            public void run() {
                                Map<String, String> getInfo = InfoUtils.getLoginInfo(StuFileActivity.this);
                                String mToken = getInfo.get("StuToken");
                                final String result = NetUtils.ChangeUserInfo(mToken,Info,TEL);
                                if (result != null) {

                                }

                            }
                        }.start();
                        mStuQQ.setText("QQ：" + Info);
                    } else {
                        mStuTEL.setText("电话：" + Info);
                    }
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
