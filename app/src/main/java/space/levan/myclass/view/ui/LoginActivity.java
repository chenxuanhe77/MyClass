package space.levan.myclass.view.ui;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import space.levan.myclass.R;
import space.levan.myclass.Service.LoginService;

/**
 * Created by 339 on 2016/4/15.
 */
public class LoginActivity extends AppCompatActivity{

    private AutoCompleteTextView mUserName;
    private EditText mPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登录");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mUserName = (AutoCompleteTextView) findViewById(R.id.user_name);
        mPassWord = (EditText) findViewById(R.id.password);

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

    /**
     * 开启新线程用于登录
     * @param view
     */

    public void Login(View view) {
        final String username = mUserName.getText().toString().trim();
        final String password = mPassWord.getText().toString().trim();

        new Thread() {
            public void run() {
                final String result = LoginService.loginByGet(username,password);
                if (result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONTokener jsonTokener = new JSONTokener(result);
                                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                                if (jsonObject.getInt("error") ==0 ) {
                                    Toast.makeText(LoginActivity.this,"ERROR:"+jsonObject.getInt("error")
                                            + "\nTOKEN:"+jsonObject.getString("token"),Toast.LENGTH_SHORT).show();
                                }else if (jsonObject.getInt("error") == 1) {
                                    Toast.makeText(LoginActivity.this,""+jsonObject.get("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e) {

                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}
