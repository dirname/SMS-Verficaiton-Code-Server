<?php
/**
 * Created by PhpStorm.
 * User: Administrator
 * Date: 2018/6/11
 * Time: 16:29
 */

class Aes
{
    public static function encrypt($encryptStr){
        $localIV = "2011121211143000";		//密钥偏移量IV
        $encryptKey = $encryptKey = "83DE52335EC63A54";		//AESkey
        $data = openssl_encrypt($encryptStr, 'AES-128-CBC', $encryptKey, OPENSSL_RAW_DATA,$localIV);
        $data = base64_encode($data);
        return $data;
    }

    public static function decrypt($decryptStr){
        $localIV = "2011121211143000";		//密钥偏移量IV
        $encryptKey = $encryptKey = "83DE52335EC63A54";		//AESkey
        $decrypted = openssl_decrypt(base64_decode($decryptStr), 'AES-128-CBC', $encryptKey, OPENSSL_RAW_DATA,$localIV);
        return $decrypted;
    }
}