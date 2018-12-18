namespace SMS_Coder_demo
{
    partial class Main
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.btn_request = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.tb_phone = new System.Windows.Forms.TextBox();
            this.SuspendLayout();
            // 
            // btn_request
            // 
            this.btn_request.Location = new System.Drawing.Point(169, 77);
            this.btn_request.Name = "btn_request";
            this.btn_request.Size = new System.Drawing.Size(75, 23);
            this.btn_request.TabIndex = 0;
            this.btn_request.Text = "下一步";
            this.btn_request.UseVisualStyleBackColor = true;
            this.btn_request.Click += new System.EventHandler(this.btn_request_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(11, 29);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(77, 12);
            this.label1.TabIndex = 1;
            this.label1.Text = "您的手机号 :";
            // 
            // tb_phone
            // 
            this.tb_phone.HideSelection = false;
            this.tb_phone.Location = new System.Drawing.Point(90, 26);
            this.tb_phone.Name = "tb_phone";
            this.tb_phone.Size = new System.Drawing.Size(154, 21);
            this.tb_phone.TabIndex = 2;
            this.tb_phone.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            // 
            // Main
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(262, 116);
            this.Controls.Add(this.tb_phone);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btn_request);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "Main";
            this.Text = "Demo";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btn_request;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox tb_phone;
    }
}

