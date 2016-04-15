package space.levan.myclass.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import space.levan.myclass.tool.StreamTools;

/**
 * Created by 339 on 2016/4/15.
 */
public class LoginService {
    public static String loginByGet(String username,String password) {
        try{
            String baseURL = "http://wifi.13550101.com/app/token?username="
                    + URLEncoder.encode(username,"UTF-8")
                    + "&password="
                    + URLEncoder.encode(password);
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
}
