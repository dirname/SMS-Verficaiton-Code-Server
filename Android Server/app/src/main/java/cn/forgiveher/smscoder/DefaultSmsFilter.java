package cn.forgiveher.smscoder;

public class DefaultSmsFilter implements SmsFilter {
    //默认短信过滤
    @Override
    public String filter(String address, String smsContent) {
        return smsContent;
    }
}
