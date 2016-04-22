package space.levan.myclass.view.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hrules.charter.CharterLine;
import com.hrules.charter.CharterXLabels;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

/**
 * Created by 339 on 2016/4/22.
 */
public class CampusCardActivity extends AppCompatActivity {

    private List<HashMap<String, Object>> CardInfos;
    private HashMap<String, Object> CardInfo;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_card);

        setTitle("校园卡消费记录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.campus_card_lv);

        Map<String,String> getToken = InfoUtils.getLoginInfo(CampusCardActivity.this);
        getInfo(getToken.get("StuToken"));

    }

    /**
     * 通过token请求服务器拉取消费记录等详细信息
     * 但此处只显示时间与消费金额
     * @param mToken
     */
    public void getInfo(final String mToken) {
        new Thread() {
            @Override
            public void run() {
                final String result = NetUtils.getCampusCardInfo(mToken);
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt("error") == 0) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("data");
                            CardInfos = new ArrayList<>();
                            for (int i = 0;i < jsonArray.length();i++ ){
                                JSONObject jo = (JSONObject) jsonArray.get(i);
                                String Time = jo.getString("time");
                                String Trade = jo.getString("trade");

                                CardInfo = new HashMap<>();
                                CardInfo.put("Time","时间：" + Time);
                                CardInfo.put("Trade","消费金额：" + Trade);

                                CardInfos.add(CardInfo);
                            }
                        }else if(jsonObject.getInt("error") == 1 || jsonObject.getInt("error") == 2) {
                            Toast.makeText(CampusCardActivity.this, "" + jsonObject.get("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleAdapter adapter = new SimpleAdapter(CampusCardActivity.this,
                                    CardInfos,R.layout.item_card,
                                    new String[]{"Time","Trade"},
                                    new int[]{R.id.card_time,R.id.card_trade});
                            mListView.setAdapter(adapter);
                        }
                    });

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

    /**
     * 用于界面返回按钮
     *
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
