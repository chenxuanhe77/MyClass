package space.levan.myclass.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.ButterKnife;
import butterknife.OnClick;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

/**
 * Created by 339 on 2016/4/15.
 */
public class LoginActivity extends AppCompatActivity {

    @OnClick(R.id.sign_in_button)
    public void onClick(View view) {
        Login(view);
    }

    private AutoCompleteTextView mUserName;
    private EditText mPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle("登录");

        mUserName = (AutoCompleteTextView) findViewById(R.id.user_name);
        mPassWord = (EditText) findViewById(R.id.password);

    }

    /**
     * 封装的Intent
     * @param cls
     */
    public void initIntent(Class cls) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,cls);
        startActivity(intent);
    }

    /**
     * 开启新线程用于登录
     * @param view
     */

    public void Login(View view) {
        final String username = mUserName.getText().toString().trim();
        final String password = mPassWord.getText().toString().trim();

        new Thread() {
            public void run() {
                final String result = NetUtils.loginByGet(username, password);
                if (result != null) {
                    try {
                        JSONTokener jsonTokener = new JSONTokener(result);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        if (jsonObject.getInt("error") == 0) {
                            String token = jsonObject.getString("token");
                            boolean isSaveSuccess = InfoUtils.saveUserInfo(LoginActivity.this,token);
                            if (isSaveSuccess) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        initIntent(MainActivity.class);
                                        finish();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else if(jsonObject.getInt("error") == 1) {
                            Toast.makeText(LoginActivity.this, "" + jsonObject.get("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}