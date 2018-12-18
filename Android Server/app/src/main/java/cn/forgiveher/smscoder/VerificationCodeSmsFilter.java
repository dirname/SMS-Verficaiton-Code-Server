package cn.forgiveher.smscoder;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationCodeSmsFilter implements SmsFilter {
    /**
     * 需要过滤的发短信的人
     */
    private String filterAddress;

    public VerificationCodeSmsFilter(String filterAddress) {
        this.filterAddress = filterAddress;
    }

    @Override
    public String filter(String address, String smsContent) {
        if (address.startsWith(filterAddress)) {
            Pattern pattern = Pattern.compile("(\\d{4,8})");//匹配4-8位的数字
            Matcher matcher = pattern.matcher(smsContent);
            Log.i(getClass().getName(),smsContent);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }
}
