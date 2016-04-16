package space.levan.myclass.utils;

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

   /* public static String NetConn (String mURL) {
        try{
            URL url = new URL(mURL);
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
    }*/

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

            URL Url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
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
           // NetConn(URL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMailList(String mToken) {
        try {
            String URL = BaseURL + "contacts/list?token=" +
                    URLEncoder.encode(mToken,"UTF-8");
            //NetConn(URL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
