package cn.forgiveher.smscoder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SqliteHelper extends SQLiteOpenHelper {

    private static String name = "smslog.db"; //表示数据库的名称
    //private static int version = 1; //表示数据库的版本号
    private static int version = 2; //更新数据库的版本号，此时会执行 onUpgrade()方法

    public SqliteHelper(Context context) {
        super(context, name, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table log(sender varchar(64),content varchar(255),time timestamp(0),result varchar(255))";
        sqLiteDatabase.execSQL(sql); //完成数据库的创建
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void insert(SQLiteDatabase db,String sender,String content,String result){
        //实例化常量值
        ContentValues cValue = new ContentValues();
        cValue.put("sender",sender);
        cValue.put("content",content);
        cValue.put("time",new Date().getTime());
        cValue.put("result",result);
        //调用insert()方法插入数据
        db.insert("log",null,cValue);
    }

    public static String readLogs(SQLiteDatabase db,Context context) {
        String log = "";
        //查询获得游标
        Cursor cursor =  db.rawQuery("select * from log", null);
        while(cursor.moveToNext()){
            String sender = cursor.getString(0);
            String content = cursor.getString(1);
            String timestamp = cursor.getString(2);
            String result = cursor.getString(3);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timestamp = sdf.format(new Date(toLong(timestamp)));
            log = log + "[" + timestamp + "]" + context.getResources().getString(R.string.sender) + sender + context.getResources().getString(R.string.sms_content) + content + " " + result + "\n";
            System.out.println(sender+":"+content+":"+timestamp);
        }
        return log;
    }

    //清空日志
    public static void ClearLogs(SQLiteDatabase db){
        String sql = "delete from log";
        db.execSQL(sql);
    }

    private static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    //获取最后一次写入日志的时间
    public static String getLasttime(SQLiteDatabase db){
        String timestamp = "";
        Cursor cursor =  db.rawQuery("select * from log", null);
        if (cursor.moveToLast()){
            timestamp = cursor.getString(2);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timestamp = sdf.format(new Date(toLong(timestamp)));
        }
        return timestamp;
    }

    //获取日志的数量
    public static int getLogsCounts(SQLiteDatabase db){
        try{
            Cursor cursor =  db.rawQuery("select * from log", null);
            return cursor.getCount();
        }catch (Exception e){
            return 0;
        }
    }
}
