package space.levan.myclass.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Map;

import space.levan.myclass.R;
import space.levan.myclass.utils.NetUtils;

/**
 * Created by 339 on 2016/4/16.
 */
public class MailListActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);

        setTitle("通讯录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

       /* Map<String,String> userInfo = NetUtils.getUserInfo(MailListActivity.this);
        if (userInfo != null) {
            if (userInfo.get("token") != null) {
                String token = userInfo.get("token");
                NetUtils.getMailList(token);
            }
        }*/
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
