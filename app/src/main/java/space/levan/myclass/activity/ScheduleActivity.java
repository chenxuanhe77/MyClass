package space.levan.myclass.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import space.levan.myclass.R;
import space.levan.myclass.fragment.LessonFragment;
import space.levan.myclass.tool.StatusBarCompat;
import space.levan.myclass.utils.InfoUtil;
import space.levan.myclass.utils.NetUtil;

/**
 * Created by 339 on 2016/5/3.
 */
public class ScheduleActivity extends AppCompatActivity implements MaterialTabListener{
    @Bind(R.id.materialTabHost)
    MaterialTabHost mTabHost;
    @Bind(R.id.pager)
    ViewPager mViewPager;

    ViewPagerAdapter adapter;

    private List<HashMap<String, Object>>[] weekCourses;
    private ProgressDialog mProDialog;
    private String mToken;
    final String[] Weeks =
            {"第1周","第2周","第3周","第4周",
            "第5周","第6周","第7周","第8周",
            "第9周","第10周","第11周","第12周",
            "第13周","第14周","第15周","第16周",
            "第17周","第18周","第19周","第20周",};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        StatusBarCompat.setStatusBarColor(this);

        ButterKnife.bind(this);

        setTitle("课程表");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Map<String,String> weekInfo = InfoUtil.getWeek(ScheduleActivity.this);
        String week = weekInfo.get("StuWeek");
        Map<String, String> loginInfo = InfoUtil.getLoginInfo(ScheduleActivity.this);
        mToken = loginInfo.get("StuToken");
        getLesson(mToken,2,week);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                mTabHost.setSelectedNavigationItem(position);
                if (weekCourses != null) {
                    adapter.update(position, weekCourses[position]);
                }
            }

        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }

        Calendar calendar = Calendar.getInstance();
        int temp = calendar.get(Calendar.DAY_OF_WEEK)-2;
        mViewPager.setCurrentItem(temp);
    }

    public String getLesson(final String mToken, final int Code,final String week) {

        mProDialog = ProgressDialog.show(ScheduleActivity.this,"","加载中，请稍候...");

        new Thread() {
            public void run() {

                String result = NetUtil.getSchedule(mToken,Code,week);

                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int error = jsonObject.getInt("error");
                        String message = jsonObject.getString("message");
                        switch (error) {
                            case 0:
                                getDes(jsonObject);
                                mProDialog.dismiss();
                                break;
                            case 1:
                                mProDialog.dismiss();
                                showToast(message);
                                break;
                            case 2:
                                mProDialog.dismiss();
                                reLogin();
                                break;
                            default:
                                break;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mProDialog.dismiss();
                    showToast("数据异常");
                }
            }
        }.start();

        return null;
    }

    private void getDes(JSONObject jsonObject) {
        try {
            JSONObject Object = jsonObject.getJSONObject("data");
            JSONObject data = Object.getJSONObject("data");
            weekCourses = new List[7];
<<<<<<< HEAD
            for (int i = 1; i < 7; i++) {
                weekCourses[i-1]= new ArrayList<>();
=======
            for (int i = 1; i <= 7; i++) {
                weekCourses[i - 1] = new ArrayList<>();
>>>>>>> refs/remotes/WangZhiYao/master
                JSONObject day = data.getJSONObject(""+i);
                List<HashMap<String, Object>> DayClass = new ArrayList<>();
                for (int n = 1; n <= 5; n++) {
                    JSONArray lesson = day.getJSONArray(""+n);
                    for(int m = 0; m < lesson.length();m++) {
                        JSONObject des = (JSONObject) lesson.get(m);
                        String name = des.getString("course");
                        String teacher = des.getString("teacher");
                        String time = des.getString("time");
                        String room = des.getString("classroom");

                        HashMap<String, Object> ClassInfo = new HashMap<>();
                        ClassInfo.put("Num",n);
                        ClassInfo.put("Name","课程名字：" + name);
                        ClassInfo.put("Teacher","上课老师：" + teacher);
                        ClassInfo.put("Time","上课周次：" + time);
                        ClassInfo.put("Room","上课教室：" + room);

                        DayClass.add(ClassInfo);
                    }
                }
                weekCourses[i-1] = DayClass;
            }

            Log.d("cxy", "getDes: data end");
            for (int i = 0; i < 7; i++) {
                final int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.update(finalI, weekCourses[finalI]);
                    }
                });
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScheduleActivity.this,message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reLogin() {

        InfoUtil.deleteUserInfo(ScheduleActivity.this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Intent intent = getPackageManager().
                        getLaunchIntentForPackage(getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(ScheduleActivity.this,
                        "数据异常，请重新登录帐号",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private LessonFragment[] fragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new LessonFragment[7];
            for (int i = 0; i < 7; i++) {
                fragments[i] = new LessonFragment();
            }
        }

        public Fragment getItem(int num) {
            return fragments[num];
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int temp = position+1;
            return "星期" + temp;
        }

        public void update(int index, List<HashMap<String, Object>> data) {
            fragments[index].update(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0,1,0, R.string.home_week);
        menu.add(0,2,0,R.string.home_total);
        menu.add(0,3,0,R.string.home_choose);
        return true;
    }

    /**
     * 用于界面返回按钮
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                Map<String,String> weekInfo = InfoUtil.getWeek(ScheduleActivity.this);
                String week = weekInfo.get("StuWeek");
                getLesson(mToken,2,week);
                setTitle("课程表" + "    当前周");
                break;
            case 2:
                getLesson(mToken,1,null);
                setTitle("课程表" + "    总课表");
                break;
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
                builder.setTitle("请选择：");
                builder.setItems(Weeks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String week = Integer.toString(which+1);
                        getLesson(mToken,2,week);
                        setTitle("课程表" + "    第" + week+"周");
                    }
                }).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}