using Flurl.Http;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SMS_Coder_demo
{
    public partial class Main : Form
    {
        public static Main main = null;
        public Main()
        {
            InitializeComponent();
            main = this;
        }

        private void btn_request_Click(object sender, EventArgs e)
        {
            try
            {
                AES.Decrypt("WQUNM/hBynLgU0vM5mJ9mGElUJ/4G1W444dfMIqgKbZmsKtD62lbdCjb+xe5YrLaJRWYzBw+hlwVDzynFc5QIXwIJpqIRxJGAKa6Yej/zt7/gGAnHA9Ex+APF+VHmrig+rtRnLFiX8SThd7WGB2q+2nOD+LMNljzDYKWTjdQ5Xv7AoJ5KBRBMBqhAbRxQlBx09QNFh5f75FvYjZbgwLhf1XtjUaWvg/lYhqrQThCZ2nTHcrgsBA6A/yHbz75hecQ");
            }catch(Exception e1)
            {
                Console.WriteLine(e1.Message);
            }
            long timestamp = 0;
            String phone = tb_phone.Text;
            String enstr = "";
            if (string.IsNullOrWhiteSpace(phone))
            {
                MessageBoxEx.Show(this,"手机号不能为空 !", "输入错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            else
            {
                TimeSpan ts = DateTime.Now.ToUniversalTime() - new DateTime(1970, 1, 1);
                timestamp = (long)ts.TotalSeconds;
                JObject o = new JObject();
                o["phone"] = tb_phone.Text;
                o["time"] = timestamp;
                enstr = AES.Encrypt(JsonConvert.SerializeObject(o));
                Console.WriteLine(enstr);
                request_codeAsync(enstr);
                this.Hide();
            }
            
        }

        private static async Task request_codeAsync(string param)
        {
            try
            {
                var responseString = await "https://api.forgiveher.cn/request.php"
                .PostUrlEncodedAsync(new {data = param, sign = getMd5(param + "client!!!") })
                .ReceiveString();
                Console.WriteLine(responseString);
                JObject jObject = JObject.Parse(responseString);
                String server_number = (string)jObject["info"]["server"];
                String phone = AES.Decrypt((string)jObject["info"]["phone"]);
                String deadline = (string)jObject["info"]["deadline"];
                String code = (string)jObject["info"]["code"];
                Console.WriteLine(deadline);
                Form form = new Form_Send();
                form.Show();
                Form_Send.code = code;
                Form_Send.phone = phone;
                Form_Send.form.webBrowser1.DocumentText = "<body style='background:#F0F0F0;font-size: 15px'><diy>请您使用手机号码为 <strong style='color: red;font-size: 20px'>" + phone + "</strong></label> 的手机编辑短信 <strong style='color: red;font-size: 20px'>" + code + "</strong> 发送到<strong style='color: red;font-size: 20px'> " + server_number + "</strong></diy></body>";
                Console.WriteLine(responseString);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        public static string getMd5(string source)
        {
            byte[] sor = Encoding.UTF8.GetBytes(source);
            MD5 md5 = MD5.Create();
            byte[] result = md5.ComputeHash(sor);
            StringBuilder strbul = new StringBuilder(40);
            for (int i = 0; i < result.Length; i++)
            {
                strbul.Append(result[i].ToString("x2"));//加密结果"x2"结果为32位,"x3"结果为48位,"x4"结果为64位

            }
            return strbul.ToString();
        }
    }
}
