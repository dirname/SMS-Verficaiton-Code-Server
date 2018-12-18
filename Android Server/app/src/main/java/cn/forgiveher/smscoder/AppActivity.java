package cn.forgiveher.smscoder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SmsResponseCallback {

    private SmsObserver smsObserver;
    private static boolean isActive; //此Activity变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AppActivity.this);
                builder.setTitle(getResources().getString(R.string.clear_log))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getResources().getString(R.string.sure_clear_log) + SqliteHelper.getLogsCounts(MainActivity.database) + getResources().getString(R.string.sure_clear_notice))
                        .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SqliteHelper.ClearLogs(MainActivity.database);
                                LoadLog(); //刷新日志
                                Snackbar.make(view, getResources().getString(R.string.log_cleared), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                builder.show();
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
        navigationView.setCheckedItem(R.id.nav_log);

        //短信监控
        smsObserver = new SmsObserver(this, this, null);
        registerReceiver(smsObserver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        //载入日志
        LoadLog();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            // Handle the camera action
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCallbackSmsContent(String address, String smsContent) {
        NotificationHelper.NewSmsNotification(this,smsContent);
        submit(address,smsContent);
        //LoadLog(); //位于该activity，刷新日志
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsObserver);
        //smsObserver.unregisterSMSObserver();
        //Log.i("sms", "over!");
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

    public void LoadLog(){
        //获取日志
        ScrollView scrollView = findViewById(R.id.scrollView2);
        scrollView.getScrollY();
        String logs = SqliteHelper.readLogs(MainActivity.database,this);
        int logcounts = SqliteHelper.getLogsCounts(MainActivity.database);
        //TextView 初始化
        TextView logview = findViewById(R.id.textView2);
        TextView counts = findViewById(R.id.textView4);
        counts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        logview.setText(logs);
        counts.setText(getResources().getString(R.string.logs) + String.valueOf(logcounts) + getResources().getString(R.string.strip));
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        //scrollView.scrollTo(0,logview.getMeasuredHeight() - scrollView.getHeight());
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
                handler.sendEmptyMessage(0x123);
            }
        });
    }

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 0x123)
            {
                LoadLog();
            }
        };
    };
}
