package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.database.sqlite.SQLiteDatabase;

import com.example.jukyungyoo.myapplication_0525.R;


public class MainActivity extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // SQLITE DB 생성 부분
       helper = new DBHelper(this);
        db = helper.getWritableDatabase();
       //helper.onUpgrade(db,0,1);
       //Intent service_intent = new Intent(this BackAlarmManagerService.class);
       Intent service_intent = new Intent(this.getApplication(),BackAlarmManagerService.class);
       // start the ringtone service
       startService(service_intent);

        final ImageButton move_alarm = (ImageButton)findViewById(R.id.imgBtn_alarm);
        move_alarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myIntent = new Intent(MainActivity.this, AlarmList.class);
                startActivity(myIntent);
            }
        });

    }
}




