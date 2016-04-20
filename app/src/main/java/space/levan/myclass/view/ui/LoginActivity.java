package space.levan.myclass.view.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

/**
 * Created by 339 on 2016/4/15.
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.sign_in_button)
    Button mButton;
    @OnClick(R.id.sign_in_button)
    public void onClick(View view) {
        Login(view);
    }

    private EditText mUserName;
    private EditText mPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //setTitle("登录");

        mUserName = (EditText) findViewById(R.id.user_name);
        mPassWord = (EditText) findViewById(R.id.password);

        Drawable drawable = getResources().getDrawable(R.drawable.user_32px);
        drawable.setBounds(0,0,50,50);
        mUserName.setCompoundDrawables(drawable,null,null,null);

        Drawable drawable1 = getResources().getDrawable(R.drawable.unlocked_32px);
        drawable1.setBounds(0,0,50,50);
        mPassWord.setCompoundDrawables(drawable1,null,null,null);


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
        mButton.setClickable(false);
        mButton.setText("Loading...");
        final String username = mUserName.getText().toString().trim();
        final String password = mPassWord.getText().toString().trim();

        new Thread() {
            public void run() {
                final String result = NetUtils.loginByGet(username, password);
                if (result != null) {
                    try {
                        JSONTokener jsonTokener = new JSONTokener(result);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        final String message = jsonObject.getString("message");
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
                                        mButton.setClickable(true);
                                        mButton.setText("登录");
                                    }
                                });
                            }
                        } else if(jsonObject.getInt("error") == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "" + message,
                                            Toast.LENGTH_SHORT).show();
                                    mButton.setClickable(true);
                                    mButton.setText("登录");
                                }
                            });
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                            mButton.setClickable(true);
                            mButton.setText("登录");
                        }
                    });
                }
            }
        }.start();
    }
}