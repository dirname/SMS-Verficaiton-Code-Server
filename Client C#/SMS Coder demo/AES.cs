using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace SMS_Coder_demo
{
    class AES
    {
        private static String AesIV = "2011121211143000";
        private static String AesKey = "83DE52335EC63A54";
        public static string Decrypt(string text)
        {
            try
            {
                byte[] encryptedData = Convert.FromBase64String(text);  // strToToHexByte(text);
                RijndaelManaged rijndaelCipher = new RijndaelManaged();
                rijndaelCipher.Key = System.Text.Encoding.Default.GetBytes(AesKey);
                rijndaelCipher.IV = System.Text.Encoding.Default.GetBytes(AesIV);
                rijndaelCipher.Mode = CipherMode.CBC;
                rijndaelCipher.Padding = PaddingMode.PKCS7;
                ICryptoTransform transform = rijndaelCipher.CreateDecryptor();
                byte[] plainText = transform.TransformFinalBlock(encryptedData, 0, encryptedData.Length);
                string result = Encoding.Default.GetString(plainText);
                return result;
            }catch (Exception ex)
            {
                return ex.Message;
            }
        }

        public static String Encrypt(string text)
        {
            try
            {
                byte[] decryptedData = System.Text.Encoding.Default.GetBytes(text);
                RijndaelManaged rijndaelCipher = new RijndaelManaged();
                rijndaelCipher.Key = System.Text.Encoding.Default.GetBytes(AesKey);
                rijndaelCipher.IV = System.Text.Encoding.Default.GetBytes(AesIV);
                rijndaelCipher.Mode = CipherMode.CBC;
                rijndaelCipher.Padding = PaddingMode.PKCS7;
                ICryptoTransform transform = rijndaelCipher.CreateEncryptor();
                byte[] plainText = transform.TransformFinalBlock(decryptedData, 0, decryptedData.Length);
                string result = Convert.ToBase64String(plainText);
                return result;
            }catch (Exception ex)
            {
                return ex.Message;
            }
        }
    }
}
