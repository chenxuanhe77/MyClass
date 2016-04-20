package space.levan.myclass.view.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_file);
        ButterKnife.bind(this);
        setTitle("个人档案");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(Avatar,0,Avatar.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mStuAvatar.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
