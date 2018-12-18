package cn.forgiveher.smscoder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements SmsResponseCallback, NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_PERMISSIONS_REQUEST = 3000;
    private ArrayList<String> permissionList = new ArrayList<String>();
    private static boolean isActive; //此Activity变量
    public static SQLiteDatabase database;
    private SmsObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.home);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送邮件
                Uri uri = Uri.parse("mailto:"+ "i@forgiveher.cn");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                // intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                // intent.putExtra(Intent.EXTRA_TEXT, "这是邮件的正文部分"); // 正文
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.choice_mail_app)));
                /**Snackbar.make(view, InfoHelper.getAndoirVersion(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

            }
        });

        //Navigation 的初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_main); //导航栏选中

        //安卓7.1及以后版本适配，长按图标弹出快捷方式
        if (Build.VERSION.SDK_INT >= 25){
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id")
                    .setShortLabel(getResources().getString(R.string.open_log))
                    .setLongLabel(getResources().getString(R.string.open_log))
                    .setDisabledMessage("Disabled")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_menu_log_black_24dp))
                    .setIntent(new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, AppActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    //.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://")))
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        }

        //检查权限
        permissionList.add(Manifest.permission.READ_SMS);
        permissionList.add(Manifest.permission.RECEIVE_SMS);
        permissionList.add(Manifest.permission.READ_PHONE_STATE);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.INTERNET);
        checkAndRequestPermissions(permissionList);
        ignoreBatteryOptimization(MainActivity.this);

        //创建数据库
        SQLiteOpenHelper sqLiteOpenHelper = new SqliteHelper(this);
        database = sqLiteOpenHelper.getWritableDatabase(); //调用方法使数据库建立

        //TextView 的初始化
        TextView textView = findViewById(R.id.textView3);
        try {
            textView.setText(textView.getText() + "\n" + InfoHelper.getPhoneInfo(this));
        }catch (Exception e){
            Log.d(getClass().getName(),e.getMessage());
        }

        //短信监控
        smsObserver = new SmsObserver(this, this, null);
        registerReceiver(smsObserver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        //开源协议初始化
        TextView license = findViewById(R.id.textView5);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context mContext = MainActivity.this;
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(R.string.license);
                WebView wv = new WebView(mContext);
                wv.loadUrl("file:///android_asset/License.html");
                alert.setView(wv);
                //alert.setIcon(R.mipmap.ic_launcher);
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        return;
                    }
                });
                alert.show();
            }
        });

        //github
        TextView github = findViewById(R.id.textView6);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://github.com/dirname");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
    }

    @Override
    public void onCallbackSmsContent(String sender,String code) {
        NotificationHelper.NewSmsNotification(this,code);
        submit(sender,code);
        Log.i("smscontent", sender + ":" + code);
    }

    //检查是否授以权限
    private void checkAndRequestPermissions(ArrayList<String> permissionList) {
        ArrayList<String> list = new ArrayList<>(permissionList);
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String permission = it.next();
            //检查权限是否已经申请
            int hasPermission = ContextCompat.checkSelfPermission(this, permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                it.remove();
            }
        }
        if (list.size() == 0) {
            return;
        }
        String[] permissions = list.toArray(new String[0]);
        //正式请求权限
        ActivityCompat.requestPermissions(this, permissions, MainActivity.MY_PERMISSIONS_REQUEST);
    }

    //忽略电池优化
    public void ignoreBatteryOptimization(Activity activity) {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        }
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivity(intent);
        }
    }

    //授权结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MainActivity.MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                int length = grantResults.length;
                boolean re_request = false;//标记位：如果需要重新授予权限，true；反之，false。
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, getResources().getText(R.string.permissioned), Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("smscoder", getResources().getString(R.string.permission_failed) + ":" + permissions[i]);
                        re_request = true;
                    }
                }
                if (re_request) {
                    //弹出对话框，提示用户重新授予权限
                    //关于弹出自定义对话框，可以查看本博文开头的知识扩展
                    final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(this);
                    permissionDialog.setCancelable(false);
                    permissionDialog.setTitle(getResources().getString(R.string.permission_request));
                    permissionDialog.setIcon(R.mipmap.ic_launcher);
                    permissionDialog.setMessage(getResources().getString(R.string.permission_message));
                    permissionDialog.setPositiveButton(getResources().getText(R.string.allow), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            checkAndRequestPermissions(permissionList);
                        }
                    });
                    permissionDialog.setNegativeButton(getResources().getText(R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            database.close();
                            unregisterReceiver(smsObserver);
                            //smsObserver.unregisterSMSObserver();
                            System.exit(0);
                        }
                    });
                    permissionDialog.show();
                }else{
                    //TextView 的初始化
                    TextView textView = findViewById(R.id.textView3);
                    textView.setText(textView.getText() + "\n" + InfoHelper.getPhoneInfo(this));
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsObserver);
        //smsObserver.unregisterSMSObserver();
        Log.i("sms", "over!");
    }

    @Override
    protected void onResume() {
        if (!isActive) {
            //app 从后台唤醒，进入前台
            try {
                NotificationHelper.CancelNotification();
            } catch (Exception e) {
                //错误处理
            }
            isActive = true;
            //Log.i("ACTIVITY", "程序从后台唤醒");
        }
        super.onResume();
    }

    @Override
    protected void onStop() { //activity stop
        if (!InfoHelper.isAppOnForeground(this)) {
            isActive = false;//记录当前已经进入后台
            //Log.i("ACTIVITY", "程序进入后台");
            NotificationHelper.CreateNotification(this);
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0); //返回activity无动画效果(导航栏)
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_log) {
            Intent intent = new Intent(this, AppActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void submit(final String sender, final String code){
        String enstr;
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        //post方式提交的数据
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", sender);
            jsonObject.put("code", code);
            String json = jsonObject.toString();
            enstr = AES.encrypt(json);
        } catch (JSONException e) {
            SqliteHelper.insert(MainActivity.database,sender,code,getResources().getString(R.string.json_error ) + "[" + e.getMessage() + "]");
            return;
        }
        FormBody formBody = new FormBody.Builder()
                .add("data", enstr)
                .add("sign", Client2Server.md5(enstr + "client!!!"))
                .build();
        //Log.i("okhttp3",formBody.toString());
        final Request request = new Request.Builder()
                .url(Client2Server.verify_url)//请求的url
                .post(formBody)
                .build();
        final String[] result = new String[1];
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                result[0] = getResources().getString(R.string.server_error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        String message = jsonObject.getString("message");
                        result[0] = message;
                    } catch (JSONException e) {
                        result[0] = e.getMessage();
                    }
                }
                SqliteHelper.insert(MainActivity.database,sender,code,result[0]);
            }
        });
    }
}
