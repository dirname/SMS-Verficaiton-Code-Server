package cn.forgiveher.smscoder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    public static NotificationManager mNotificationManager;
    public static int NotificationID = 0;
    public static final String id = "channel_1";
    public static final String name = "sms_notification";

    public static void CancelNotification(){
        if (Build.VERSION.SDK_INT > 25){
            mNotificationManager.deleteNotificationChannel(id);
        }else{
            mNotificationManager.cancel(NotificationID);
        }
    }

    public static void CreateNotification(Activity activity){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(activity.getApplicationContext(), activity.getClass()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent contentIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent
        // 安卓8.0及以后版本适配
        if (Build.VERSION.SDK_INT > 25){
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            channel.setShowBadge(false);
            mNotificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity.getApplicationContext(),id)
                    .setContentIntent(contentIntent)
                    .setContentTitle(activity.getResources().getString(R.string.Monitoring_SMS))//设置通知栏标题
                    .setContentText(activity.getResources().getString(R.string.running_background))
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setAutoCancel(false)
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    //.setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            mNotificationManager.createNotificationChannel(channel);
            NotificationChannel channel1 = mNotificationManager.getNotificationChannel(id);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            //检测通知是否被屏蔽
            if (channel1.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent2 = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent2.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                intent2.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                activity.startActivity(intent2);
                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.notification_notice), Toast.LENGTH_SHORT).show();
            } else{
                mNotificationManager.notify(NotificationID,notification);
            }
        } else{
            mNotificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity.getApplicationContext());
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setContentTitle(activity.getResources().getString(R.string.Monitoring_SMS))//设置通知栏标题
                    .setContentText(activity.getResources().getString(R.string.running_background))
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setAutoCancel(false)
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    //.setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(NotificationID, notification);
        }
    }

    public static void NewSmsNotification(Activity activity,String code) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(activity, activity.getClass()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent contentIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent
        // 安卓8.0及以后版本适配
        if (Build.VERSION.SDK_INT > 25) {
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(false);
            mNotificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity.getApplicationContext(), id)
                    .setContentIntent(contentIntent)
                    .setContentTitle(activity.getResources().getString(R.string.new_sms))//设置通知栏标题
                    .setContentText(code)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setAutoCancel(false)
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    //.setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            mNotificationManager.createNotificationChannel(channel);
            NotificationChannel channel1 = mNotificationManager.getNotificationChannel(id);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            //检测通知是否被屏蔽
            if (channel1.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent2 = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent2.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
                intent2.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                activity.startActivity(intent2);
                Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.notification_notice), Toast.LENGTH_SHORT).show();
            } else{
                mNotificationManager.notify(NotificationID,notification);
            }
        } else{
            mNotificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity.getApplicationContext());
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setContentTitle(activity.getResources().getString(R.string.new_sms))//设置通知栏标题
                    .setContentText(code)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setAutoCancel(false)
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            mNotificationManager.notify(NotificationID, notification);
        }
            /**
             try {
             NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity.getApplicationContext());
             mBuilder.setContentIntent(contentIntent);
             mBuilder.setContentTitle("监测到新的短信")//设置通知栏标题
             .setContentText(code)
             .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
             .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
             .setAutoCancel(false)
             .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
             .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
             .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
             Notification notification = mBuilder.build();
             notification.flags = Notification.FLAG_NO_CLEAR;
             mNotificationManager.notify(NotificationID, notification);
             } catch (Exception e) {
             Log.d("error", e.getMessage().toString()); //异常处理
             } */
    }
}
