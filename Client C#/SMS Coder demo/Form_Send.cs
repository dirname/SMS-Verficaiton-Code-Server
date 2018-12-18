using Flurl.Http;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SMS_Coder_demo
{
    public partial class Form_Send : Form
    {
        public static Form_Send form;
        public static String code;
        public static String phone;
        public Form_Send()
        {
            InitializeComponent();
            form = this;
        }

        private void previous_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void Form_Send_Load(object sender, EventArgs e)
        {

        }

        private void Form_Send_FormClose(object sender, FormClosedEventArgs e)
        {
            Main.main.Show();
        }

        private async Task verifyAsync(string param)
        {
            var responseString = await "https://api.forgiveher.cn/query.php"
                .PostUrlEncodedAsync(new { data = param, sign = Main.getMd5(param + "client!!!") })
                .ReceiveString();
            JObject jObject = JObject.Parse(responseString);
            String content = (string)jObject["message"];
            int status = (int)jObject["status"];
            DialogResult = MessageBoxEx.Show(this, content, "认证结果",MessageBoxButtons.OK);
            if (status == 0)
            {
                this.Close();
            }
            Console.WriteLine(responseString);
        }

        private void send_Click(object sender, EventArgs e)
        {
            String enstr;
            JObject o = new JObject();
            o["phone"] = phone;
            o["code"] = code;
            enstr = AES.Encrypt(JsonConvert.SerializeObject(o));
            verifyAsync(enstr);
        }
    }
}
