package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

//C:\Users\jukyungyoo\AndroidStudioProjects\MyApplication_ruddms_0605_10pm\app\src\test\java\googlemapsapp
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kyungeun on 2016-06-05.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "myalarm.db";
    private static final int DATABASE_VERSION =1;
    SQLiteDatabase db;
    /*
     *먼저 SQLiteOpenHelper클래스를 상속받은 dbHelper클래스가 정의 되어 있다. 데이터베이스 파일 이름은 "mycontacts.db"가되고,
     *데이터베이스 버전은 1로 되어있다. 만약 데이터베이스가 요청되었는데 데이터베이스가 없으면 onCreate()를 호출하여 데이터베이스
     *파일을 생성해준다.
     */

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase(DATABASE_NAME);// DB 초기화
        try {

            db = this.getWritableDatabase();
            //데이터베이스 객체를 얻기 위하여 getWritableDatabse()를 호출

        } catch (SQLiteException e) {
            db = this.getReadableDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE [alarm_table] (" +
                "[no] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "[MtoS] INTEGER  NULL," +
                "[is_AM] BOOLEAN NULL," +
                "[alarm_time] TIME  NULL," +
                "[bell_type] INTEGER  NULL," +
                "[ready_time] INTEGER  NULL," +
                "[arrive_place] TEXT  NULL," +
                "[arrive_lat] REAL  NULL," +
                "[arrive_lon] REAL  NULL," +
                "[start_place] TEXT  NULL," +
                "[start_lat] REAL  NULL," +
                "[start_lon] REAL  NULL," +
                "[result_time] Time NULL" +
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS alarm_table");
        onCreate(db);
    }

    public void insertAlarmTable(Alarm_DTO myaldto){

        String temp_sql = "insert into alarm_table( MtoS ,alarm_time , is_AM , bell_type , ready_time " +
                ", arrive_place, arrive_lat, arrive_lon, start_place, start_lat, start_lon )" +
                " values("+myaldto.getMtoS()+", '"+ myaldto.getAlarm_time()+"'," + myaldto.getIs_AMInt() + "," + myaldto.getBell_type() + ", '"
                + myaldto.getReady_time() + "','"  + myaldto.getArrive_place() + "',"
                + myaldto.getArrive_lat() + "," + myaldto.getArrive_lon() + ",'" + myaldto.getStart_place() + "',"
                + myaldto.getStart_lat() + "," + myaldto.getStart_lon() + ");";
        db.execSQL(temp_sql);
    }

    public void delete_All_AlarmTable( ){
        String temp_sql ="delete from alarm_table;";
        db.execSQL(temp_sql);
    }

    public void delete_selected(int no) {
        String temp_sql = "delete from alarm_table where no="+no+" ;";
        db.execSQL(temp_sql);
    }


    public Cursor getAlarmTableAll(){
        String query = "SELECT * FROM alarm_table;"; // No trailing ';'
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
    public Cursor getTodayAlarmTable(int today){//해당되는 요일에 대한 알람일정불러오기
        String query = "SELECT * FROM alarm_table where ( MtoS & "+ today+") ="+ today+";"; // No trailing ';'
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Alarm_DTO getAlarmTable(int no){
        Alarm_DTO mydto = new Alarm_DTO();
        String query = "SELECT * FROM alarm_table where no =" + no +";"; // No trailing ';'
        Cursor mycursor = db.rawQuery(query, null);
        mycursor.moveToFirst();
        mydto.setNo(mycursor.getInt(mycursor.getColumnIndex("no")));
        mydto.setMtoS(mycursor.getInt(mycursor.getColumnIndex("MtoS")));
        mydto.setIs_AM( mycursor.getInt(mycursor.getColumnIndex("is_AM")));
        mydto.setAlarm_time(mycursor.getString(mycursor.getColumnIndex("alarm_time")));
        mydto.setBell_type(mycursor.getInt(mycursor.getColumnIndex("bell_type")));
        mydto.setReady_time(mycursor.getInt(mycursor.getColumnIndex("ready_time")));
        mydto.setStart_place( mycursor.getString(mycursor.getColumnIndex("start_place")));
        mydto.setStart_lat(mycursor.getString(mycursor.getColumnIndex("start_lat")));
        mydto.setStart_lon(mycursor.getString(mycursor.getColumnIndex("start_lon")));
        mydto.setArrive_place(mycursor.getString(mycursor.getColumnIndex("arrive_place")));
        mydto.setArrive_lat( mycursor.getString(mycursor.getColumnIndex("arrive_lat")));
        mydto.setArrive_lon(mycursor.getString(mycursor.getColumnIndex("arrive_lon")));

        return mydto;
    }

    //",'" <-- 세개넣는거는 ??
    public void update_selected( Alarm_DTO myaldto) {
        String temp_sql = "update alarm_table set MtoS =" +myaldto.getMtoS() +
                ", alarm_time ='" +myaldto.getAlarm_time() + "' , is_AM =" +myaldto.getIs_AMInt() +
                ", bell_type =" +myaldto.getBell_type()+", ready_time =" +myaldto.getReady_time() +
                ", arrive_place ='" + myaldto.getArrive_place() + "', arrive_lat =" + myaldto.getArrive_lat() +
                ", arrive_lon ="+myaldto.getArrive_lon() + ", start_place='" + myaldto.getStart_place() +
                "', start_lat =" +myaldto.getStart_lat() + ", start_lon=" +myaldto.getStart_lon()+
                " where no =" + myaldto.getNo() + ";";
        Log.i("tttt",temp_sql);
        db.execSQL(temp_sql);
    }

    public void update_ResultTime( Alarm_DTO myaldto) {
        String temp_sql = "update alarm_table set result_time ='" +myaldto.getResult_time()+
                "' where no =" + myaldto.getNo() + ";";
        Log.i("tttt","update result time "+temp_sql);
        db.execSQL(temp_sql);
    }

}
