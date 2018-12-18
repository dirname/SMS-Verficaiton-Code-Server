package cn.forgiveher.smscoder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsObserver extends BroadcastReceiver {

    private Context mContext;
    public static final int MSG_RECEIVED_CODE = 1001;
    private SmsHandler mHandler;

    /***
     * 构造器
     * @param context
     * @param callback 短信接收器
     * @param smsFilter 短信过滤器
     */


    public SmsObserver(Activity context, SmsResponseCallback callback, SmsFilter smsFilter) {
        this(new SmsHandler(callback,smsFilter));
        this.mContext = context;
    }

    public SmsObserver(Activity context, SmsResponseCallback callback) {
        this(new SmsHandler(callback));
        this.mContext = context;
    }


    public SmsObserver(SmsHandler handler) {
        this.mHandler = handler;
    }

    /***
     * 设置短信过滤器
     * @param smsFilter
     */
    public void setSmsFilter(SmsFilter smsFilter) {
        mHandler.setSmsFilter(smsFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle extras = intent.getExtras();
            Log.e("bundle", extras.toString());
            Object[] pdus = (Object[]) extras.get("pdus");
            for (Object pdu : pdus) {
                SmsMessage mess = SmsMessage.createFromPdu((byte[]) pdu);
                String body = mess.getMessageBody();
                String address = mess.getOriginatingAddress();
                if (mHandler != null) {
                    mHandler.obtainMessage(MSG_RECEIVED_CODE, new String[]{address, body})
                            .sendToTarget();
                }
                Log.i(getClass().getName(), "发件人为：" + address + " " + "短信内容为：" + body);
                // ((MainActivity)context).setText(messageBody);
            }
        }
    }
}

