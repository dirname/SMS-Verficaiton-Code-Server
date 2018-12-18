package cn.forgiveher.smscoder;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsHandler extends Handler {

    private SmsResponseCallback mCallback;

    /***
     * 短信过滤器
     */
    private SmsFilter smsFilter;

    public SmsHandler(SmsResponseCallback callback) {
        this.mCallback = callback;
    }

    public SmsHandler(SmsResponseCallback callback, SmsFilter smsFilter) {
        this(callback);
        this.smsFilter = smsFilter;
    }

    /***
     * 设置短信过滤器
     * @param smsFilter 短信过滤器
     */
    public void setSmsFilter(SmsFilter smsFilter) {
        this.smsFilter = smsFilter;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == SmsObserver.MSG_RECEIVED_CODE) {
            String[] smsInfos = (String[]) msg.obj;
            if (smsInfos != null && smsInfos.length == 2 && mCallback != null) {
                if (smsFilter == null) {
                    smsFilter = new DefaultSmsFilter();
                }
                /**
                 * 阿里小号取真实手机和号码
                 */
                Pattern pattern = Pattern.compile("0?(13|14|15|17|18|19|16)[0-9]{9}");
                Matcher matcher = pattern.matcher(smsInfos[1]);
                if (matcher.find()){
                    Log.i(getClass().getName(),matcher.group(0));
                    smsInfos[0] = matcher.group(0);
                }
                pattern = Pattern.compile("\\(The message is from 0?(13|14|15|17|18|19|16)[0-9]{9}\\)");
                matcher = pattern.matcher(smsInfos[1]);
                if (matcher.find()){
                    smsInfos[1] = matcher.replaceAll("");
                }
                Log.i(getClass().getName(),smsInfos[0]+smsInfos[1]);
                mCallback.onCallbackSmsContent(smsInfos[0], smsInfos[1]);
            }
        }
    }
}

