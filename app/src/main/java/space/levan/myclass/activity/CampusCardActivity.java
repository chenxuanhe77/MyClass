package space.levan.myclass.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtil;
import space.levan.myclass.utils.NetUtil;

/**
 * Created by 339 on 2016/4/22.
 */
public class CampusCardActivity extends AppCompatActivity {

    @Bind(R.id.card_name)
    TextView mCardName;
    @Bind(R.id.card_id)
    TextView mCardID;
    @Bind(R.id.card_balance)
    TextView mCardBalance;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;

    private List<HashMap<String, Object>> CardInfos;
    private HashMap<String, Object> CardInfo;
    private ListView mListView;

    private ProgressDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_card);

        ButterKnife.bind(this);

        setTitle("校园卡消费记录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.campus_card_lv);

        final Map<String,String> getToken = InfoUtil.getLoginInfo(CampusCardActivity.this);
        getInfo(getToken.get("StuToken"));


        /**
         * 解决SwipeRefreshLayout与ListView的下拉滑动冲突
         * 只有在当前视图中ListView的firstItemID = 0时
         * 下拉刷新控件才可用
         */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstView = view.getChildAt(firstVisibleItem);
                if (firstVisibleItem == 0&& (firstView ==null || firstView.getTop() == 0)) {
                    mSwipeLayout.setEnabled(true);
                } else {
                    mSwipeLayout.setEnabled(false);
                }
            }
        });

        /**
         * 下拉刷新执行的操作
         */
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo(getToken.get("StuToken"));
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 通过token请求服务器拉取消费记录等详细信息
     * 然后对服务器返回的error字段进行判断
     * error == 0 则属于正常返回，对返回的数据进行解析与适配
     * error ==2 则删除UserData里的所有数据
     * 重启程序并提示重新登录
     * @param mToken
     */
    public void getInfo(final String mToken) {

        mProDialog = ProgressDialog.show(CampusCardActivity.this,"","加载中，请稍候...");

        new Thread() {
            @Override
            public void run() {
                final String result = NetUtil.getCampusCardInfo(mToken);

                if (result != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int error = jsonObject.getInt("error");
                        String message = jsonObject.getString("message");
                        switch (error) {
                            case 0:
                                JSONObject object = jsonObject.getJSONObject("data");
                                JSONObject info = object.getJSONObject("info");
                                final String cardName = info.getString("name");
                                final String cardID = info.getString("cardId");
                                final String cardBalance = info.getString("balance");
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
                                initData(cardName,cardID,cardBalance);
                                break;
                            case 1:
                                setToast(message);
                                break;
                            case 2:
                                setToast("数据异常，请重新登录帐号");
                                InfoUtil.deleteUserInfo(CampusCardActivity.this);
                                final Intent intent = getPackageManager().
                                        getLaunchIntentForPackage(getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    setToast("请求信息失败");
                }
            }
        }.start();
    }

    /**
     * 通过传进来的不同message进行对用户的提示
     * @param message
     */
    public void setToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProDialog.dismiss();
                Toast.makeText(CampusCardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 填充ListView的数据以及上方
     * 姓名，卡号，余额
     * @param cardName
     * @param cardID
     * @param cardBalance
     */
    public void initData(final String cardName,
                         final String cardID,
                         final String cardBalance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleAdapter adapter = new SimpleAdapter(CampusCardActivity.this,
                        CardInfos,R.layout.item_card,
                        new String[]{"Time","Trade"},
                        new int[]{R.id.card_time,R.id.card_trade});
                mCardName.setText(cardName);
                mCardID.setText(cardID);
                mCardBalance.setText(cardBalance);
                mListView.setAdapter(adapter);
                mProDialog.dismiss();
            }
        });
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
