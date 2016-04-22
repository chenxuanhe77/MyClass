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
     * 保存token
     * @param context
     * @param mUserToken
     * @return
     */
    public static boolean saveUserInfo(Context context,String mUserToken) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("StuToken",mUserToken);
        edit.commit();
        return true;
    }

    /**
     * 读取登录的token实现不注销就免登录
     * @param context
     * @return
     */
    public static Map<String,String> getLoginInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        String mUserToken = sp.getString("StuToken",null);
        Map<String,String> loginMap = new HashMap<>();
        loginMap.put("StuToken",mUserToken);
        return loginMap;
    }

    /**
     * 注销登录
     * @param context
     * @return
     */
    public static boolean deleteUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("StuToken");
        edit.remove("StuID");
        edit.remove("StuName");
        edit.remove("StuQQ");
        edit.remove("StuTEL");
        edit.remove("StuAvatar");
        edit.commit();
        return true;
    }

    /**
     * 重载saveUserInfo用于保存个人档案
     * @param context
     * @param mStuID
     * @param mStuName
     * @param mStuQQ
     * @param mStuTEL
     * @param mStuAvatar
     * @return
     */

    public static boolean saveUserInfo(Context context,String mStuID,
                                       String mStuName,String mStuQQ,
                                       String mStuTEL,String mStuAvatar) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("StuID",mStuID);
        edit.putString("StuName",mStuName);
        edit.putString("StuQQ",mStuQQ);
        edit.putString("StuTEL",mStuTEL);
        edit.putString("StuAvatar",mStuAvatar);
        edit.commit();
        return true;
    }

    /*public static boolean UpdateUserInfo(Context context,String mStuQQ,String mStuTEL) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("StuQQ",mStuQQ);
        edit.putString("StuTEL",mStuTEL);
        edit.commit();
        return true;
    }*/

    /**
     * 用于读取个人档案
     * @param context
     * @return
     */
    public static Map<String,String> getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        String mStuID = sp.getString("StuID",null);
        String mStuName = sp.getString("StuName",null);
        String mStuQQ = sp.getString("StuQQ",null);
        String mStuTEL = sp.getString("StuTEL",null);
        String mStuAvatar = sp.getString("StuAvatar",null);
        Map<String,String> userMap = new HashMap<>();
        userMap.put("StuID",mStuID);
        userMap.put("StuName",mStuName);
        userMap.put("StuQQ",mStuQQ);
        userMap.put("StuTEL",mStuTEL);
        userMap.put("StuAvatar",mStuAvatar);
        return userMap;
    }
}