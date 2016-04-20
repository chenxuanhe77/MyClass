package space.levan.myclass.view.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import space.levan.myclass.R;
import space.levan.myclass.utils.InfoUtils;
import space.levan.myclass.utils.NetUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mContainer;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        Map<String, String> loginInfo = InfoUtils.getLoginInfo(MainActivity.this);
        if (loginInfo != null) {
            if (loginInfo.get("StuToken") != null) {
                getStuInfo(loginInfo.get("StuToken"));
                Toast.makeText(MainActivity.this, "登录状态", Toast.LENGTH_SHORT).show();
            } else {
                initIntent(LoginActivity.class);
                this.finish();
            }
        } else {
            initIntent(LoginActivity.class);
            this.finish();
        }
    }

    /**
     * 封装的Intent跳转
     * @param cls
     */
    public void initIntent(Class cls) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,cls);
        startActivity(intent);
    }

    /**
     * 注销登录
     */
    public void SignOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定退出吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                InfoUtils.deleteUserInfo(MainActivity.this);
                initIntent(LoginActivity.class);
                MainActivity.this.finish();
            }
        });
        builder.create();
        AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 通过token发起网络请求获取个人详细信息
     * @param mToken
     */
    public void getStuInfo(final String mToken) {

        View view = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        final CircleImageView mStuAvatar = (CircleImageView)view.findViewById(R.id.nav_avatar);
        final TextView mStuName = (TextView)view.findViewById(R.id.nav_name);
        final TextView mStuID = (TextView)view.findViewById(R.id.nav_id);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null||!networkInfo.isAvailable()) {
            Map<String, String> userInfo = InfoUtils.getUserInfo(MainActivity.this);
            if(userInfo != null) {
                mStuID.setText(userInfo.get("StuID"));
                mStuName.setText(userInfo.get("StuName"));
            } else {
                mStuID.setText("");
                mStuName.setText("");
            }
        } else {
            new Thread() {
                public void run() {
                    final String result = NetUtils.getUserInfo(mToken);
                    if (result != null) {
                        try {
                            JSONTokener jsonTokener = new JSONTokener(result);
                            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                            final String message = jsonObject.getString("message");
                            if (jsonObject.getInt("error") == 0) {
                                JSONObject object = new JSONObject(result).getJSONObject("data");
                                String StuID = object.getString("xuehao");
                                String StuName = object.getString("name");
                                String StuQQ = object.getString("QQ");
                                String StuTEL = object.getString("tel");
                                String StuAvatar = object.getString("avatar");
                                boolean isSaveSuccess = InfoUtils.saveUserInfo(MainActivity.this, StuID,
                                        StuName,StuQQ,StuTEL,StuAvatar);
                                byte[] Avatar = NetUtils.getUserAvatar(StuAvatar);
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(Avatar,0,Avatar.length);
                                if (isSaveSuccess) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Map<String, String> userInfo = InfoUtils.getUserInfo(MainActivity.this);
                                            mStuID.setText(userInfo.get("StuID"));
                                            mStuName.setText(userInfo.get("StuName"));
                                            mStuAvatar.setImageBitmap(bitmap);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,"信息保存失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else if(jsonObject.getInt("error") == 2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,""+message,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"获取信息失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();

        }
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 以下两个函数用于创建右上角按钮以及点击事件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, 1, 0, R.string.home_about);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case 1:
                initIntent(AboutActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧滑栏点击选项
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stuFile) {
            initIntent(StuFileActivity.class);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_mail) {
            initIntent(MailListActivity.class);
        } else if (id == R.id.nav_signOut) {
            SignOut();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
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
                Snackbar.make(mContainer, R.string.home_exit,
                        Snackbar.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}