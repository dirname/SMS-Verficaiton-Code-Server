package cn.forgiveher.smscoder;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static String key = "83DE52335EC63A54";  //AES 密匙
    private static String iv = "2011121211143000";  //AES 向量
    private static final String CipherMode = "AES/CBC/PKCS7Padding";  //AES 填充方式

    //加密
    public static String encrypt(String data){
        Log.i("AES",data);
        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            byte[] dataBytes = data.getBytes();
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(dataBytes);
            return Base64.encodeToString(encrypted,encrypted.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //解密
    public static String decrypt(String data){
        try {
            byte[] dataBytes = Base64.decode(data,data.length());
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(dataBytes);
            return new String(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
