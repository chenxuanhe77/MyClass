package space.levan.myclass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import space.levan.myclass.R;
import space.levan.myclass.tool.StreamTools;

/**
 * Created by 339 on 2016/4/16.
 */
public class LoginUtils {

    /**
     * 用于登录
     * @param username
     * @param password
     * @return
     */

    public static String loginByGet(String username,String password) {
        try{
            String baseURL = "http://wifi.13550101.com/app/token?username="
                    + URLEncoder.encode(username,"UTF-8")
                    + "&password="
                    + URLEncoder.encode(password,"UTF-8");
            URL url = new URL(baseURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String str = StreamTools.readInputStream(is);
                return str;
            } else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将error code 与 token 保存到学号.xml
     * @param context
     * @param mUserToken
     * @return
     */
    public static boolean saveUserInfo(Context context,String mStuID, String mUserToken) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("stuID",mStuID);
        edit.putString("token",mUserToken);
        edit.commit();
        return true;
    }

    /**
     * 读取信息
     * @param context
     * @return
     */

    public static Map<String,String> getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        String mUserToken = sp.getString("token",null);
        Map<String,String> userMap = new HashMap<String, String>();
        userMap.put("token",mUserToken);
        return userMap;
    }

    public static boolean deleteUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.remove("stuID");
        edit.remove("token");
        edit.commit();
        return true;
    }
}
