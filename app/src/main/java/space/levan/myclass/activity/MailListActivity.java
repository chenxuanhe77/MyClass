package space.levan.myclass.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import space.levan.myclass.tool.StatusBarCompat;
import space.levan.myclass.utils.InfoUtil;
import space.levan.myclass.utils.NetUtil;

/**
 * Created by 339 on 2016/4/16.
 */
public class MailListActivity extends AppCompatActivity {

    @Bind(R.id.refreshMail)
    SwipeRefreshLayout mRefreshMail;

    private List<HashMap<String, Object>> MailInfos;
    private HashMap<String, Object> MailInfo;
    private ListView mListView;

    private ProgressDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);

        StatusBarCompat.setStatusBarColor(this);

        ButterKnife.bind(this);
        setTitle("通讯录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.MailInfo_LV);

        FillData();

        mRefreshMail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshMail.setRefreshing(true);
                FillData();
                mRefreshMail.setRefreshing(false);
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> mMap = (HashMap<String,String>)
                        mListView.getItemAtPosition(position);
                String mStuTEL = mMap.get("StuTEL").replace("电话：","").trim();
                String mStuName = mMap.get("StuName").replace("姓名：","").trim();
                if(mStuTEL.equals("")) {
                    Toast.makeText(MailListActivity.this,"该用户还没用设置电话号码",Toast.LENGTH_SHORT).show();
                }else {
                    Call(mStuName,mStuTEL);
                }

            }
        });

    }

    /**
     * 下面开启新线程获取通讯录
     * 通过从服务器返回数据的error字段来判断
     * 如果error == 0 则服务器返回正常
     * 对返回数据进行解析以及适配更新UI
     * 如果error == 2 则本地token异常
     * 删除UserData里的所有信息
     * 并返回登录界面提示重新登录来获取新的token
     */
    public void FillData() {

        mProDialog = ProgressDialog.show(MailListActivity.this,"","加载中，请稍候...");

        new Thread() {

            public void run() {

                Map<String, String> getToken = InfoUtil.getLoginInfo(MailListActivity.this);
                final String mToken = getToken.get("StuToken");
                final String result = NetUtil.getMailList(mToken);

                if(result != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");
                        int error = jsonObject.getInt("error");
                        switch (error) {
                            case 0:
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                MailInfos = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = (JSONObject) jsonArray.get(i);
                                    String StuID = jo.getString("xuehao");
                                    String StuName = jo.getString("name");
                                    String StuQQ = jo.getString("QQ");
                                    String StuTEL = jo.getString("tel");

                                    MailInfo = new HashMap<>();
                                    MailInfo.put("StuID","学号："+StuID);
                                    MailInfo.put("StuName","姓名："+StuName);
                                    MailInfo.put("StuQQ","QQ："+StuQQ);
                                    MailInfo.put("StuTEL","电话："+StuTEL);

                                    MailInfos.add(MailInfo);
                                }
                                initData();
                                break;
                            case 1:
                                setToast(message);
                                break;
                            case 2:
                                setToast(message);
                                InfoUtil.deleteUserInfo(MailListActivity.this);
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
                    setToast("请求失败");
                }
            }
        }.start();
    }

    public void setToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MailListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 填充ListView数据
     */
    public void initData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleAdapter adapter = new SimpleAdapter(MailListActivity.this,
                        MailInfos,R.layout.item_mail,
                        new String[]{"StuID","StuName","StuQQ","StuTEL"},
                        new int[]{R.id.StuID,R.id.StuName,R.id.StuQQ,R.id.StuTEL});
                mListView.setAdapter(adapter);
                mProDialog.dismiss();
            }
        });
    }

    /**
     * 拨打电话，跳转到拨号界面
     * @param mStuName
     * @param mStuTEL
     */
    public void Call(String mStuName,String mStuTEL) {

        final String StuTEL = mStuTEL;

        AlertDialog.Builder builder = new AlertDialog.Builder(MailListActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定拨打" + mStuName +"的电话吗？");
        builder.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + StuTEL));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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