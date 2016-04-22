package space.levan.myclass.view.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    public void onClick() {
        Login();
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

        /**
         * 响应输入法前往按钮
         */
        mPassWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    Login();
                    InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mPassWord.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        Drawable drawable = getResources().getDrawable(R.drawable.user_32px);
        drawable.setBounds(0,0,50,50);
        mUserName.setCompoundDrawables(drawable,null,null,null);

        Drawable drawable1 = getResources().getDrawable(R.drawable.unlocked_32px);
        drawable1.setBounds(0,0,55,55);
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
     * 并对服务器返回数据进行判断
     * 以及对存储登录信息进行判断
     * error == 0 时服务器判定登录成功
     * 则进行对返回数据的存储以便后面调用
     * 如果存储失败则需要重新登录
     * error == 1 时服务器判定登录失败
     * 需要重新登录
     */
    public void Login() {
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
                                        Toast.makeText(LoginActivity.this, "保存登录信息失败", Toast.LENGTH_SHORT).show();
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

    /**
     * 实现再按一次退出提醒
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 3000) {
                Toast.makeText(LoginActivity.this,R.string.home_exit,Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}