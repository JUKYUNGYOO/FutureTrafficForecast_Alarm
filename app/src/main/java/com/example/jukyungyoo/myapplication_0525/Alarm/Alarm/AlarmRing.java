package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.jukyungyoo.myapplication_0525.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kyungeun on 2016-06-05.
 */
public class AlarmRing extends Activity {

    AlarmManager alarm_manager;
    private TextView mDate, mTime;
    private final Handler mDateTimeHandler = new Handler();
    PendingIntent pending_intent;
    Calendar mcalendar = Calendar.getInstance();
    Date mcurMills;
    int mcurYear;
    int mcurMonth;
    int mcurDay;
    int mcurHour;
    int mcurNoon;
    int mintDay;
    int choose_whale_sound;
    int mcurMinute;
    int mcurSecond;

    String mNoon;
    String mstrDate;
    String mstrTime;
    String mstrDay;
    TextView update_text;
    Context context;


    MediaPlayer media_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_ring);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        context = this;
        alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mDate = (TextView) findViewById(R.id.date);
        mTime = (TextView) findViewById(R.id.time);
        final Intent my_intent = new Intent(this, AlarmReceiver.class);
        displayCameraInformation();
        // initialize the stop button
        final Button alarm_off = (Button) findViewById(R.id.stop_Btn);
        alarm_off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
          Intent my_intent = new Intent(AlarmRing.this, AlarmReceiver.class);
                my_intent.putExtra("extra", "alarm off");
                media_song.stop();
             sendBroadcast(my_intent);
                        finish();

            }
        });
        Intent intent = getIntent();
        String state = intent.getExtras().getString("extra");
        // fetch the whale choice integer values
        Integer whale_sound_choice = intent.getExtras().getInt("whale_choice");

        Log.e("Ringtone extra is ", state);
        Log.e("Whale choice is ", whale_sound_choice.toString());
       // if (!this.isRunning && startId == 1) {
            Log.e("there is no music, ", "and you want start");

            // set up the start command for the notification
            //notify_manager.notify(0, notification_popup);


        if (whale_sound_choice == 0) {
            media_song = MediaPlayer.create(this, R.raw.nice);
            media_song.start();
        }
        else if (whale_sound_choice == 2) {
            // create an instance of the media player
            media_song = MediaPlayer.create(this, R.raw.nice);
            // start the ringtone
            media_song.start();
        }
        else {
            media_song = MediaPlayer.create(this, R.raw.nice);
            media_song.start();
        }





    //    }

    }


    private void set_alarm_text(String output) {
        update_text.setText(output);
    }
    public void displayCameraInformation() {
        displayCurrentTime();
    }

    private void displayCurrentTime() {
        TimerTask CurrentTimeTask = new TimerTask() {
            private String tag;

            public void run() {
                Update();
            }
        };

        Timer displayTimer = new Timer();
        displayTimer.schedule(CurrentTimeTask, 0, 1000);
    }

    protected void Update() {
        mcalendar = Calendar.getInstance();
        mcurMills = mcalendar.getTime();
        mcurYear = mcalendar.get(Calendar.YEAR);
        mcurMonth = mcalendar.get(Calendar.MONTH) + 1;
        mcurDay = mcalendar.get(Calendar.DAY_OF_MONTH);
        mcurHour = mcalendar.get(Calendar.HOUR_OF_DAY);
        mcurNoon = mcalendar.get(Calendar.AM_PM);

        mcurMinute = mcalendar.get(Calendar.MINUTE);
        mcurSecond = mcalendar.get(Calendar.SECOND);

        if (mcurNoon == 0) {
            mNoon = "오전";
        } else {
            mNoon = "오후";
            mcurHour -= 12;
        }

        if (mintDay == 1) {
            mstrDay = "일요일";
        } else if (mintDay == 2) {
            mstrDay = "월요일";
        } else if (mintDay == 3) {
            mstrDay = "화요일";
        } else if (mintDay == 4) {
            mstrDay = "수요일";
        } else if (mintDay == 5) {
            mstrDay = "목요일";
        } else if (mintDay == 6) {
            mstrDay = "금요일";
        } else {
            mstrDay = "토요일";
        }

        Runnable updater = new Runnable() {
            public void run() {
                if ((mcurMonth < 10) && (mcurDay < 10)) {
                    mstrDate =  "0" + mcurMonth + "월 " + "0" + mcurDay + "일 " + mstrDay;
                } else if ((mcurMonth < 10) && (mcurDay >= 10)) {
                    mstrDate =  "0" + mcurMonth + "월 " + mcurDay + "일 " + mstrDay;
                } else if ((mcurMonth >= 10) && (mcurDay < 10)) {
                    mstrDate =  mcurMonth + "월 " + "0" + mcurDay + "일 " +mstrDay;
                } else {
                    mstrDate =  mcurMonth + "월 " + mcurDay + "일 " + mstrDay;
                }

                if ((mcurHour < 10) && (mcurMinute < 10) && (mcurSecond < 10)) {
                    mstrTime = mNoon + " " + "0" + mcurHour + ":" + "0" + mcurMinute ;
                } else if ((mcurHour < 10) && (mcurMinute < 10) && (mcurSecond >= 10)) {
                    mstrTime = mNoon + " " + "0" + mcurHour + ":" + "0" + mcurMinute ;
                } else if ((mcurHour < 10) && (mcurMinute >= 10) && (mcurSecond < 10)) {
                    mstrTime = mNoon + " " + "0" + mcurHour + ":" + mcurMinute ;
                } else if ((mcurHour < 10) && (mcurMinute >= 10) && (mcurSecond >= 10)) {
                    mstrTime = mNoon + " " + "0" + mcurHour + ":" + mcurMinute ;
                } else if ((mcurHour >= 10) && (mcurMinute < 10) && (mcurSecond < 10)) {
                    mstrTime = mNoon + " " + mcurHour + ":" + "0" + mcurMinute ;
                } else if ((mcurHour >= 10) && (mcurMinute < 10) && (mcurSecond >= 10)) {
                    mstrTime = mNoon + " " + mcurHour + ":" + "0" + mcurMinute ;
                } else if ((mcurHour >= 10) && (mcurMinute >= 10) && (mcurSecond < 10)) {
                    mstrTime = mNoon + " " + mcurHour + ":" + mcurMinute ;
                } else {
                    mstrTime = mNoon + " " + mcurHour + ":" + mcurMinute ;
                }

                mDate.setText(mstrDate);
                mTime.setText(mstrTime);
            }
        };

        mDateTimeHandler.post(updater);
    }
}
