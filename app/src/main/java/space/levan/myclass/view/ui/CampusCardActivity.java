package space.levan.myclass.view.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

/**
 * Created by 339 on 2016/4/22.
 */
public class CampusCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_card);

        setTitle("校园卡消费记录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Map<String,String> getToken = InfoUtils.getLoginInfo(CampusCardActivity.this);
        getInfo(getToken.get("StuToken"));
    }

    public void getInfo(final String mToken) {
        new Thread() {
            @Override
            public void run() {
                final String result = NetUtils.getCampusCardInfo(mToken);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i = 0;i < jsonArray.length();i++ ){
                            JSONObject object = (JSONObject) jsonArray.get(i);

                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CampusCardActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}
