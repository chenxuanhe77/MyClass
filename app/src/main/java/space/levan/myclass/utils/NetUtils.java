package space.levan.myclass.utils;

import android.widget.EditText;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import space.levan.myclass.tool.StreamTools;

/**
 * Created by 339 on 2016/4/16.
 */
public class NetUtils {

    final static String BaseURL = "http://wifi.13550101.com/app/";

    /**
     * 封装网络链接以便重复调用
     * 其他函数调用记得return NetUtils.NetConn(URL);
     * @param URL
     * @return
     */

    public static String NetConn (String URL) {

        try{
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                return StreamTools.readInputStream(is);
            } else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用于获取头像
     * @param URL
     * @return
     * @throws Exception
     */
    public static byte[] getUserAvatar(String URL) throws Exception{

        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inStream = conn.getInputStream();
            return StreamTools.readImageInputStream(inStream);
        } else {
            return null;
        }
    }

    /**
     * 使用GET方式登录
     * @param mUserName
     * @param mPassWord
     * @return
     */
    public static String loginByGet(String mUserName,String mPassWord) {

        try {
            String URL = BaseURL + "token?username="
                    + URLEncoder.encode(mUserName,"UTF-8")
                    + "&password="
                    + URLEncoder.encode(mPassWord,"UTF-8");

            return NetUtils.NetConn(URL);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取通讯录
     * @param mToken
     * @return
     */
    public static String getMailList(String mToken) {

        try {
            String URL = BaseURL + "contacts/list?token=" +
                    URLEncoder.encode(mToken,"UTF-8");

            return NetUtils.NetConn(URL);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取个人信息
     * @param mToken
     * @return
     */
    public static String getUserInfo(String mToken) {

        try {
            String URL = BaseURL + "base_info?token=" +
                    URLEncoder.encode(mToken,"UTF-8");

            return NetUtils.NetConn(URL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过token来修改个人信息
     * @param mToken
     * @param mStuQQ
     * @param mStuTEL
     * @return
     */
    public static String ChangeUserInfo(String mToken,String mStuQQ,String mStuTEL) {

        try {
            String URL = BaseURL + "update_info?token=" +
                    URLEncoder.encode(mToken,"UTF-8") +
                    "&QQ=" + URLEncoder.encode(mStuQQ,"UTF-8") +
                    "&tel=" + URLEncoder.encode(mStuTEL,"UTF-8");

            return NetUtils.NetConn(URL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
