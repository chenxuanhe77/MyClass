package space.levan.myclass.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 339 on 2016/4/16.
 */
public class InfoUtils {

    /**
     * 保存error code 与 token
     * @param context
     * @param mStuID
     * @param mUserToken
     * @return
     */
    public static boolean saveUserInfo(Context context, String mStuID, String mUserToken) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
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
        Map<String,String> userMap = new HashMap<String,String>();
        userMap.put("token",mUserToken);
        return userMap;
    }

    /**
     * 退出登录
     * @param context
     * @return
     */
    public static boolean deleteUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("stuID");
        edit.remove("token");
        edit.commit();
        return true;
    }
}
