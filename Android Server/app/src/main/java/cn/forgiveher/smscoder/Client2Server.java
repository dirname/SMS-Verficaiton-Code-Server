package cn.forgiveher.smscoder;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client2Server extends AppActivity{
    //客户端与服务器通信类
    public static String verify_url = "https://api.forgiveher.cn/verify.php";  //验证短信的服务端地址
    //private static String verify_url = "http://baidu.com";

    //md5 的加密,用来计算提交参数中的sign
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
