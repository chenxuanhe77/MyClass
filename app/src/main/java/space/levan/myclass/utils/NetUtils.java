package space.levan.myclass.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
    final static String testURL = "http://wifi.13550101.com/assets/i/userhead.png";
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
}
