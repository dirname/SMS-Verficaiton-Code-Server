package cn.forgiveher.smscoder;

public interface SmsFilter {

    /***
     * 过滤方法
     * @param address 发信人
     * @param smsContent 短信内容
     * @return 过滤处理后的短信信息
     */
    String filter(String address,String smsContent);
}
