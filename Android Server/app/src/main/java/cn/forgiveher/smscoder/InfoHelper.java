package cn.forgiveher.smscoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.List;

public class InfoHelper {
    //获取设备信息的类

    //获取短信数量
    public static int getSmsCounts(Context context) {
        Uri SMS_INBOX = Uri.parse("content://sms/");
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        int counts = 0;
        if (null == cur) {
            Log.i("ooc", "************cur == null");
            return counts;
        }
        while (cur.moveToNext()) {
            counts = cur.getCount();
        }
        return counts;
    }

    //获取手机号码
    public static String getNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String phoneNumber = tm.getLine1Number();
        return phoneNumber;
    }

    //获取安卓版本号
    public static String getAndoirVersion(){
        return Build.VERSION.RELEASE;
    }

    //判断 应用状态 前台还是后台
    public static boolean isAppOnForeground(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = activity.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    //获取手机型号
    public static String getBrand(){
        return android.os.Build.BRAND + " " + Build.MODEL;
    }

    //获取版本号
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    //获取版本信息
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


    //信息总汇
    public static String getPhoneInfo(Context context){
        String info = context.getResources().getString(R.string.app_version) + getLocalVersionName(context) + context.getResources().getString(R.string.Version_Code) + getLocalVersion(context) + context.getResources().getString(R.string.Model) + getBrand() + context.getResources().getString(R.string.phone_number) + getNumber(context) + context.getResources().getString(R.string.android_version) + getAndoirVersion() + context.getResources().getString(R.string.sms_counts) + getSmsCounts(context) + context.getResources().getString(R.string.last_intercept) + SqliteHelper.getLasttime(MainActivity.database);
        return info;
    }
}
